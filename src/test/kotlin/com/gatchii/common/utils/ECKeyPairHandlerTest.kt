package com.gatchii.common.utils

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@UnitTest
class ECKeyPairHandlerTest {

    @Test
    @DisplayName("Should verify ECDSA signature when signed with generated EC key")
    fun shouldSignAndVerify() {
        // given
        val kp = ECKeyPairHandler.generateKeyPair()
        val message = "hello-ec-sig"

        // when
        val signature = ECKeyPairHandler.sign(message, kp.private)

        // then
        assertTrue(ECKeyPairHandler.verify(message, kp.public, signature))
    }

    @Test
    @DisplayName("Should convert Base64-encoded keys and reconstruct public from private")
    fun shouldConvertAndReconstructKeys() {
        // given
        val kp = ECKeyPairHandler.generateKeyPair()
        val privatePem = Base64.getEncoder().encodeToString(kp.private.encoded)
        val publicPem = Base64.getEncoder().encodeToString(kp.public.encoded)

        // when
        val restoredPrivate = ECKeyPairHandler.convertPrivateKey(privatePem)
        val restoredPublic = ECKeyPairHandler.convertPublicKey(publicPem)
        val derivedPublic = ECKeyPairHandler.generatePublicKeyFromPrivateKey(restoredPrivate)

        // then
        // public.encoded 비교(바이트 동등성)로 검증
        assertEquals(Base64.getEncoder().encodeToString(restoredPublic.encoded), Base64.getEncoder().encodeToString(derivedPublic.encoded))
    }
}