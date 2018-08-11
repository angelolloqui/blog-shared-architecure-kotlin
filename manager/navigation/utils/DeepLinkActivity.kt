package com.playtomic.general.manager.navigation.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.playtomic.R
import com.playtomic.general.manager.ManagerProvider
import com.playtomic.general.IDependencyProvider
import io.branch.referral.Branch


/**
 * Created by manuelgonzalezvillegas on 17/3/17.
 */

class DeepLinkActivity : Activity() {

    private var appRelaunched = false
    private var processed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.LaunchScreenTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val dependencyProvider = IDependencyProvider.instance

        if (dependencyProvider == null || dependencyProvider.managerProvider.navigationManager.activityStack.size <= 1) {
            appRelaunched = true
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent.data = this.intent.data
            intent.putExtras(this.intent.extras)
            startActivity(intent)
            checkAppStarted()
        } else {
            processIntentUrl(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
    }

    private fun checkAppStarted() {
        Handler().postDelayed({
            processIntentUrl(intent)

            if (!processed) {
                checkAppStarted()
            }
        }, 500)
    }

    @Synchronized
    private fun processIntentUrl(intent: Intent) {
        if (processed) {
            return
        }
        IDependencyProvider.instance?.let { dependencyProvider ->
            processed = true
            finish()
            if (intent.data != null) {
                dependencyProvider.managerProvider.deepLinkManager.processUrl(url = intent.data, appRelaunched = appRelaunched)
            }
        }
    }

}
