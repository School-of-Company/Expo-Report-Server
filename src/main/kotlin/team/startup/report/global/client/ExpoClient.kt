package team.startup.report.global.client

import org.springframework.web.client.RestClient
import team.startup.report.domain.excel.service.TrainingCategory

/** Expo-Expo-Server 내부 조회(PR #50). */
class ExpoClient(
    private val http: RestClient,
) {
    /** 없으면 null */
    fun expo(expoId: String): ExpoSummary? = http.get().uri("/internal/expo/{expoId}", expoId).fetchOrNull(NAME)

    /** 행사가 없거나 프로그램이 그 행사 소속이 아니면 null(공급자는 둘 다 404) */
    fun standardProgram(
        expoId: String,
        programId: Long,
    ): StandardProgramSummary? =
        http.get().uri("/internal/expo/{expoId}/standard-programs/{programId}", expoId, programId).fetchOrNull(NAME)

    /** 하나라도 없거나 다른 행사 소속이면 공급자가 404로 거절하고, 여기서는 실패다. */
    fun trainingPrograms(
        expoId: String,
        programIds: List<Long>,
    ): List<TrainingProgramSummary> {
        require(programIds.size <= MAX_PROGRAM_IDS)
        return http
            .post()
            .uri("/internal/expo/{expoId}/training-programs/batch", expoId)
            .body(mapOf("programIds" to programIds))
            .fetchRequired(NAME)
    }

    companion object {
        const val NAME = "Expo"
        const val MAX_PROGRAM_IDS = 100
    }
}

data class ExpoSummary(
    val title: String?,
)

data class StandardProgramSummary(
    val id: Long,
)

data class TrainingProgramSummary(
    val id: Long,
    val title: String?,
    val startedAt: String,
    val endedAt: String,
    val category: TrainingCategory?,
)
