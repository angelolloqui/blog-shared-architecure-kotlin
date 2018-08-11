package com.playtomic.general.manager.navigation.tabbar

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.playtomic.R
import com.playtomic.general.manager.navigation.utils.ActivityInjector
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

/**
 * Created by mgonzalez on 10/1/17.
 */

open class TabBarActivity : AppCompatActivity() {
    private var tabItems: List<TabItemConfig>? = null

    @JvmField
    @BindView(R.id.bottom_navigation_menu)
    internal var bottomNavigationView: BottomNavigationViewEx? = null

    var listener: ((itemSelected: Int) -> Unit)? = null

    var selectedIndex: Int = 0
        set(value) {
            field = value
            val menu = bottomNavigationView?.menu
            menu?.getItem(value)?.isChecked = true
        }


    lateinit var currentFragment: Fragment
    private var pendingReplaceFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            selectedIndex = savedInstanceState.getInt("selectedIndex")
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.tab_bar_activity)
        ButterKnife.bind(this)
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            val listener = listener
            if (listener != null) {
                listener(item.order)
                false
            } else {
                true
            }
        }

        val callback = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(fm: FragmentManager?, f: Fragment?, v: View?, savedInstanceState: Bundle?) {
                super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                hideBackButton(v)
            }
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(callback, false)

        updateTabItems()
    }

    override fun onResume() {
        super.onResume()
        val pendingReplaceFragment = pendingReplaceFragment ?: return
        this.pendingReplaceFragment = null
        replaceFragment(pendingReplaceFragment)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun configTabs(tabItems: List<TabItemConfig>) {
        this.tabItems = tabItems
        updateTabItems()
    }


    fun updateTabItems() {
        val tabItems = tabItems
        if (tabItems != null) {
            for (i in tabItems.indices) {
                bottomNavigationView?.menu
                        ?.add(Menu.NONE, Menu.NONE, i, tabItems[i].title?.let(this::getString))
                        ?.setIcon(tabItems[i].icon)
            }
            this.selectedIndex = selectedIndex

            if (tabItems.size > 3) {
                com.playtomic.general.view.BottomNavigationViewHelper.disableShiftMode(bottomNavigationView)
            }

            bottomNavigationView?.setIconSize(32f, 32f)
            bottomNavigationView?.setIconsMarginTop(10)
            bottomNavigationView?.setTextVisibility(false)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        ActivityInjector.register(intent, fragment)
        this.currentFragment = fragment
        try {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
        } catch (ex: IllegalStateException) {
            pendingReplaceFragment = fragment
        }
    }

    private fun hideBackButton(view: View?) {
        view?.findViewById<View>(R.id.toolbar_back_button)?.let {
            it.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedIndex", selectedIndex)
    }
}
