package team.startup.report.global.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ExpectedException::class)
    fun handleExpectedException(exception: ExpectedException): ResponseEntity<ErrorResponse> =
        errorResponse(exception.status, exception.message)

    // v1 호환: 경로·쿼리 숫자 변환 실패(예: /excel/trainee/abc)를 v1 RuntimeException 처리기처럼 500과 예외 메시지로 응답한다
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(exception: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> =
        errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.message ?: "요청을 처리할 수 없습니다.")

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(exception: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled API exception", exception)
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.")
    }

    // Spring MVC 기본 예외(400, 404, 405, 415 등)는 원래 상태 코드를 유지하고 응답 형식만 맞춘다
    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val message = if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) "잘못된 요청입니다." else "요청을 처리할 수 없습니다."
        return ResponseEntity
            .status(statusCode)
            .headers(headers)
            .body(ErrorResponse(status = statusCode.value(), message = message))
    }

    private fun errorResponse(
        status: HttpStatus,
        message: String,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(status)
            .body(ErrorResponse(status = status.value(), message = message))
}
