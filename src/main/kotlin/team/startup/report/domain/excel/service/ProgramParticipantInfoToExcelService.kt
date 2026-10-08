package team.startup.report.domain.excel.service

interface ProgramParticipantInfoToExcelService {
    fun execute(
        expoId: String,
        programId: Long,
    ): ExcelFile
}
