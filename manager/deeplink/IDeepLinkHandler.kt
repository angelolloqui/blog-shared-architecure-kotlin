package com.playtomic.general.manager.deeplink

import android.net.Uri

/**
 * Created by manuelgonzalezvillegas on 2/10/17.
 */
interface IDeepLinkHandler {
    fun processUrl(url: Uri, appRelaunched: Boolean): Boolean
}