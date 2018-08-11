package com.playtomic.general.manager.navigation.utils

import android.content.Intent
import android.support.v4.app.Fragment
import java.util.*

/**
 * Created by mgonzalez on 15/12/16.
 */
object ActivityInjector {
    private val fragmentMap = HashMap<String, Fragment>()

    fun register(intent: Intent, presenterFragment: Fragment) {
        var id: String? = intent.getStringExtra("intentId")
        if (id == null) {
            id = randomString
            intent.putExtra("intentId", id)
        }
        fragmentMap.put(id, presenterFragment)
    }

    @Throws(Exception::class)
    fun inject(activity: FragmentContainerActivity) {
        val id = activity.intent.getStringExtra("intentId")
        val fragment = fragmentMap.remove(id) ?: throw NullPointerException()
        activity.addFragment(fragment)
    }

    private val randomString: String
        get() = UUID.randomUUID().toString()
}
