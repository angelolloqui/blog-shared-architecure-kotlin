package com.playtomic.general.manager.location.model

/**
 * Created by agarcia on 09/05/2017.
 */

enum class PlaceAutocompleteType {
    NEIGHBORHOOD,
    DISTRICT,
    CITY,
    REGION,
    STATION,
    OTHER;


    companion object {

        fun fromRawValue(rawValue: String): PlaceAutocompleteType {
            when (rawValue) {
                "bus_station", "train_station", "transit_station", "subway_station" -> return STATION

                "neighborhood" -> return NEIGHBORHOOD

                "sublocality", "sublocality_level_1" -> return DISTRICT

                "locality" -> return CITY

                "administrative_area_level_1", "administrative_area_level_2", "administrative_area_level_3", "administrative_area_level_4" -> return REGION

                else -> return OTHER
            }
        }
    }

}
