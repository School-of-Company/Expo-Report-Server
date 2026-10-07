package team.startup.report.domain.excel.service.impl

import org.springframework.stereotype.Service
import team.startup.report.domain.excel.service.ExcelFile
import team.startup.report.domain.excel.service.ReportSource
import team.startup.report.domain.excel.service.TraineeAttendanceToExcelService

/** GET /excel/trainee/{trainee_id} */
@Service
class TraineeAttendanceToExcelServiceImpl(
    private val source: ReportSource,
) : TraineeAttendanceToExcelService {
    override fun execute(traineeId: Long): ExcelFile =
        wrapExcelFailure({ "출석부 엑셀 생성 중 오류 발생" }) {
            val attendance = source.traineeAttendance(traineeId) ?: throw excelFailure("해당 연수자를 찾을 수 없습니다.")
            val dates =
                attendance.programs
                    .map { it.startDate }
                    .distinct()
                    .sorted()
            if (dates.isEmpty()) throw excelFailure("해당 연수자가 신청한 프로그램이 없습니다.")
            ExcelFile(
                "attachment; filename=\"Trainee_Attendance_${attendance.traineeName}.xlsx\"",
                renderTraineeAttendance(attendance, dates),
            )
        }
}
