package com.playtomic.general.manager.navigation.intent

/**
 * Created by manuelgonzalezvillegas on 14/6/17.
 */

interface IDialogIntent {
    val image: Int?
    val imageUrl: String?
    val title: String?
    val message: String?
    val positiveAction: DialogAction?
    val negativeAction: DialogAction?
    val neutralAction: DialogAction?
    val cancelable: Boolean
    val onCancel: (() -> Unit)?
}

class DialogAction(
    val title: String,
    val handler: (() -> Unit)?
)
