package ru.modemastudio.notepro.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import ru.modemastudio.notepro.App
import ru.modemastudio.notepro.di.AppComponent
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.DATE
import java.util.Calendar.YEAR

fun Context.toast(text: String?) = Toast.makeText(this, text, LENGTH_SHORT).show()
fun Context.toast(resource: Int) = toast(getString(resource))
fun Context.longToast(text: String?) = Toast.makeText(this, text, LENGTH_LONG).show()
fun Context.longToast(resource: Int) = longToast(getString(resource))

fun ViewGroup.addViews(views: List<View>) = views.forEach { addView(it) }

fun Date?.dateTimeString(): String = this?.let { "${dateString()} ${timeString()}" } ?: String()

fun Date?.dateString(): String =
    this?.let { SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(this) } ?: String()

fun Date?.simpleDateString(): String =
    this?.let { SimpleDateFormat("d MMMM", Locale.getDefault()).format(this) } ?: String()

fun Date?.timeString(): String =
    this?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(this) } ?: String()

/**
 * Extension function for [java.util.Date] class.
 * Provides date string based on current date.
 * @return for today - only time,
 *         for date in current year - date without year,
 *         otherwise - full date.
 * */
fun Date?.adaptiveString(): String? = this?.let {
    val today = Calendar.getInstance()
    val date = Calendar.getInstance().apply { time = this@adaptiveString }
    return when {
        date[DATE] == today[DATE] -> timeString()
        date[YEAR] == today[YEAR] -> "${simpleDateString()} ${timeString()}"
        else -> "${dateString()} ${timeString()}"
    }
}

// Extension to determine appComponent from any Contexts child
val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }

fun String?.like(another: String?): Boolean =
    another?.let { this?.trim()?.contains(it.trim(), true) } == true