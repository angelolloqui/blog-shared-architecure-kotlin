package com.anemonesdk.general

import android.app.Activity
import android.content.Context

/**
 * Created by agarcia on 20/01/2017.
 */

interface IContextProvider {

    val applicationContext: Context

    val currentActivity: Activity

    val activityStack: List<Activity>
}
