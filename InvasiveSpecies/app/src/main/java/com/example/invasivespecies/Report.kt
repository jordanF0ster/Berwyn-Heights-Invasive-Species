package com.example.invasivespecies

import android.location.Location
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Report {
    var id: String? = null
    var plantname: String? = null
    var color: String? = null
    var amount: String? = null
    var notes: String? = null
    var location: Location? = null

    internal constructor(
        id: String?,
        plantname: String,
        color: String,
        amount: String,
        notes: String,
        location: Location?
    ) {
        this.id = id
        this.plantname = plantname
        this.color = color
        this.amount = amount
        this.notes = notes
        this.location = location
    }
}