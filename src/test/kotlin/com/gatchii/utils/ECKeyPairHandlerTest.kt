package com.gatchii.utils

import com.gatchii.common.utils.ECKeyPairHandler
import com.gatchii.common.utils.ECKeyPairHandler.Companion.convertPrivateKey
import com.gatchii.common.utils.ECKeyPairHandler.Companion.generatePublicKeyFromPrivateKey
import org.assertj.core.api.Assertions.assertThat
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import java.security.KeyPair
import java.security.PrivateKey
import java.security.Security

/**
 * Package: com.gatchii.unit.utils
 * Created: Devonshin
 * Date: 03/11/2024
 */

@UnitTest
class ECKeyPairHandlerTest {
    companion object {
        init {
            // Add Bouncy Castle Provider for tests
            Security.addProvider(BouncyCastleProvider())
        }
    }

    @Test
    fun `ECDSA enerateKeyPair test`() {
        val keyPair: KeyPair = ECKeyPairHandler.Companion.generateKeyPair()
        assertNotNull(keyPair.private)
        assertNotNull(keyPair.public)
    }

    @Test
    fun `ECDSA signAndVerify test`() {
        val message = "This is a test message"
        val keyPair: KeyPair = ECKeyPairHandler.Companion.generateKeyPair()

        val signature = ECKeyPairHandler.Companion.sign(message, keyPair.private)
        assertNotNull(signature)

        val isVerified = ECKeyPairHandler.Companion.verify(message, keyPair.public, signature)
        assertTrue(isVerified, "Signature should be verified.")
    }

    @Test
    fun `ECDSA InvalidSignature test`() {
        val message = "This is a test message"
        val otherMessage = "This is a different message"
        val keyPair: KeyPair = ECKeyPairHandler.Companion.generateKeyPair()

        val signature = ECKeyPairHandler.Companion.sign(message, keyPair.private)

        val isVerified = ECKeyPairHandler.Companion.verify(otherMessage, keyPair.public, signature)
        assertFalse(isVerified, "Signature should not be verified.")
    }

    @Test
    fun `test if convertPrivateKey generates PrivateKey from PEM format`() {
        // given
        val keyPair: KeyPair = ECKeyPairHandler.Companion.generateKeyPair()
        val privateKeyPem = java.util.Base64.getEncoder().encodeToString(keyPair.private.encoded)
        //println("privateKeyPem = $privateKeyPem")
        // when
        val privateKey: PrivateKey = convertPrivateKey(privateKeyPem)

        //then
        assertThat(privateKey)
            .isEqualTo(keyPair.private)
            .withFailMessage("PrivateKey should not be null for valid PEM format")
    }

    @Test
    fun `test if convertPrivateKey throws exception for invalid PEM format`() {
        // given
        val invalidPemString = "This is an invalid PEM format"
        // when - then
        assertThrows(IllegalArgumentException::class.java) {
            convertPrivateKey(invalidPemString)
        }
    }

    @Test
    fun `test if publicKey from privateKey`() {
        //given
        //val expiredToken = "eyJraWQiOiI4OTMyMDRiNy1lZjg4LTQ2MDAtOWViZi1iMDhlZTVkMjA4ZTYiLCJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJUZXN0QXVkaWVuY2UiLCJpc3MiOiJUZXN0SXNzdWVyIiwiY2xhaW0iOnsidXNlcm5hbWUiOiJ0ZXN0VXNlciIsInVzZXJJZCI6InRlc3RVc2VySWQiLCJyb2xlIjoidXNlciJ9LCJleHAiOjE3MzU2NDM2MzksImp0aSI6IjEyMzQifQ.Ymkr9R_U-qopbXZXJNGKd98hMO6w6Q0rxRIXPJi2-MTUk1ej8cC_ujX9jchMQhj675LaGQkj_cqHm9YzNfZPkg"
        val privateKey = convertPrivateKey("MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDQ+B6qEzr/M2sql4X+09X9YlYt8BKAHX8Q7/6s4KC3qQ==")
        //when
        val publicKey = generatePublicKeyFromPrivateKey(privateKey)
        //val ecdsA256 = Algorithm.ECDSA256(publicKey as ECPublicKey?, privateKey as ECPrivateKey?)
        //val verify = verify(expiredToken, ecdsA256)
        //then
        assertThat(publicKey).isNotNull
    }
}