package com.cj.d_daywatch.frameworks.helper

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AES256Util{
    companion object{
        private external fun getNativeKey1() : String
        private external fun getNativeKey2() : String

        fun encrypt(string: String?): String {
            System.loadLibrary("keys")

            val keySpec = SecretKeySpec(getNativeKey1().toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, IvParameterSpec(getNativeKey2().toByteArray()))

            val encrypted = cipher.doFinal(string?.toByteArray())

            return Base64.encodeToString(encrypted, 0)
        }

        fun decrypt(encoded : String?) : String{
            System.loadLibrary("keys")

            try {
                val textBytes = Base64.decode(encoded, Base64.DEFAULT)
                val ivSpec = IvParameterSpec(getNativeKey2().toByteArray())
                val key = SecretKeySpec(getNativeKey1().toByteArray(), "AES")
                println(key.toString() + ivSpec.toString())

                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

                return String(cipher.doFinal(textBytes))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return encoded ?: ""
        }
    }
}