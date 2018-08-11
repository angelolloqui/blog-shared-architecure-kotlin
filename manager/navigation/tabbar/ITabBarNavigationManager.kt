package com.playtomic.general.manager.navigation.tabbar

import com.playtomic.general.manager.navigation.INavigationManager

/**
 * Created by mgonzalez on 10/1/17.
 */

interface ITabBarNavigationManager : INavigationManager {
    val tabItems: List<TabItemConfig>
    val selectedTabIndex: Int
    fun configTabs(tabItems: List<TabItemConfig>)
    fun selectTab(item: TabItemConfig)
}
