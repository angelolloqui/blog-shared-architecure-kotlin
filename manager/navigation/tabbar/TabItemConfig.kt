package com.playtomic.general.manager.navigation.tabbar

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

import com.playtomic.general.coordinator.IMainCoordinator

/**
 * Created by mgonzalez on 10/1/17.
 */

data class TabItemConfig(
        val id: String,
        @StringRes val title: Int?,
        @DrawableRes val icon: Int,
        val coordinator: IMainCoordinator)
