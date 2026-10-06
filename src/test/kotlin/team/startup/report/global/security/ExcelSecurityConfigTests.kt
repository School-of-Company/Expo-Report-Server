package team.startup.report.global.security

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import tools.jackson.databind.json.JsonMapper

class ExcelSecurityConfigTests :
    StringSpec({
        "JWT_PUBLIC_KEY가 비어 있으면 원인을 알려 주며 기동에 실패한다" {
            shouldThrow<IllegalArgumentException> { ExcelSecurityConfig(" ", JsonMapper()) }.message shouldContain "JWT_PUBLIC_KEY"
        }
    })
