package com.gatchii.common.tasks

import com.gatchii.common.utils.DateUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest

/**
 * Test class for the `RoutineTaskHandler` class.
 *
 * `RoutineTaskHandler` is responsible for scheduling and running tasks based on a given schedule.
 * The `startTask` method starts a coroutine that periodically executes the assigned task according
 * to the schedule defined in a `RoutineScheduleExpression`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@UnitTest
class RoutineTaskHandlerTest {

    @BeforeEach
    fun setUp() {
        DateUtil.initTestDate("RoutineTaskHandler")
    }

    //
    @Test
    fun `test task executes after delay when current time is before scheduled time`() = runTest {
        // given
        val scheduleExpression = RoutineScheduleExpression(hour = 23, minute = 59, second = 59)
        var taskExecutedCount = 0
        val task: () -> Unit = { taskExecutedCount++ }
        val handler = RoutineTaskHandler(
            taskName = "testTask",
            scheduleExpression = scheduleExpression,
            task = task,
            period = 24 * 60 * 60, //1일
            scope = this
        )

        // when
        handler.startTask()
        advanceTimeBy(3 * 24 * 60 * 60 * 1000) //3일
        runCurrent()
        // then
        handler.stopTask()
        assert(taskExecutedCount == 3) { "Task should execute after the scheduled delay time" }
    }

    //
    @Test
    fun `test task executes immediately when current time is after scheduled time`() = runTest {
        // given
        val scheduleExpression = RoutineScheduleExpression(hour = 0, minute = 0, second = 0)
        var taskExecutedCount = 0
        val task: () -> Unit = { taskExecutedCount++ }
        val handler = RoutineTaskHandler(
            taskName = "testTask",
            scheduleExpression = scheduleExpression,
            task = task,
            period = 24 * 60 * 60, //1일
            scope = this
        )

        // when
        handler.startTask()
        advanceTimeBy(3 * 24 * 60 * 60 * 1000) //3일
        runCurrent()
        // then
        handler.stopTask()
        assert(taskExecutedCount == 4) { "Task should execute immediately when current time is after scheduled time" }
    }

    @Test
    @DisplayName("Should execute once when within 30 seconds tolerance of scheduled time")
    fun `should execute once within 30 seconds window`() = runTest {
        // given: schedule is 10 seconds ahead of 'now'
        val now = java.time.LocalDateTime.now()
        val schedule = now.plusSeconds(10)
        val scheduleExpression = RoutineScheduleExpression(
            hour = schedule.hour,
            minute = schedule.minute,
            second = schedule.second
        )
        var taskExecutedCount = 0
        val task: () -> Unit = { taskExecutedCount++ }
        val handler = RoutineTaskHandler(
            taskName = "testTask", // enables test clock
            scheduleExpression = scheduleExpression,
            task = task,
            period = 24 * 60 * 60, // 1 day
            scope = this
        )

        // when
        handler.startTask()
        // advance by 70 seconds (past the first execution, before next day)
        advanceTimeBy(70 * 1000)
        runCurrent()

        // then
        handler.stopTask()
        assert(taskExecutedCount == 1) { "Task should execute once within 30 seconds tolerance and not schedule again until next period" }
    }
}
