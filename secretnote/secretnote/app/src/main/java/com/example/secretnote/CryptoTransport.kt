import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

/*
 * RSA Key Size: 4096
 * Cipher Type: RSA
 */
class CryptoTransport {

        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadPublicKey(stored: String): Key {
            val data: ByteArray = Base64.getDecoder().decode(stored.toByteArray())
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance("RSA")
            return fact.generatePublic(spec)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(Exception::class)
        fun encryptMessage(plainText: ByteArray, publickey: String): ByteArray {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
            val blockSize = 256
            var offset = 0
            var output:ByteArray = "".toByteArray()
            var flag = true
            while (offset < plainText.size) {
                val block = if (offset + blockSize < plainText.size) {
                    plainText.sliceArray(offset until offset + blockSize)
                } else {
                    plainText.sliceArray(offset until plainText.size)
                }
                val encryptedBlock = cipher.doFinal(block)
                if(flag == true)
                {
                    output =  encryptedBlock
                    flag = false
                }
                else {
                    output = output +encryptedBlock
                }
                offset += blockSize
            }
            return output
        }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun decryptMessage(encryptedText: ByteArray?, privatekey: String): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
        val blockSize = 512
        var offset = 0
        var output:ByteArray = "".toByteArray()
        var flag = true
            while (offset < encryptedText!!.size) {
                val block = if (offset + blockSize < encryptedText!!.size) {
                    val ret = encryptedText!!.sliceArray(offset until offset + blockSize)
                    ret
                } else {
                    val ret = encryptedText!!.sliceArray(offset until encryptedText!!.size)
                    ret
                }
                val decryptedBlock = cipher.doFinal(block)

                if(flag == true)
                {
                    output =  decryptedBlock
                    flag = false
                }
                else {
                    output = output +decryptedBlock
                }
                offset += blockSize
            }

            return output


    }



        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(GeneralSecurityException::class)
        fun loadPrivateKey(key64: String): PrivateKey {
            val clear: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
            val keySpec = PKCS8EncodedKeySpec(clear)
            val fact = KeyFactory.getInstance("RSA")
            val priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }



        data class MyPair(val private: String, val public: String)

        @RequiresApi(Build.VERSION_CODES.O)
        @Throws(GeneralSecurityException::class)
        fun GenerateKeysString(): MyPair//first - private, second - public
        {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(4096)
            val pair = keyGen.generateKeyPair()
            return MyPair(Base64.getEncoder().encodeToString(pair.private.encoded), Base64.getEncoder().encodeToString(pair.public.encoded))
        }


    }