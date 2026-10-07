package team.startup.report.support

import org.springframework.test.context.DynamicPropertyRegistry

/** 컨텍스트 기동용 공급자 설정. 실제 주소·토큰이 아니며 호출되지 않는다. */
object TestClients {
    fun register(registry: DynamicPropertyRegistry) {
        listOf("user", "application", "expo").forEach {
            registry.add("clients.$it.url") { "http://127.0.0.1:9" }
            registry.add("clients.$it.internal-token") { "test-$it-internal-token" }
        }
    }
}
