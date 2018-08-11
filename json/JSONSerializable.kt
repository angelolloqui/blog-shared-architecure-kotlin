package com.anemonesdk.general.json

import org.json.JSONException

/**
 * Created by agarcia on 16/02/2017.
 */

interface JSONSerializable {

    @Throws(JSONException::class)
    fun toJson(): JSONObject

}
