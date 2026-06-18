package com.flla.example.core.datastore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenCipher
    @Inject
    constructor() {
        private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

        fun encrypt(plainText: String): String {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            return listOf(cipher.iv, cipherText)
                .joinToString(separator = PART_SEPARATOR) { Base64.encodeToString(it, Base64.NO_WRAP) }
        }

        fun decrypt(encoded: String): String {
            val parts = encoded.split(PART_SEPARATOR)
            require(parts.size == 2) { "Encrypted token payload is malformed." }
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val cipherText = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), GCMParameterSpec(TAG_LENGTH_BITS, iv))
            return cipher.doFinal(cipherText).toString(Charsets.UTF_8)
        }

        private fun getOrCreateSecretKey(): SecretKey {
            val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            if (existing != null) return existing.secretKey

            val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val spec =
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            generator.init(spec)
            return generator.generateKey()
        }

        private companion object {
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val KEY_ALIAS = "com.flla.example.auth_tokens"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val TAG_LENGTH_BITS = 128
            const val PART_SEPARATOR = "."
        }
    }
