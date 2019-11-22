package com.cyuan.bimibimi.core.utils

import java.util.*

object DateUtils {

    private const val sElapsedFormatHMMSS = "%1\$d:%2\$02d:%3\$02d"
    private const val sElapsedFormatMMSS = "%1\$d:%2\$02d"

    /**
     * Formats an elapsed time in a format like "MM:SS" or "H:MM:SS" (using a form
     * suited to the current locale), similar to that used on the call-in-progress
     * screen.
     *
     * @param recycle [StringBuilder] to recycle, or null to use a temporary one.
     * @param elapsedSeconds the elapsed time in seconds.
     */
    fun formatElapsedTime(recycle: StringBuilder?, elapsedSeconds: Long,
                          formatWithHour: String = sElapsedFormatHMMSS,
                          formatWithMinute: String = sElapsedFormatMMSS): String {
        var elapsedSeconds = elapsedSeconds
        // Break the elapsed seconds into hours, minutes, and seconds.
        var hours: Long = 0
        var minutes: Long = 0
        var seconds: Long = 0
        if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600
            elapsedSeconds -= hours * 3600
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60
            elapsedSeconds -= minutes * 60
        }
        seconds = elapsedSeconds

        // Create a StringBuilder if we weren't given one to recycle.
        // TODO: if we cared, we could have a thread-local temporary StringBuilder.
        var sb = recycle
        if (sb == null) {
            sb = StringBuilder(8)
        } else {
            sb.setLength(0)
        }

        // Format the broken-down time in a locale-appropriate way.
        // TODO: use icu4c when http://unicode.org/cldr/trac/ticket/3407 is fixed.
        val f = Formatter(sb, Locale.getDefault())
        return if (hours > 0) {
            f.format(formatWithHour, hours, minutes, seconds).toString()
        } else {
            f.format(formatWithMinute, minutes, seconds).toString()
        }
    }
}