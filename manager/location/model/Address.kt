package com.anemonesdk.model

import android.text.TextUtils
import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import org.json.JSONException
import java.util.*

/**
 * Created by agarcia on 23/12/2016.
 */

class Address : JSONMappable, HistorySerializable {

    var name: String? = null

    var streetName: String? = null

    var streetNumber: String? = null

    var locality: String? = null

    var subLocality: String? = null

    var administrativeArea: String? = null

    var subAdministrativeArea: String? = null

    var postalCode: String? = null

    var countryCode: String? = null

    var countryName: String? = null

    var coordinate: Coordinate? = null

    var timeZone: TimeZone? = null

    constructor(coordinate: Coordinate) {
        this.coordinate = coordinate
    }

    constructor(name: String, coordinate: Coordinate) {
        this.name = name
        this.coordinate = coordinate
    }


    constructor(address: android.location.Address, coordinate: Coordinate?) {
        this.name = address.featureName
        this.streetName = address.thoroughfare
        this.streetNumber = address.subThoroughfare
        this.locality = address.locality
        this.subLocality = address.subLocality
        this.administrativeArea = address.adminArea
        this.subAdministrativeArea = address.subAdminArea
        this.postalCode = address.postalCode
        this.countryCode = address.countryCode
        this.countryName = address.countryName
        if (coordinate == null && address.hasLatitude() && address.hasLongitude()) {
            this.coordinate = Coordinate(address.latitude, address.longitude)
        } else {
            this.coordinate = coordinate
        }

        // Not nice but sometimes google api returns same info in streetNumber and name. Name in that case is wrong and should include the streetName
        if (name != null && streetNumber != null && streetName != null && name == streetNumber) {
            this.name = streetName + ", " + streetNumber
        }
    }

    @Throws(JSONException::class)
    constructor(json: JSONObject) : super(json) {
        if (json.has("html_attributions") || json.has("result")) {
            mapFromGooglePlacesJSON(json.getJSONObject("result"))
        } else {
            mapFromServerJSON(json)
        }
    }

    constructor(name: String? = null, streetName: String? = null, streetNumber: String? = null, locality: String? = null, subLocality: String? = null, administrativeArea: String? = null, subAdministrativeArea: String? = null, postalCode: String? = null, countryCode: String? = null, countryName: String? = null, coordinate: Coordinate? = null, timeZone: TimeZone? = null) : super() {
        this.name = name
        this.streetName = streetName
        this.streetNumber = streetNumber
        this.locality = locality
        this.subLocality = subLocality
        this.administrativeArea = administrativeArea
        this.subAdministrativeArea = subAdministrativeArea
        this.postalCode = postalCode
        this.countryCode = countryCode
        this.countryName = countryName
        this.coordinate = coordinate
        this.timeZone = timeZone
    }


    @Throws(JSONException::class)
    override fun toJson(): JSONObject {
        val json = JSONObject()
        json.setString("street", streetName)
        json.setString("postal_code", postalCode)
        json.setString("city", locality)
        json.setObject("coordinate", coordinate?.toJson())
        json.setString("name", name)
        json.setString("street_number", streetNumber)
        json.setString("sub_locality", subLocality)
        json.setString("administrative_area", administrativeArea)
        json.setString("sub_administrative_area", subAdministrativeArea)
        json.setString("country_code", countryCode)
        json.setString("country", countryName)
        json.setString("timezone", timeZone?.id)

        return json
    }

    fun formattedShortAddress(): String {
        name?.let {
            return it
        }

        var addressComponents: MutableList<String> = ArrayList()
        streetName?.let { streetName ->
            addressComponents.add(streetName)

            streetNumber?.let { streetNumber ->
                addressComponents.add(streetNumber)
            }
        }
        locality?.let { locality ->
            addressComponents.add(locality)
        }

        if (addressComponents.isEmpty()) {

            return formattedDistrict()
        }

        addressComponents = addressComponents.filter { !TextUtils.isEmpty(it) }.toMutableList()
        return TextUtils.join(", ", addressComponents)
    }

    fun formattedFullAddress(): String {
        name?.let { name ->
            return name
        }

        var addressComponents: MutableList<String> = ArrayList()

        streetName?.let { streetName ->
            addressComponents.add(streetName)

            streetNumber?.let { streetNumber ->
                addressComponents.add(streetNumber)
            }
        }

        postalCode?.let { postalCode ->
            addressComponents.add(postalCode)
        }

        locality?.let { locality ->
            addressComponents.add(locality)
        }

        countryName?.let { countryName ->
            addressComponents.add(countryName)
        }

        addressComponents = addressComponents.filter { !TextUtils.isEmpty(it) }.toMutableList()
        return TextUtils.join(", ", addressComponents)
    }

    fun formattedDistrict(): String {
        return subLocality ?: locality ?: formattedRegion()
    }

    fun formattedRegion(): String {
        var addressComponents: MutableList<String> = ArrayList()

        val administrativeArea = administrativeArea
        val subAdministrativeArea = subAdministrativeArea
        val locality = locality
        if (administrativeArea != null) {
            addressComponents.add(administrativeArea)
        } else if (subAdministrativeArea != null) {
            addressComponents.add(subAdministrativeArea)
        } else if (locality != null) {
            addressComponents.add(locality)
        }

        countryName?.let { countryName ->
            addressComponents.add(countryName)
        }

        addressComponents = addressComponents.filter { !TextUtils.isEmpty(it) }.toMutableList()
        return TextUtils.join(", ", addressComponents)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val address = other as Address?

        return if (coordinate != null) coordinate == address?.coordinate else address?.coordinate == null

    }

    override fun hashCode(): Int {
        return coordinate?.hashCode() ?: 0
    }


    override val historyType: HistoryItemType
        get() = HistoryItemType.ADDRESS

    override fun replaces(item: HistorySerializable): Boolean {
        return item == this
    }


    @Throws(JSONException::class)
    private fun mapFromServerJSON(json: JSONObject) {
        streetName = json.optString("street")
        postalCode = json.optString("postal_code")
        locality = json.optString("city")
        if (json.has("coordinate")) {
            coordinate = Coordinate(json.getJSONObject("coordinate"))
        }
        name = json.optString("name")
        streetNumber = json.optString("street_number")
        subLocality = json.optString("sub_locality")
        administrativeArea = json.optString("administrative_area")
        subAdministrativeArea = json.optString("sub_administrative_area")
        countryCode = json.optString("country_code")
        countryName = json.optString("country") ?: json.optString("country_name")
        val timeZone = json.optString("timezone")
        if (timeZone != null) {
            this.timeZone = TimeZone.getTimeZone(timeZone)
        }
    }

    @Throws(JSONException::class)
    private fun mapFromGooglePlacesJSON(json: JSONObject) {
        name = json.getString("name")
        val coordinateJson = json.getJSONObject("geometry").getJSONObject("location")
        coordinate = Coordinate(coordinateJson.getDouble("lat"), coordinateJson.getDouble("lng"))

        val addressComponents = json.getJSONArray("address_components").asJSONList()
        for (component in addressComponents) {
            val types = component.optStringArray("types")
            val name = component.optString("long_name")
            if (types != null) {

                if (types.contains("street_number")) {
                    streetNumber = name
                } else if (types.contains("route")) {
                    streetName = name
                } else if (types.contains("locality")) {
                    locality = name
                } else if (types.contains("sublocality")) {
                    subLocality = name
                } else if (types.contains("administrative_area_level_1")) {
                    administrativeArea = name
                } else if (types.contains("administrative_area_level_2")) {
                    subAdministrativeArea = name
                } else if (types.contains("postal_code")) {
                    postalCode = name
                } else if (types.contains("country")) {
                    countryName = name
                    countryCode = component.optString("short_name")
                }
            }
        }
    }
}
