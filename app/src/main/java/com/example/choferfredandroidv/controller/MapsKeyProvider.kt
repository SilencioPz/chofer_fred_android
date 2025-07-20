package com.example.choferfredandroidv.controller

interface MapsKeyProvider {
    val mapsApiKey: String
}

class MapsKeyProviderImpl(
    override val mapsApiKey: String
) : MapsKeyProvider