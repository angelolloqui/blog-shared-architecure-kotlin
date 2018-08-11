package com.playtomic.general.manager.location.model

import com.google.android.gms.location.LocationRequest
import java.util.*

/**
 * Created by agarcia on 20/01/2017.
 */

class LocationServiceRequest(private val minAccuracy: Double?, private val maxAge: Double, var timeout: Double, val fulfill: (Location) -> Unit, val reject: (Throwable) -> Unit) {

    var timer: TimerTask? = null

    fun isLocationValid(location: Location): Boolean {

        //Check the accuaracy
        minAccuracy?.let {
            val locationAccuracy = location.accuracy ?: return false
            if (minAccuracy < locationAccuracy) {
                return false
            }
        }

        //Check the age
        /*  Google APIs are handling age automatically, so better to not intereer as it is optimum
        if (maxAge > 0 && (new Date().getTime() - location.timestamp.getTime()) > (maxAge * 1000)) {
            return false;
        }
        */

        return true
    }

    val locationRequest: LocationRequest
        get() {
            val locationRequest = LocationRequest()
            locationRequest.setExpirationDuration((timeout * 1000).toLong())
            locationRequest.interval = 1000
            return locationRequest
        }
}
