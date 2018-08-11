package com.playtomic.general.manager.location.model

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import com.anemonesdk.model.Coordinate
import org.json.JSONException
import java.util.*

/**
 * Created by agarcia on 23/12/2016.
 */

class Location : JSONMappable {

    val timestamp: Date

    val accuracy: Double?

    val coordinate: Coordinate

    constructor(timestamp: Date, accuracy: Double?, coordinate: Coordinate) {
        this.timestamp = timestamp
        this.accuracy = accuracy
        this.coordinate = coordinate
    }

    constructor(location: android.location.Location) {
        this.timestamp = Date(location.time)
        this.accuracy = location.accuracy.toDouble()
        this.coordinate = Coordinate(location.latitude, location.longitude)
    }

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        this.timestamp = Date()
        this.accuracy = null
        this.coordinate = Coordinate(json = json)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Location

        if (timestamp != other.timestamp) return false
        if (accuracy != other.accuracy) return false
        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode(): Int  = timestamp.hashCode()

}
