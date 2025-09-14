package com.gatchii.common.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import kotlin.test.assertEquals

@UnitTest
class RsaPairHandlerStaticEncryptDecryptTest {

    @Test
    @DisplayName("Should encrypt and decrypt using statically loaded RSA key pair")
    fun shouldEncryptDecryptWithStaticKeys() {
        // given
        val plain = "static-hello"

        // when
        val cipher = RsaPairHandler.encrypt(plain)
        val back = RsaPairHandler.decrypt(cipher)

        // then
        assertEquals(plain, back)
    }
}