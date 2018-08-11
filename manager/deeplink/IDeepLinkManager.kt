package com.playtomic.general.manager.deeplink

import android.net.Uri

/**
 * Created by manuelgonzalezvillegas on 17/3/17.
 */

interface IDeepLinkManager {
    fun addHandler(handler: IDeepLinkHandler)
    fun processUrl(url: Uri, appRelaunched: Boolean): Boolean

}
