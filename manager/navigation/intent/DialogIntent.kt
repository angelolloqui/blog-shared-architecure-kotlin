package com.playtomic.general.manager.navigation.intent

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.playtomicui.utils.Context

/**
 * Created by manuelgonzalezvillegas on 14/6/17.
 */

class DialogIntent
    : IDialogIntent {

    override @DrawableRes var image: Int? = null
    override var imageUrl: String? = null
    override var title: String? = null
    override var message: String? = null
    override var positiveAction: DialogAction? = null
    override var negativeAction: DialogAction? = null
    override var neutralAction: DialogAction? = null
    override var cancelable: Boolean = true
    override var onCancel: (() -> Unit)? = null

    fun image(@DrawableRes image: Int?): DialogIntent {
        this.image = image
        return this
    }

    fun imageUrl(imageUrl: String?): DialogIntent {
        this.imageUrl = imageUrl
        return this
    }

    fun title(@StringRes title: Int): DialogIntent = title(title = Context.getString(title))
    fun title(title: String?): DialogIntent {
        this.title = title
        return this
    }

    fun message(@StringRes title: Int): DialogIntent = message(message = Context.getString(title))
    fun message(message: String?): DialogIntent {
        this.message = message
        return this
    }

    fun positiveAction(@StringRes title: Int, handler: (() -> Unit)? = null): DialogIntent = positiveAction(title = Context.getString(title), handler = handler)
    fun positiveAction(title: String, handler: (() -> Unit)? = null): DialogIntent {
        this.positiveAction = DialogAction(title = title, handler = handler)
        return this
    }

    fun negativeAction(@StringRes title: Int, handler: (() -> Unit)? = null): DialogIntent = negativeAction(title = Context.getString(title), handler = handler)
    fun negativeAction(title: String, handler: (() -> Unit)? = null): DialogIntent {
        this.negativeAction = DialogAction(title = title, handler = handler)
        return this
    }

    fun neutralAction(@StringRes title: Int, handler: (() -> Unit)? = null): DialogIntent = neutralAction(title = Context.getString(title), handler = handler)
    fun neutralAction(title: String, handler: (() -> Unit)? = null): DialogIntent {
        this.neutralAction = DialogAction(title = title, handler = handler)
        return this
    }

    fun cancelable(cancelable: Boolean = true, onCancel: (() -> Unit)? = null): DialogIntent {
        this.cancelable = cancelable
        this.onCancel = onCancel
        return this
    }

}
