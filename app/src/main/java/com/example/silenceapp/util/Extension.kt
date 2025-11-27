package com.example.silenceapp.util

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun String.toShortDate(): String {
    return try {
        val odt = OffsetDateTime.parse(this)
        odt.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        this.substringBefore("T")
    }
}