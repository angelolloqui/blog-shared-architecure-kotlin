package com.playtomic.general.manager.navigation

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.playtomic.general.manager.navigation.animations.DialogAnimation
import com.playtomic.general.manager.navigation.animations.NavigationAnimation
import com.playtomic.general.manager.navigation.intent.IDialogIntent
import com.playtomic.general.manager.navigation.intent.INavigationIntent
import com.playtomic.general.manager.navigation.utils.ActivityInjector
import com.playtomic.general.manager.navigation.utils.FragmentContainerActivity
import com.playtomic.general.manager.navigation.utils.TransparentFragmentContainerActivity
import com.playtomic.general.view.IView
import com.playtomicui.components.dialog.AlertDialog
import com.playtomicui.extensions.transitionAnimation
import com.playtomicui.transitions.TransitionAnimations
import java.util.*

/**
 * Created by mgonzalez on 15/12/16.
 */

class NavigationManager(context: FragmentActivity) : INavigationManager, Application.ActivityLifecycleCallbacks {

    private val navActivityStack: MutableList<Activity>
    private val modalIntentKey = "modalActivity"

    init {
        this.navActivityStack = ArrayList<Activity>()
        this.navActivityStack.add(0, context)
        context.application.registerActivityLifecycleCallbacks(this)
    }

    override fun show(navigationIntent: INavigationIntent, animation: NavigationAnimation) {
        if (isModalPresented) {
            Log.w("NavigationManager", "WARN: Trying to show a view controller when a modal is shown -> Do nothing")
            return
        }

        val intent =
                if (animation == NavigationAnimation.TRANSPARENT || animation == NavigationAnimation.FADE) {
                    Intent(currentActivity, TransparentFragmentContainerActivity::class.java)
                } else {
                    Intent(currentActivity, FragmentContainerActivity::class.java)
                }
        presentingIntent = intent

        if (animation == NavigationAnimation.PRESENT || animation == NavigationAnimation.TRANSPARENT) {
            intent.transitionAnimation = TransitionAnimations.transparent
        } else if (animation == NavigationAnimation.MODAL) {
            intent.putExtra(modalIntentKey, true)
        } else if (animation == NavigationAnimation.FADE) {
            intent.transitionAnimation = TransitionAnimations.fade
        }

        ActivityInjector.register(intent, navigationIntent.fragment)
        currentActivity.startActivity(intent)
    }

    override fun replace(view: IView, intent: INavigationIntent) {
        val dismissActivity = (view as? Fragment)?.activity ?: return

        for (activity in navActivityStack) {
            if (!activity.isFinishing) {
                activity.intent.transitionAnimation = TransitionAnimations.none
                activity.finish()
            }
            if (activity == dismissActivity) {
                break
            }
        }

        show(intent, NavigationAnimation.NONE)
    }

    override fun dismiss(view: IView, animated: Boolean) {
        if (view is Fragment) {
            val activity = view.activity
            if (!animated) {
                activity?.intent?.transitionAnimation = TransitionAnimations.none
            }
            activity?.finish()
        }
    }

    override fun show(intent: IDialogIntent, animation: DialogAnimation) {
        var handled = false
        var alert = AlertDialog.Builder(currentActivity)
                .setImage(intent.image)
                .setImageUrl(intent.imageUrl)
                .setTitle(intent.title)
                .setMessage(intent.message)
                .setCancelable(intent.cancelable)
                .setOnCancelListener {
                    if (!handled) {
                        handled = true
                        intent.onCancel?.invoke()
                    }
                }
        intent.positiveAction?.let { action ->
            alert = alert.setPositiveButton(text = action.title) {
                if (!handled) {
                    handled = true
                    action.handler?.invoke()
                }
            }
        }
        intent.negativeAction?.let { action ->
            alert = alert.setNegativeButton(text = action.title) {
                if (!handled) {
                    handled = true
                    action.handler?.invoke()
                }
            }
        }
        intent.neutralAction?.let { action ->
            alert = alert.setNeutralButton(text = action.title) {
                if (!handled) {
                    handled = true
                    action.handler?.invoke()
                }
            }
        }
        alert.show()
    }

    // agarcia: Dirty hack but when an activity is being launched it takes some time to reflect the change on activity stack
    var presentingIntent: Intent? = null
    private val isModalPresented: Boolean
        get() = (presentingIntent?.hasExtra(modalIntentKey) ?: false) ||
                navActivityStack.any { it.intent.hasExtra(modalIntentKey) }

    // ********************************
    // *        Context Provider      *
    // ********************************

    override val applicationContext: Context
        get() = currentActivity.application

    override val currentActivity: Activity
        get() {
            for (activity in navActivityStack) {
                if (!activity.isFinishing) {
                    return activity
                }
            }
            return navActivityStack.last()
        }
    override val activityStack: List<Activity>
        get() = navActivityStack

    // ******************************************
    // *        ActivityLifecycleCallbacks      *
    // ******************************************
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if (activity != null) {
            this.navActivityStack.add(0, activity)
            presentingIntent = null
        }
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (navActivityStack.contains(activity)) {
            this.navActivityStack.remove(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }


}
