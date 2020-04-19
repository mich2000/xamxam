package be.android.firebase.xamxam.classes

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * static class used to convert string to localdate and vice-versa, but also for shorter
 * function invocations of primitive types.
 *
 * link for localdate/string: https://www.baeldung.com/kotlin-dates
 * **/
class Converter {
    companion object{
        //extension function to change a date string to a localdate
        fun String.toLocalDate():LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)

        //extension function to change a localDate to a string
        fun LocalDate.toStringDate():String = this.format(DateTimeFormatter.ISO_LOCAL_DATE)

        //extension funtion used to convert to lowercase
        fun String.toLow():String = this.toLowerCase(Locale.ROOT)

        //extension function used to convert localdate to calendar, used for calendarview.
        fun LocalDate.toCalendar():Calendar{
            return Calendar.getInstance().also {
                it[Calendar.YEAR] = this.year
                it[Calendar.MONTH] = this.monthValue - 1
                it[Calendar.DAY_OF_MONTH] = this.dayOfMonth
            }
        }
    }
}