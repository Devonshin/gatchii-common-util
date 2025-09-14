package com.gatchii.common.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 * Package: com.gatchii.utils
 * Created: Devonshin
 * Date: 14/02/2025
 */

class FileUtil {
    companion object {

        private val logger: Logger = LoggerFactory.getLogger(FileUtil::class.java)

        fun writeFile(path: String, content: String) {
            try {
                val file = File(path)
                file.parentFile?.mkdirs()
                if (!file.exists()) {
                    file.createNewFile()
                } else {
                    file.renameTo(
                        File(
                            file.parent,
                            "${file.nameWithoutExtension}_${DateUtil.Companion.getCurrentDate().toEpochSecond()}.${file.extension}"
                        ))
                }
                file.writeText(content)
            } catch (ioe: IOException) {
                logger.error("Failed to create file: $path", ioe)
            }
        }

        fun readFile(path: String): String? {
            return try {
                File(path).readText()
            } catch (ioe: IOException) {
                logger.error("Failed to read file: $path", ioe)
                null
            }
        }

        fun existFile(path: String): Boolean {
            return File(path).exists()
        }

        fun getActualPath(): String {
            return File(".").absolutePath
        }
    }
}