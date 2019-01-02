package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.parse

sealed class Session(
    open val id: String,
    open val dayNumber: Int,
    open val startTime: DateTime,
    open val endTime: DateTime,
    open val room: Room?
) {
    data class SpeechSession(
        override val id: String,
        override val dayNumber: Int,
        override val startTime: DateTime,
        override val endTime: DateTime,
        val title: String,
        val desc: String,
        override val room: Room,
        val format: String,
        val language: String,
        val topic: Topic,
        val isFavorited: Boolean,
        val speakers: List<Speaker>,
        val message: SessionMessage?
    ) : Session(id, dayNumber, startTime, endTime, room)

    data class SpecialSession(
        override val id: String,
        override val dayNumber: Int,
        override val startTime: DateTime,
        override val endTime: DateTime,
        val title: String,
        override val room: Room?
    ) : Session(id, dayNumber, startTime, endTime, room) {
        companion object {
            private val formatter: DateFormat =
                DateFormat("yyyy-MM-dd'T'HH:mm:ss")

            fun specialSessions() = listOf(
                SpecialSession(
                    "100000",
                    1,
                    formatter.parse("2018-02-08T10:00:00").utc,
                    formatter.parse("2018-02-08T10:20:00").utc,
                    LocaleMessage("Welcome talk", "ウェルカムトーク").get(),
                    Room(513, "Hall")
                )
            )
        }
    }

    val startDayText by lazy { startTime.format("yyyy.M.d") }

    fun timeSummary(lang: Lang) = buildString {
        // ex: 2月2日 10:20-10:40
        if (lang == Lang.EN) {
            append(startTime.format("M"))
            append(".")
            append(startTime.format("d"))
        } else {
            append(startTime.format("M"))
            append("月")
            append(startTime.format("d"))
            append("日")
        }
        append(" ")
        append(startTime.format("hh:mm"))
        append(" - ")
        append(endTime.format("hh:mm"))
    }

    fun summary(lang: Lang) = buildString {
        append(timeSummary(lang))
        append(" / ")
        append(timeInMinutes)
        append("min")
        room?.let {
            append(" / ")
            append(it.name)
        }
    }

    val isFinished: Boolean
        get() = DateTime.nowUnixLong() > endTime.unixMillisLong

    val isOnGoing: Boolean
        get() = DateTime.nowUnixLong() in startTime.unixMillisLong..endTime.unixMillisLong

    val timeInMinutes: Int
        get() = TimeSpan(endTime.unixMillis - startTime.unixMillis).minutes.toInt()
}