package team.startup.report.support

import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.Signature
import java.time.Instant
import java.util.Base64

/** Expo-Expo-Server TestJwt와 같은 RS256 토큰 ({"sub","role","iat","exp"}) */
object TestJwt {
    private val pair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    private val otherPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    private val encoder = Base64.getUrlEncoder().withoutPadding()
    val publicKeyPem: String =
        "-----BEGIN PUBLIC KEY-----\n" +
            Base64.getMimeEncoder(64, "\n".toByteArray()).encodeToString(pair.public.encoded) +
            "\n-----END PUBLIC KEY-----"

    fun token(
        role: String,
        ttlSeconds: Long = 900,
        signedByOtherKey: Boolean = false,
    ): String {
        val now = Instant.now().epochSecond
        val content =
            encode("""{"alg":"RS256","typ":"JWT"}""") + "." +
                encode("""{"sub":"1","role":"$role","iat":$now,"exp":${now + ttlSeconds}}""")
        val signature =
            Signature
                .getInstance("SHA256withRSA")
                .apply {
                    initSign(if (signedByOtherKey) otherPair.private else pair.private)
                    update(content.toByteArray(StandardCharsets.UTF_8))
                }.sign()
        return "$content.${encoder.encodeToString(signature)}"
    }

    private fun encode(value: String): String = encoder.encodeToString(value.toByteArray(StandardCharsets.UTF_8))
}
