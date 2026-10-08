package team.startup.report.global.exception

import org.springframework.http.HttpStatus

class ExpectedException(
    val status: HttpStatus,
    override val message: String,
) : RuntimeException(message)
