package com.anemonesdk.general.client

import com.anemonesdk.general.json.CustomStringConvertible

/**
 * Created by manuelgonzalezvillegas on 25/8/17.
 */
enum class HttpMethod: CustomStringConvertible {
    get,
    post,
    put,
    patch,
    delete;

    override val description get() = name.toUpperCase()
}