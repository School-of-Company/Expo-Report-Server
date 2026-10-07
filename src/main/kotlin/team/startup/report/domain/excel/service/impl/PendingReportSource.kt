package team.startup.report.domain.excel.service.impl

import org.springframework.stereotype.Component
import team.startup.report.domain.excel.service.ReportSource

/**
 * ponytail: 공급자 내부 API 계약이 확정·배포되기 전 자리표시자. 가짜 파일을 성공으로 주지 않고 v1 실패 응답으로 끝난다.
 * 교체 조건: Expo-User-Server#44·#43, Expo-Application-Server#21, Expo-Expo-Server#46, Expo-Form-Server#65 확정 후 HTTP 어댑터 구현.
 */
@Component
class PendingReportSource : ReportSource {
    override fun standardParticipants(expoId: String) = unavailable()

    override fun trainees(expoId: String) = unavailable()

    override fun programParticipants(
        expoId: String,
        programId: Long,
    ) = unavailable()

    override fun traineeAttendance(traineeId: Long) = unavailable()

    private fun unavailable(): Nothing = throw IllegalStateException("리포트 원천 데이터 연동이 아직 준비되지 않았습니다.")
}
