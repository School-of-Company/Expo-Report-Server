package team.startup.report

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import team.startup.report.support.TestClients
import team.startup.report.support.TestJwt

@SpringBootTest
class ExpoReportServerApplicationTests {
    @Test
    fun contextLoads() {
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun jwt(registry: DynamicPropertyRegistry) {
            registry.add("JWT_PUBLIC_KEY") { TestJwt.publicKeyPem }
            TestClients.register(registry)
        }
    }
}
