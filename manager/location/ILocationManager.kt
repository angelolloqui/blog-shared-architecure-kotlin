package com.playtomic.general.manager.location

import com.anemonesdk.general.promise.Promise
import com.anemonesdk.model.Address
import com.anemonesdk.model.Coordinate
import com.playtomic.general.manager.location.model.Location
import com.playtomic.general.manager.location.model.LocationServiceStatus
import com.playtomic.general.manager.location.model.PlaceAutocomplete

/**
 * Created by agarcia on 23/12/2016.
 */

interface ILocationManager {

    val lastLocation: Location?

    val locationStatus: LocationServiceStatus

    fun findLocation(allowRequestPermission: Boolean): Promise<Location>

    fun findLocation(allowRequestPermission: Boolean, minAccuracy: Double, maxAge: Double, timeout: Double): Promise<Location>

    fun findAddress(coordinate: Coordinate): Promise<Address>

    fun findAddress(placeAutocomplete: PlaceAutocomplete): Promise<Address>

    fun findAddresses(text: String): Promise<List<Address>>

    fun findAutocomplete(text: String): Promise<List<PlaceAutocomplete>>

    fun hasPermission(): Boolean
    fun requestLocationPermission()

}

fun ILocationManager.formatDistance(coordinate: Coordinate?): String? {
    val fromCoordinate = lastLocation?.coordinate ?: return null
    if (coordinate == null) return null

    return  fromCoordinate.formattedDistance(coordinate)
}
