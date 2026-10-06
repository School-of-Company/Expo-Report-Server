package team.startup.report.domain.excel.service

interface TraineeInfoToExcelService {
    fun execute(expoId: String): ExcelFile
}
