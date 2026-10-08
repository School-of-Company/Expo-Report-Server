package team.startup.report.domain.excel.service

interface TraineeAttendanceToExcelService {
    fun execute(traineeId: Long): ExcelFile
}
