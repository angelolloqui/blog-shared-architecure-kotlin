package com.anemonesdk.general.promise

import android.os.AsyncTask
import org.jdeferred.android.AndroidDeferredManager
import org.jdeferred.impl.DeferredObject

/**
 * Created by mgonzalez on 20/12/16.
 */

open class Promise<D> {
    companion object {
        var asyncExecutor: (() -> Unit) -> Unit = {
            AsyncTask.execute(it)
        }
    }


    internal var promise: org.jdeferred.Promise<D, Throwable, Double>

    var value: D? = null

    var error: Throwable? = null

    constructor(value: D) {
        val manager = AndroidDeferredManager()
        val deferred = DeferredObject<D, Throwable, Double>()
        promise = manager.`when`(deferred.promise())
        this.value = value
        deferred.resolve(value)
    }

    constructor(error: Throwable) {
        val manager = AndroidDeferredManager()
        val deferred = DeferredObject<D, Throwable, Double>()
        promise = manager.`when`(deferred.promise())
        this.error = error
        deferred.reject(error)
    }

    constructor(executeInBackground: Boolean = false, resolver: (fulfill: (D) -> Unit, reject: (Throwable) -> Unit) -> Unit) {
        val manager = AndroidDeferredManager()
        val deferred = DeferredObject<D, Throwable, Double>()
        promise = manager.`when`(deferred).always { state, resolved, rejected ->
            this.value = resolved
            this.error = rejected
        }
        val executionJob = {
            try {
                resolver({ deferred.resolve(it) }, { deferred.reject(it) })
            } catch (e: Throwable) {
                deferred.reject(e)
            }
        }
        if (executeInBackground) {
            asyncExecutor { executionJob() }
        } else {
            executionJob()
        }
    }

    protected constructor(promise: org.jdeferred.Promise<D, Throwable, Double>, value: D?, error: Throwable?) {
        this.value = value
        this.error = error
        this.promise = promise.always { state, resolved, rejected ->
            this.value = resolved
            this.error = rejected
        }
    }

    fun catchError(callback: (Throwable) -> Unit): Promise<D> {
        return Promise<D>(promise.fail(callback), value, error)
    }

    fun then(doneCallback: (D) -> Unit): Promise<D> {
        return Promise(promise.then(doneCallback), value, error)
    }

    @JvmName("thenMap")
    fun <U> then(map: (D) -> U): Promise<U> {
        return Promise(promise.then<U, Throwable, Double>(map), null, error)
    }

    @JvmName("thenPromise")
    fun <D_OUT> then(promise: (D) -> Promise<D_OUT>): Promise<D_OUT> {

        val deferred = DeferredObject<D_OUT, Throwable, Double>()

        this.promise = this.promise
                .then { result: D ->
                    try {
                        val deferredPromise = promise(result)
                        deferredPromise
                                .then(deferred::resolve)
                                .catchError { deferred.reject(it) }
                    } catch (e: Throwable) {
                        deferred.reject(e)
                    }
                }
                .fail { deferred.reject(it) }

        return Promise(deferred, null, error)
    }

//    private fun always(callback: (org.jdeferred.Promise.State, D, Throwable) -> Unit): Promise<D> {
//        return Promise(promise.always(callback), value, error)
//    }


    fun always(callback: () -> Unit): Promise<D> {
        return Promise(promise.always { state, resolved, rejected -> callback() }, value, error)
    }

}

fun <T> whenAll(promises: List<Promise<T>>): Promise<List<T>> =
        Promise { fulfill, reject ->
            val internalPromises = promises.map { it.promise }
            val manager = AndroidDeferredManager()
            manager.`when`(*internalPromises.toTypedArray())
                    .then { results ->
                        fulfill(results.toList().map { it.result as T })
                    }
                    .fail {
                        reject(it.reject as Throwable)
                    }
        }

fun <T1, T2> whenAll(promise1: Promise<T1>, promise2: Promise<T2>): Promise<ResultTuple2<T1, T2>> =
        Promise { fulfill, reject ->
            val manager = AndroidDeferredManager()
            manager.`when`(promise1.promise, promise2.promise)
                    .then { results ->
                        fulfill(ResultTuple2(results[0].result as T1, results[1].result as T2))
                    }
                    .fail {
                        reject(it.reject as Throwable)
                    }
        }

fun <T1, T2, T3> whenAll(promise1: Promise<T1>, promise2: Promise<T2>, promise3: Promise<T3>): Promise<ResultTuple3<T1, T2, T3>> =
        Promise { fulfill, reject ->
            val manager = AndroidDeferredManager()
            manager.`when`(promise1.promise, promise2.promise, promise3.promise)
                    .then { results ->
                        fulfill(ResultTuple3(results[0].result as T1, results[1].result as T2, results[2].result as T3))
                    }
                    .fail {
                        reject(it.reject as Throwable)
                    }
        }

fun <T1, T2, T3, T4> whenAll(promise1: Promise<T1>, promise2: Promise<T2>, promise3: Promise<T3>, promise4: Promise<T4>): Promise<ResultTuple4<T1, T2, T3, T4>> =
        Promise { fulfill, reject ->
            val manager = AndroidDeferredManager()
            manager.`when`(promise1.promise, promise2.promise, promise3.promise, promise4.promise)
                    .then { results ->
                        fulfill(ResultTuple4(results[0].result as T1, results[1].result as T2, results[2].result as T3, results[3].result as T4))
                    }
                    .fail {
                        reject(it.reject as Throwable)
                    }
        }

data class ResultTuple2<out T1, out T2>(val value1: T1, val value2: T2)
data class ResultTuple3<out T1, out T2, out T3>(val value1: T1, val value2: T2, val value3: T3)
data class ResultTuple4<out T1, out T2, out T3, out T4>(val value1: T1, val value2: T2, val value3: T3, val value4: T4)
