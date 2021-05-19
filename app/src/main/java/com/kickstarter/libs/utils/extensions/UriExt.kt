@file:JvmName("UriExt")
package com.kickstarter.libs.utils.extensions

import android.net.Uri

fun Uri.host(): String {
    return this.host ?: ""
}

fun Uri.lastPathSegment(): String {
    return this.lastPathSegment ?: ""
}

fun Uri.path(): String {
    return this.path ?: ""
}

fun Uri.query(): String {
    return this.query ?: ""
}

/**
 * Get token from Uri query params
 * From "at={TOKEN}&ref=ksr_email_user_email_verification" to "{TOKEN}"
 */
fun Uri.getTokenFromQueryParams(): String {
    return this.getQueryParameter("at") ?: ""
}

enum class EmailSubdomains {
    EMAILS("emails.kickstarter.com"),
    ME("me.kickstarter.com"),
    EA("ea.kickstarter.com"),
    CLICKS("clicks.kickstarter.com");

    val host: String
    constructor(text: String) {
        this.host = text
    }

    constructor() {
        this.host = this.name
    }
}

/**
 * Evaluates if the URI host belongs to any of the Email subdomains
 */
fun Uri.isEmailSubdomain(): Boolean {
    return EmailSubdomains.values().any {
        it.host == this.host
    }
}
