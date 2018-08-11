package com.playtomic.general.manager.navigation

import com.anemonesdk.general.IContextProvider
import com.playtomic.general.manager.navigation.animations.DialogAnimation
import com.playtomic.general.manager.navigation.animations.NavigationAnimation
import com.playtomic.general.manager.navigation.intent.IDialogIntent
import com.playtomic.general.manager.navigation.intent.INavigationIntent
import com.playtomic.general.view.IView

/**
 * Created by mgonzalez on 15/12/16.
 */

interface INavigationManager : IContextProvider {

    fun show(navigationIntent: INavigationIntent, animation: NavigationAnimation)
    fun replace(view: IView, intent: INavigationIntent)
    fun dismiss(view: IView, animated: Boolean = true)

    fun show(intent: IDialogIntent, animation: DialogAnimation)

}
