package team.startup.report.domain.excel.presentation

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import team.startup.report.domain.excel.service.ExcelFile
import team.startup.report.domain.excel.service.ProgramParticipantInfoToExcelService
import team.startup.report.domain.excel.service.StandardParticipantInfoToExcelService
import team.startup.report.domain.excel.service.TraineeAttendanceToExcelService
import team.startup.report.domain.excel.service.TraineeInfoToExcelService

/** v1 ExcelController와 같은 경로·파라미터·응답(200, 본문은 XLSX 바이트). */
@RestController
@RequestMapping("/excel")
class ExcelController(
    private val traineeInfoToExcelService: TraineeInfoToExcelService,
    private val standardParticipantInfoToExcelService: StandardParticipantInfoToExcelService,
    private val programParticipantInfoToExcelService: ProgramParticipantInfoToExcelService,
    private val traineeAttendanceToExcelService: TraineeAttendanceToExcelService,
) {
    @GetMapping("/{expo_id}")
    fun traineeInfoToExcel(
        @PathVariable("expo_id") expoId: String,
        res: HttpServletResponse,
    ) = traineeInfoToExcelService.execute(expoId).writeTo(res)

    @GetMapping("/standard/{expo_id}")
    fun standardParticipantInfoToExcel(
        @PathVariable("expo_id") expoId: String,
        res: HttpServletResponse,
    ) = standardParticipantInfoToExcelService.execute(expoId).writeTo(res)

    @GetMapping("/program/{expo_id}")
    fun programParticipantInfoToExcel(
        @PathVariable("expo_id") expoId: String,
        @RequestParam("programId") programId: Long,
        res: HttpServletResponse,
    ) = programParticipantInfoToExcelService.execute(expoId, programId).writeTo(res)

    @GetMapping("/trainee/{trainee_id}")
    fun traineeAttendanceToExcel(
        @PathVariable("trainee_id") traineeId: Long,
        res: HttpServletResponse,
    ) = traineeAttendanceToExcelService.execute(traineeId).writeTo(res)

    private fun ExcelFile.writeTo(res: HttpServletResponse) {
        res.contentType = XLSX_CONTENT_TYPE
        res.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        workbook.use { workbook ->
            try {
                res.outputStream.use { workbook.write(it) }
            } finally {
                workbook.dispose()
            }
        }
    }

    private companion object {
        const val XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }
}
