package com.gatchii.common.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@UnitTest
class FileUtilTest {

    @Test
    @DisplayName("Should write file and rotate existing file with timestamp suffix when overwritten")
    fun shouldWriteAndRotate() {
        // given
        val tempDir: Path = Files.createTempDirectory("gatchii-fileutil-test-")
        val filePath = tempDir.resolve("test.txt").toString()

        // when
        FileUtil.writeFile(filePath, "v1")
        val firstRead = FileUtil.readFile(filePath)
        FileUtil.writeFile(filePath, "v2")
        val secondRead = FileUtil.readFile(filePath)

        // then
        assertEquals("v1".length, firstRead?.length)
        assertEquals("v2", secondRead)

        // 회전된 파일이 존재하는지 확인
        val parent = File(filePath).parentFile
        val rotated = parent.listFiles { _, name ->
            name.startsWith("test_") && name.endsWith(".txt")
        }
        assertNotNull(rotated)
        assertTrue(rotated!!.isNotEmpty(), "Rotated file should exist")
    }

    @Test
    @DisplayName("Should return true when file exists and provide absolute path for current directory")
    fun shouldCheckExistAndActualPath() {
        val tempDir: Path = Files.createTempDirectory("gatchii-fileutil-path-")
        val filePath = tempDir.resolve("hello.txt").toString()
        FileUtil.writeFile(filePath, "hello")
        assertTrue(FileUtil.existFile(filePath))
        assertTrue(FileUtil.getActualPath().isNotBlank())
    }
}