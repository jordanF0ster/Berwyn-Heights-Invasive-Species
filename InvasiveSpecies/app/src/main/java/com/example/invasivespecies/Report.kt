package com.example.invasivespecies

import android.location.Location
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Report(val id: String? = null,
                  val plantname: String? = null,
                  val color: String? = null,
                  val amount: String? = null,
                  val notes: String? = null,
                  val location: Location? = null) {


}