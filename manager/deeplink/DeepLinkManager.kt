package com.playtomic.general.manager.deeplink

import android.net.Uri
import com.playtomic.general.Constants

/**
 * Created by manuelgonzalezvillegas on 17/3/17.
 */

class DeepLinkManager
    : IDeepLinkManager {

    var handlers: MutableList<IDeepLinkHandler> = mutableListOf()

    override fun addHandler(handler: IDeepLinkHandler) {
        handlers.add(handler)
    }

    override fun processUrl(url: Uri, appRelaunched: Boolean): Boolean {
        val url = convertToAppLinkUrl(url) ?: return false

        var processed = false
        handlers.forEach { handler ->
           if(handler.processUrl(url, appRelaunched = appRelaunched)) {
               processed = true
           }
        }
        return processed
    }

    private fun convertToAppLinkUrl(url: Uri): Uri? {
        url.authority ?: return null
        url.scheme ?: return null

        val baseUrl = Uri.parse(Constants.webUrl)
        if (url.authority.contains("playtomic")) {
            return url
        }
        if (url.scheme != Constants.scheme) {
            return null
        }
        return Uri.Builder()
                .scheme(baseUrl.scheme)
                .authority(baseUrl.authority)
                .appendEncodedPath(url.authority)
                .appendEncodedPath(url.pathSegments.joinToString(separator = "/"))
                .encodedQuery(url.encodedQuery)
                .build()
    }

}
