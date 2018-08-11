package com.playtomic.general.manager.location.model

import com.anemonesdk.general.json.JSONMappable
import com.anemonesdk.general.json.JSONObject
import org.json.JSONException

/**
 * Created by agarcia on 21/04/2017.
 */

class PlaceAutocomplete : JSONMappable {

    val id: String

    val description: String

    val mainText: String

    val secondaryText: String

    val types: List<PlaceAutocompleteType>

    @Throws(JSONException::class)
    constructor(json: JSONObject): super(json) {
        id = json.getString("place_id")
        description = json.getString("description")
        types = json.getStringArray("types").map { PlaceAutocompleteType.fromRawValue(it) }

        val structuredFormattingJson = json.getJSONObject("structured_formatting")
        mainText = structuredFormattingJson.getString("main_text")
        secondaryText = structuredFormattingJson.getString("secondary_text")
    }

    constructor(id: String = "", description: String = "", mainText: String = "", secondaryText: String = "", types: List<PlaceAutocompleteType> = arrayListOf()) : super() {
        this.id = id
        this.description = description
        this.mainText = mainText
        this.secondaryText = secondaryText
        this.types = types
    }

}
