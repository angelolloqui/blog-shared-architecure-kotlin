package com.anemonesdk.general.extension

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by agarcia on 02/06/2017.
 */

object Dates {
    val UTC_TIME_ZONE = TimeZone.getTimeZone("UTC")

    fun from(day: Date, time: Date, timeZone: TimeZone? = null): Date {
        val dateComponents = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
        dateComponents.timeInMillis = day.time

        val timeComponents = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
        timeComponents.timeInMillis = time.time

        dateComponents.set(Calendar.HOUR_OF_DAY, timeComponents.get(Calendar.HOUR_OF_DAY))
        dateComponents.set(Calendar.MINUTE, timeComponents.get(Calendar.MINUTE))
        dateComponents.set(Calendar.SECOND, timeComponents.get(Calendar.SECOND))
        dateComponents.set(Calendar.MILLISECOND, timeComponents.get(Calendar.MILLISECOND))

        return dateComponents.time
    }

    fun today(timeZone: TimeZone? = null): Date =
            Date().midnight(timeZone = timeZone)


    fun tomorrow(timeZone: TimeZone? = null): Date =
            today(timeZone = timeZone).addDays(1)


    fun yesterday(timeZone: TimeZone? = null): Date =
            today(timeZone = timeZone).addDays(-1)

}

@JvmOverloads
fun Date.midnight(timeZone: TimeZone? = null): Date {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.timeInMillis = this.time
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.time
}

fun Date.addDays(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.DAY_OF_YEAR, days)
    return calendar.time
}

fun Date.addHours(hours: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.HOUR_OF_DAY, hours)
    return calendar.time
}

fun Date.addMinutes(minutes: Int): Date {
    // TODO: Make this method threadsafe
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    calendar.add(Calendar.MINUTE, minutes)
    return calendar.time
}

fun Date.isToday(timeZone: TimeZone? = null): Boolean =
        isSameDay(Dates.today(timeZone = timeZone), timeZone = timeZone)

fun Date.isTomorrow(timeZone: TimeZone? = null): Boolean =
        isSameDay(Dates.tomorrow(timeZone = timeZone), timeZone = timeZone)


fun Date.isSameDay(day: Date, timeZone: TimeZone? = null): Boolean {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    currentDate.timeInMillis = this.time

    val anotherDay = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    anotherDay.timeInMillis = day.time

    val sameDay = currentDate.get(Calendar.DAY_OF_YEAR) == anotherDay.get(Calendar.DAY_OF_YEAR)
    val sameYear = currentDate.get(Calendar.YEAR) == anotherDay.get(Calendar.YEAR)

    return sameDay && sameYear
}


fun Date.dateByAddingTimeZoneOffset(timeZone: TimeZone): Date {
    val offset = timeZone.getOffset(this.time)
    return Date(this.time - offset)
}

fun Date.dateByRemovingTimeZoneOffset(timeZone: TimeZone): Date {
    val offset = timeZone.getOffset(this.time)
    return Date(this.time + offset)
}

fun Date.hours(to: Date) =
        TimeUnit.MILLISECONDS.toHours(to.time - this.time)

fun Date.minutes(to: Date) =
        TimeUnit.MILLISECONDS.toMinutes(to.time - this.time)

fun Date.millis(to: Date) =
        TimeUnit.MILLISECONDS.toMillis(to.time - this.time)

fun Date.days(to: Date) =
        TimeUnit.MILLISECONDS.toDays(to.time - this.time)

fun Date.isFuture(): Boolean =
        this.after(Date())

fun Date.isPast(): Boolean =
        this.before(Date())


fun Date.dayOfYear(timeZone: TimeZone? = null): Int {
    val currentDate = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    return currentDate.get(Calendar.DAY_OF_YEAR)
}

fun Date.minutes(timeZone: TimeZone? = null): Int {
    val calendar = Calendar.getInstance(timeZone ?: TimeZone.getDefault())
    calendar.time = this
    return calendar.get(Calendar.MINUTE)
}

fun Date(timeZone: TimeZone): Date {
    val defaultTimeZone = TimeZone.getDefault()
    TimeZone.setDefault(timeZone)
    val date = Date()
    TimeZone.setDefault(defaultTimeZone)

    return date
}