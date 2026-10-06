package team.startup.report.domain.excel.service

interface StandardParticipantInfoToExcelService {
    fun execute(expoId: String): ExcelFile
}
