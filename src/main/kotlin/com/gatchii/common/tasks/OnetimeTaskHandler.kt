package com.gatchii.common.tasks

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OnetimeTaskHandler(
    taskName: String,
    private val task: () -> Unit
): TaskLeadHandler(taskName) {

private val logger: Logger = LoggerFactory.getLogger(OnetimeTaskHandler::class.java)

    init {
        logger.info("OnetimeTaskHandler init")
    }
    private var isDone = false

    override fun startTask() {
        if(isDone) return
        task()
        afterTaskSuccess()
    }

    fun afterTaskSuccess() {
        isDone = true
        removeTask(taskName)
    }

}
