package com.playtomic.general.manager.navigation.tabbar

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import com.playtomic.general.manager.navigation.NavigationManager
import com.playtomic.general.manager.navigation.animations.DialogAnimation
import com.playtomic.general.manager.navigation.animations.NavigationAnimation
import com.playtomic.general.manager.navigation.intent.IDialogIntent
import com.playtomic.general.manager.navigation.intent.INavigationIntent
import com.playtomic.general.view.IView

/**
 * Created by mgonzalez on 10/1/17.
 */

class TabBarNavigationManager(private var tabBarActivity: TabBarActivity)
    : ITabBarNavigationManager, Application.ActivityLifecycleCallbacks {

    override var tabItems: List<TabItemConfig> = listOf()
    override val selectedTabIndex: Int
        get() = tabBarActivity.selectedIndex

    private val navigationManager: NavigationManager

    private val tabFragments: MutableMap<Int, Fragment>

    init {
        this.tabItems = listOf()
        this.tabFragments = mutableMapOf()
        this.navigationManager = NavigationManager(tabBarActivity)

        tabBarActivity.listener = { index ->  this.selectTab(tabItems[index]) }
        tabBarActivity.application.registerActivityLifecycleCallbacks(this)
    }

    override fun configTabs(tabItems: List<TabItemConfig>) {
        this.tabItems = tabItems
        tabBarActivity.configTabs(tabItems)
        openSelectedTab()
    }

    override fun selectTab(item: TabItemConfig) {
        val index = tabItems.indexOf(item)
        if (index < 0) { return }
        tabBarActivity.selectedIndex = index
        openSelectedTab()
    }

    private fun openSelectedTab() {
        val index = selectedTabIndex

        activityStack.forEach { activity ->
            if (activity == tabBarActivity) {
                return@forEach
            }
            if (!activity.isFinishing) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.finishAfterTransition()
                } else {
                    activity.finish()
                }
            }
        }

        var fragment: Fragment? = tabFragments[index]
        if (fragment == null) {
            val tabItem = tabItems[index]
            val coordinator = tabItem.coordinator
            val intent = coordinator.mainIntent()
            fragment = intent.fragment
            tabFragments.put(index, fragment)
        }
        tabBarActivity.replaceFragment(fragment)
    }

    override val applicationContext: Context
        get() = navigationManager.applicationContext

    override val currentActivity: Activity
        get() = navigationManager.currentActivity

    override val activityStack: List<Activity>
        get() = navigationManager.activityStack

    override fun show(navigationIntent: INavigationIntent, animation: NavigationAnimation) {
        navigationManager.show(navigationIntent, animation)
    }

    override fun replace(view: IView, intent: INavigationIntent) {
        navigationManager.replace(view, intent)
    }

    override fun dismiss(view: IView, animated: Boolean) {
        navigationManager.dismiss(view, animated)
    }

    override fun show(intent: IDialogIntent, animation: DialogAnimation) {
        navigationManager.show(intent = intent, animation = animation)
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (activity is TabBarActivity) {
            tabBarActivity = activity
            tabBarActivity.listener = { index ->
                selectTab(tabItems[index])
            }
            configTabs(tabItems)
        }
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }
}
