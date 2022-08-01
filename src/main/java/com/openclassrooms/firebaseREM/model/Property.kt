package com.openclassrooms.firebaseREM.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class Property(
    val type: String = "",
    val price: Int = 0,
    val avatar1: String = "",
    val description: String = "",
    val surface: Int = 0,
    val numberOfRooms: Int = 0,
    val numberOfBathrooms: Int = 0,
    val numberOfBedrooms: Int = 0,
    val city: String = "",
    val address: String = "",
    var id: String? = "",
) : Serializable




