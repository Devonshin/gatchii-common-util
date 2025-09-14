package com.gatchii.common.tasks

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import shared.common.UnitTest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import kotlin.test.assertTrue

@UnitTest
class TaskLeadHandlerTest {

    @Test
    @DisplayName("Should run startTask when runTasks is called and leader condition is true")
    fun shouldRunStartTaskWhenLeader() {
        // given
        var started = false
        val task = object : TaskLeadHandler("task1") {
            override fun startTask() { started = true }
        }

        // when
        TaskLeadHandler.addTasks(task)
        TaskLeadHandler.runTasks()

        // then
        assertTrue(started, "startTask should be invoked for leader")

        // cleanup
        TaskLeadHandler.removeTask("task1")
    }

    @Test
    @DisplayName("Should throw when adding duplicate task names")
    fun shouldThrowOnDuplicateTaskName() {
        // given
        val taskName = "dupTask"
        val t1 = object : TaskLeadHandler(taskName) {
            override fun startTask() {}
        }
        TaskLeadHandler.addTasks(t1)

        // expect
        assertThrows<Exception> {
            val t2 = object : TaskLeadHandler(taskName) {
                override fun startTask() {}
            }
            TaskLeadHandler.addTasks(t2)
        }

        // cleanup
        TaskLeadHandler.removeTask(taskName)
    }

    @Test
    @DisplayName("Should allow re-adding the same task name after removal")
    fun shouldAllowReAddAfterRemove() {
        // given
        val taskName = "reAddTask"
        val t1 = object : TaskLeadHandler(taskName) {
            override fun startTask() {}
        }
        TaskLeadHandler.addTasks(t1)
        TaskLeadHandler.removeTask(taskName)

        // expect: no exception on re-add
        assertDoesNotThrow {
            val t2 = object : TaskLeadHandler(taskName) {
                override fun startTask() {}
            }
            TaskLeadHandler.addTasks(t2)
        }

        // cleanup
        TaskLeadHandler.removeTask(taskName)
    }

    @Test
    @DisplayName("Should cancel job when stopTask is called")
    fun shouldCancelJobOnStop() {
        // given
        val t = object : TaskLeadHandler("stopTask") {
            override fun startTask() {}
        }
        val job = kotlinx.coroutines.Job()
        // reflectively set protected var via subclass scope
        t.apply {
            // Kotlin allows access to protected member in same instance scope
            this@apply.javaClass.superclass // no-op to keep scope clear
        }
        // use extension to set the protected property via reflection
        val field = TaskLeadHandler::class.java.getDeclaredField("job")
        field.isAccessible = true
        field.set(t, job)

        // when
        t.stopTask()

        // then
        kotlin.test.assertTrue((field.get(t) as kotlinx.coroutines.Job).isCancelled)
    }
}
