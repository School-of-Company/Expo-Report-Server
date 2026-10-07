package team.startup.report.global.common.time

import java.time.LocalDateTime

// Expo 프로그램 일시: v2 저장 형식 "yyyy-MM-dd HH:mm", v1 형식 "yyyy-MM-ddTHH:mm" 모두 받는다
fun parseProgramDateTime(value: String): LocalDateTime = LocalDateTime.parse(value.trim().replace(' ', 'T'))
