package com.fabsv.believers.believers.util.extension

import android.util.Patterns
import android.webkit.URLUtil
import java.net.MalformedURLException

fun String?.isValidUrl(): Boolean {
    return try {
        URLUtil.isValidUrl(this.orEmpty()) && Patterns.WEB_URL.matcher(this.orEmpty()).matches()
    } catch (ex: MalformedURLException) {
        false
    }
}
