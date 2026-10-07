package team.startup.report.global.client

import org.springframework.web.client.RestClient

/** Expo-Application-Server 내부 조회(PR #23). 공급자는 ID 오름차순으로 주지만 호출자가 applicationId로 다시 정렬한다. */
class ApplicationClient(
    private val http: RestClient,
) {
    /** 프로그램의 일반 신청. 공급자는 프로그램 존재를 확인하지 않으므로 존재 확인은 Expo로 한다. */
    fun standardApplications(programId: Long): List<StandardApplication> =
        http.get().uri("/internal/standard-program-applications/program/{programId}", programId).fetchRequired(NAME)

    fun trainingApplications(traineeIds: List<Long>): List<TrainingApplication> {
        require(traineeIds.size <= MAX_TRAINEE_IDS)
        return http
            .post()
            .uri("/internal/training-program-applications/trainees")
            .body(mapOf("traineeIds" to traineeIds))
            .fetchRequired(NAME)
    }

    companion object {
        const val NAME = "Application"
        const val MAX_TRAINEE_IDS = 500
    }
}

data class StandardApplication(
    val applicationId: Long,
    val participantId: Long,
)

data class TrainingApplication(
    val applicationId: Long,
    val traineeId: Long,
    val trainingProgramId: Long,
)
