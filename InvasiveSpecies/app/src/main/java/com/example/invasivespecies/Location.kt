package com.example.invasivespecies

import android.location.Location

class Location {
    var id: String? = null
    var lat: Double? = null
    var lng: Double? = null


    internal constructor(
        id: String?,
        lat: Double?,
        lng: Double?,
    ) {
        this.id = id
        this.lat = lat
        this.lng = lng
    }
}