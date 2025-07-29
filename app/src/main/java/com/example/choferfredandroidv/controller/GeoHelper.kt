package com.example.choferfredandroidv.controller

import android.content.Context
import android.util.Log
import com.example.choferfredandroidv.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GeoHelper(private val context: Context) {
    private val placesClient: PlacesClient by lazy {
        try {
            if (!Places.isInitialized()) {
                Places.initialize(context.applicationContext, BuildConfig.MAPS_API_KEY)
            }
            Places.createClient(context)
        } catch (e: Exception) {
            Log.e("GeoHelper", "Erro ao inicializar Places API", e)
            Places.initialize(context.applicationContext, BuildConfig.MAPS_API_KEY)
            Places.createClient(context)
        }
    }

    private val geoApiContext: GeoApiContext by lazy {
        GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()
    }

    suspend fun getLatLngFromAddress(address: String): LatLng? = withContext(Dispatchers.IO) {
        if (address.isBlank()) return@withContext null

        try {
            val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                .setQuery(address)
                .build()

            val predictionsResponse =
                placesClient.findAutocompletePredictions(predictionsRequest).await()

            val prediction: AutocompletePrediction =
                predictionsResponse.autocompletePredictions.firstOrNull()
                    ?: return@withContext null

            val placeRequest = FetchPlaceRequest.builder(
                prediction.placeId,
                listOf(Place.Field.LAT_LNG)
            ).build()

            val placeResponse = placesClient.fetchPlace(placeRequest).await()

            return@withContext placeResponse.place.latLng?.also {
                Log.d("GeoHelper", "Coordenadas encontradas: $it para '$address'")
            }
        } catch (e: Exception) {
            Log.e("GeoHelper", "Erro ao geocodificar endereço: ${e.message}")
            null
        }
    }

    suspend fun getDirections(origin: LatLng, destination: LatLng): List<LatLng>? =
        withContext(Dispatchers.IO) {
            try {
                val result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(
                        com.google.maps.model.LatLng(
                            destination.latitude,
                            destination.longitude
                        )
                    )
                    .await()

                return@withContext result.routes.firstOrNull()?.let { route ->
                    PolyUtil.decode(route.overviewPolyline.encodedPath).map {
                        LatLng(it.latitude, it.longitude)
                    }
                }
            } catch (e: Exception) {
                Log.e("GeoHelper", "Erro ao buscar direções: ${e.message}")
                null
            }
        }
}