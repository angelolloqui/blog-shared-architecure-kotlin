package com.playtomic.general.manager.navigation.utils

import android.os.Bundle
import android.support.v4.app.Fragment
import com.playtomic.R
import com.playtomic.general.presenter.navigation.PresenterFragment
import com.playtomicui.transitions.PlaytomicUIActivity

/**
 * Created by mgonzalez on 9/1/17.
 */

open class FragmentContainerActivity : PlaytomicUIActivity() {

    private var lastFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if (savedInstanceState == null) {
                ActivityInjector.inject(this)
            }
            setContentView(R.layout.fragment_container_activity)
        } catch (e: Exception) {
            finish()
        }

    }

    override fun onBackPressed() {
        if (lastFragment is PresenterFragment<*> && !(lastFragment as PresenterFragment<*>).shouldAllowBackPress()) {
            return
        }
        super.onBackPressed()
    }

    fun addFragment(fragment: Fragment) {
        this.lastFragment = fragment
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
    }

}
