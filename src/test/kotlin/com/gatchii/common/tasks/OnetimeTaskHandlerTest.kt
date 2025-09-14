package com.gatchii.common.tasks

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions.assertDoesNotThrow

@UnitTest
class OnetimeTaskHandlerTest {

    @Test
    @DisplayName("Should execute only once and remove itself allowing same name to be added again")
    fun shouldExecuteOnceAndRemove() {
        // given
        val name = "one-shot"
        var count = 0
        val handler = OnetimeTaskHandler(name) { count++ }

        // when: add and run via leader executor
        TaskLeadHandler.addTasks(handler)
        TaskLeadHandler.runTasks()

        // then: executed once and removed from registry; original handler won't run again
        handler.startTask() // should do nothing since marked done
        assertEquals(1, count, "OnetimeTaskHandler should execute only once")

        // and: same name can be added again after removal
        assertDoesNotThrow {
            TaskLeadHandler.addTasks(OnetimeTaskHandler(name) { count++ })
        }

        // when: run again, it should execute once more (new instance)
        TaskLeadHandler.runTasks()
        assertEquals(2, count, "Re-added onetime task should execute once again")

        // cleanup
        TaskLeadHandler.removeTask(name)
    }
}