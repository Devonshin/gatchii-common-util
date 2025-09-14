package com.gatchii.common.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@UnitTest
class BCryptPasswordEncoderTest {

    @Test
    @DisplayName("Should return true when raw password matches encoded hash")
    fun shouldMatchWhenPasswordCorrect() {
        // given
        val encoder = BCryptPasswordEncoder()
        val raw = "secret-password"
        val hashed = encoder.encode(raw)

        // expect
        assertTrue(encoder.matches(raw, hashed))
    }

    @Test
    @DisplayName("Should return false when raw password does not match encoded hash")
    fun shouldNotMatchWhenPasswordIncorrect() {
        // given
        val encoder = BCryptPasswordEncoder()
        val raw = "secret-password"
        val hashed = encoder.encode(raw)

        // expect
        assertFalse(encoder.matches("wrong-password", hashed))
    }
}