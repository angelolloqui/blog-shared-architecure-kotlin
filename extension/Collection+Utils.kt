package com.anemonesdk.general.extension

/**
 * Created by manuelgonzalezvillegas on 30/5/17.
 */

fun <T, R> Iterable<T>.compactMap(transform: (T) -> R?): List<R> {
    val destination = mutableListOf<R>()
    for (element in this) {
        try {
            val obj = transform(element)
            if (obj != null) {
                destination.add(obj)
            }
        } catch (ex: Throwable) {
        }
    }
    return destination
}

fun <T, R> Iterable<T>.flatMap(transform: (T) -> Iterable<R>?): List<R> {
    val destination = mutableListOf<R>()
    for (element in this) {
        val obj = transform(element)
        if (obj != null) {
            destination.addAll(obj)
        }
    }
    return destination
}

fun <K, V, R> Map<K, V>.compactMap(transform: (Map.Entry<K, V>) -> R?): List<R> {
    val destination = mutableListOf<R>()
    this.forEach { element ->
        try {
            val obj = transform(element)
            obj?.let { obj ->
                destination.add(obj)
            }
        } catch (e: Throwable) {}
    }
    return destination
}

fun <T> Iterable<T>.removing(element: T): Iterable<T> {
    val index = indexOf(element)
    if (index >= 0) {
        val newArray = this.toMutableList()
        newArray.removeAt(index)
        return newArray
    }
    return this
}

fun <T> MutableList<T>.removeLast() {
    removeAt(size - 1)
}
