package com.gatchii.common.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@UnitTest
class DateUtilTest {

    @Test
    @DisplayName("Should format '1일 1시간 1분 1초' when seconds is 90061")
    fun shouldFormatReadableTime() {
        // given
        val seconds = 90061L // 1d 1h 1m 1s
        // expect
        assertEquals("1일 1시간 1분 1초", DateUtil.toReaderbleTimeFromSeconds(seconds))
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when seconds is negative")
    fun shouldThrowOnNegativeSeconds() {
        assertFailsWith<IllegalArgumentException> {
            DateUtil.toReaderbleTimeFromSeconds(-1)
        }
    }

    @Test
    @DisplayName("Should advance fixed clock by given millis when applyTestDateCount is called")
    fun shouldAdvanceTestClock() {
        // given
        val key = "RoutineTaskHandler"
        DateUtil.initTestDate(key)
        val before = DateUtil.getTestDate(key).millis()

        // when
        val afterClock = DateUtil.applyTestDateCount(key, 1000)

        // then
        val after = afterClock.millis()
        assertTrue(after - before == 1000L, "Clock should advance by 1000 millis")
    }
}