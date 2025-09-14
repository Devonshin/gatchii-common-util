package com.gatchii.common.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@UnitTest
class RsaPairGeneratorUnitTest {

    @Test
    @DisplayName("Should generate RSA key data pair with JWK parameters and Base64-encoded PEM values")
    fun shouldGenerateRsaDataPair() {
        // when
        val pair = RsaPairHandler.generateRsaDataPair()

        // then
        assertNotNull(pair.privateKey.kid)
        assertTrue(pair.privateKey.privateKey.isNotBlank())
        assertNotNull(pair.publicKey.kid)
        assertTrue(pair.publicKey.publicKey.isNotBlank())
        assertTrue(pair.publicKey.n.isNotBlank())
        assertTrue(pair.publicKey.e.isNotBlank())
        assertEquals(pair.privateKey.kid, pair.publicKey.kid)
    }

    @Test
    @DisplayName("Should encrypt and decrypt text using generated RSA key pair strings")
    fun shouldEncryptAndDecryptWithGeneratedStrings() {
        // given
        val pair = RsaPairHandler.generateRsaDataPair()
        val plain = "hello-rsa"

        // when
        val cipher = RsaPairHandler.encrypt(plain, pair.publicKey.publicKey)
        val back = RsaPairHandler.decrypt(cipher, pair.privateKey.privateKey)

        // then
        assertEquals(plain, back)
    }
}