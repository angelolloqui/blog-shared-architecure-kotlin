package com.playtomic.general.manager.navigation.utils

import android.os.Bundle
import com.playtomic.general.presenter.IPresenter
import com.playtomic.general.presenter.navigation.PresenterFragment
import java.util.*

/**
 * Created by manuelgonzalezvillegas on 8/5/17.
 */

object InstanceStateInjector {

    private val presenterMap = HashMap<String, IPresenter<*>>()

    fun save(bundle: Bundle, fragment: PresenterFragment<*>) {
        val id = randomString
        bundle.putString("presenterId", id)
        presenterMap.put(id, fragment.presenter)
    }

    fun restore(bundle: Bundle, fragment: PresenterFragment<*>) {
        bundle.getString("presenterId")?.let {
            fragment.presenter = presenterMap.remove(it)
        }
    }

    private val randomString: String
        get() = UUID.randomUUID().toString()

}
