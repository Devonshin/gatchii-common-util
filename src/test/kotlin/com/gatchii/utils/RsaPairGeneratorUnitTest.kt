package com.gatchii.utils

import com.gatchii.common.utils.PrivateKeyData
import com.gatchii.common.utils.PublicKeyData
import com.gatchii.common.utils.RsaKeyDataPair
import com.gatchii.common.utils.RsaPairHandler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.InvalidKeySpecException
import kotlin.test.assertEquals

/**
 * Package: com.gatchii.utils
 * Created: Devonshin
 * Date: 18/09/2024
 */

@UnitTest
class RsaPairGeneratorUnitTest {

    companion object {
        private lateinit var generateRsaPair: RsaKeyDataPair
        private lateinit var publicKey: PublicKeyData
        private lateinit var privateKey: PrivateKeyData
        private lateinit var textEncrypted: String
        private const val TEXT = "test encrypt string"
        private const val ALGORITHM = "RSA"
        private const val KEY_SIZE = 2048

        private fun generateTestKeyPair(): KeyPair {
            val keyGen = KeyPairGenerator.getInstance(ALGORITHM)
            keyGen.initialize(KEY_SIZE)
            return keyGen.generateKeyPair()
        }
        @BeforeAll
        @JvmStatic
        fun setUp() {
            generateRsaPair = RsaPairHandler.generateRsaDataPair()
            publicKey = generateRsaPair.publicKey
            privateKey = generateRsaPair.privateKey
            textEncrypted = RsaPairHandler.encrypt(TEXT, publicKey.publicKey)
        }
    }

    @Test
    fun `encrypt decrypt test`() {
        //given
        //when
        val decrypt = RsaPairHandler.decrypt(textEncrypted, privateKey.privateKey)
        //then
        println("decrypt: $decrypt")
        println("text: $TEXT")
        assert(TEXT == decrypt)
    }

    @Test
    fun generateRsaPair() {
        //given
        val rsaKeyPair = RsaPairHandler.generateRsaDataPair()
        val publicKey = rsaKeyPair.publicKey
        val privateKey = rsaKeyPair.privateKey
        val textPlain = "test plain string"
        //when
        val encrypt = RsaPairHandler.encrypt(textPlain, publicKey.publicKey)
        val decrypt = RsaPairHandler.decrypt(encrypt, privateKey.privateKey)
        //then
        assertEquals(textPlain, decrypt)
    }

    @Test
    fun `encrypt should throw exception when public key is invalid`() {
        // Arrange
        val invalidPublicKeyStr = "InvalidPublicKey"
        val textToEncrypt = "This is a test"

        // Act & Assert
        assertThrows(Exception::class.java) {
            RsaPairHandler.encrypt(textToEncrypt, invalidPublicKeyStr)
        }
    }

    @Test
    fun `encrypt and decrypt should maintain text integrity`() {
        // Arrange
        val keyPair = generateTestKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        val textToEncrypt = "This is a test"

        // Act
        val encryptedText = RsaPairHandler.encrypt(textToEncrypt, publicKey)
        val decryptedText = RsaPairHandler.decrypt(encryptedText, privateKey)

        // Assert
        Assertions.assertEquals(textToEncrypt, decryptedText)
    }

    @Test
    fun `encrypt should handle empty string`() {
        // Arrange
        val keyPair = generateTestKeyPair()
        val publicKey = keyPair.public
        val textToEncrypt = ""

        // Act
        val encryptedText = RsaPairHandler.encrypt(textToEncrypt, publicKey)
        val decryptedText = RsaPairHandler.decrypt(encryptedText, keyPair.private)

        // Assert
        Assertions.assertEquals(textToEncrypt, decryptedText)
    }

    @Test
    fun `encrypt and decrypt should work with large input`() {
        // Arrange
        val keyPair = generateTestKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        val largeInput = "A".repeat(200)

        // Act
        val encryptedText = RsaPairHandler.encrypt(largeInput, publicKey)
        val decryptedText = RsaPairHandler.decrypt(encryptedText, privateKey)

        // Assert
        Assertions.assertEquals(largeInput, decryptedText)
    }

    /**
     * Tests for the `init` function in the Companion object of `RsaPairHandler`.
     *
     * The `init` function initializes the RSA public and private keys.
     * It takes `publicKeyStr` and `privateKeyStr` as input, converts them to respective key objects,
     * and assigns them to `rsaPublicKey` and `rsaPrivateKey` fields in the Companion object.
     */

    @Test
    fun `after init public key string should successful decrypt`() {
        // Arrange
        //given
        val rsaKeyPair = RsaPairHandler.generateRsaDataPair()
        val publicKey = rsaKeyPair.publicKey
        val privateKey = rsaKeyPair.privateKey
        val textPlain = "test plain string"
        val encrypt = RsaPairHandler.encrypt(textPlain, publicKey.publicKey)
        //when
        RsaPairHandler.init(publicKey.publicKey, privateKey.privateKey)
        val decrypt = RsaPairHandler.decrypt(encrypt, privateKey.privateKey)
        //then
        assertEquals(textPlain, decrypt)
    }

    @Test
    fun `init with invalid public key string throws exception`() {
        // Arrange
        val invalidPublicKeyStr = "InvalidPublicKeyString"
        val privateKeyStr = "-----BEGIN RSA PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG..." // Placeholder

        // Act & Assert
        assertThrows(InvalidKeySpecException::class.java) {
            RsaPairHandler.init(invalidPublicKeyStr, privateKeyStr)
        }
    }

    @Test
    fun `init with invalid private key string throws exception`() {
        // Arrange
        val publicKeyStr = "-----BEGIN RSA PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w==" // Placeholder
        val invalidPrivateKeyStr = "InvalidPrivateKeyString"

        // Act & Assert
        assertThrows(InvalidKeySpecException::class.java) {
            RsaPairHandler.init(publicKeyStr, invalidPrivateKeyStr)
        }
    }

    @Test
    fun `init with empty strings throws exception`() {
        // Arrange
        val emptyPublicKeyStr = ""
        val emptyPrivateKeyStr = ""

        // Act & Assert
        assertThrows(InvalidKeySpecException::class.java) {
            RsaPairHandler.init(emptyPublicKeyStr, emptyPrivateKeyStr)
        }
    }

    /*@Test
    fun generateRsaPubPrvFile() {
        val secretPath = GlobalConfig.getConfigedValue("ktor.secret.path")
        val canonicalPath = File(".").canonicalPath

        logger.info("secretPath: $secretPath, canonicalPath = $canonicalPath")
        val rsaKeyPair = RsaPairHandler.generateRSAKeyPair()
        FileUtil.writeFile("$secretPath/public.pem", "-----BEGIN RSA PUBLIC KEY-----" + rsaKeyPair.public.encoded.encodeBase64() + "-----END RSA PUBLIC KEY-----")
        FileUtil.writeFile("$secretPath/private.pem", "-----BEGIN RSA PRIVATE KEY-----" + rsaKeyPair.private.encoded.encodeBase64() + "-----END RSA PRIVATE KEY-----")
    }*/

}