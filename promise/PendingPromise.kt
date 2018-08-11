package com.anemonesdk.general.promise

import org.jdeferred.impl.DeferredObject

/**
 * Created by agarcia on 14/06/2017.
 */
class PendingPromise<T> private constructor(val deferred: DeferredObject<T, Throwable, Double>)
    : Promise<T>(deferred.promise(), null, null) {

    constructor(): this(deferred = DeferredObject<T, Throwable, Double>())

    fun fulfill(value: T) {
        deferred.resolve(value)
    }

    fun reject(error: Throwable) {
        deferred.reject(error)
    }

}
