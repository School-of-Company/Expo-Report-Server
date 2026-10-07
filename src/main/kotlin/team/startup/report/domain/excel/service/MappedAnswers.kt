package team.startup.report.domain.excel.service

import tools.jackson.databind.JsonNode

// User 공급자의 답변(answers)과 제출 당시 문항 스냅샷(questions)을 엑셀 렌더러 입력(문항 제목 → 표시 문자열)으로 바꾼다.
// 기준: Expo-User-Server PR #49 InformationResDto, Expo-Form-Server PR #67 QuestionSnapshot.
//
// - 신청 폼 답변은 문항 제목, 설문 답변은 문항 ID가 키다. 제목은 현재 Form 정의가 아니라 제출 당시 스냅샷에서만 찾는다.
// - answers는 JSONB라 키 순서가 보존되지 않으므로 열 순서는 스냅샷 order를 따른다.
// - v1 셀은 보기 문구 문자열이었다. DROPDOWN 키는 보기 문구, MULTIPLE 키 배열은 보기 문구를 ", "로 잇고(v1 클라이언트와 같은 구분자),
//   CHECKBOX boolean은 "true"/"false", 그 밖의 값은 문자열 그대로다. null 답변은 null로 두어 렌더러가 ""로 쓴다.
// - 제목이나 답변을 만들어 내지 않는다. 복원할 수 없는 답변은 [MappedAnswers.unresolved]로 셀 뿐 열을 추가하지 않는다:
//   스냅샷이 없는 설문 답변, 스냅샷에 없는 문항 ID, 같은 제목의 뒤 문항(order가 앞선 문항만 쓴다), 스냅샷에 없는 선택지 키(저장값을 그대로 쓴다).
// - v1 파싱 결함(연수자 행에 배열이 있으면 폼 열 전체를 비움, 일반 참가자 값에 따옴표·역슬래시가 있으면 행 전체를 비움)은 재현하지 않는다.

/** [values]는 열 순서가 보존된 맵이다. [unresolved]는 개인정보 없이 관측할 복원 불가 답변 건수다. */
data class MappedAnswers(
    val values: Map<String, String?>,
    val unresolved: Int,
)

/** 신청 폼 답변(제목 키). 일반 참가자·연수자·출석부 information 모두 이것을 쓴다. 스냅샷이 없으면 저장된 제목과 값을 그대로 쓴다. */
fun informationAnswers(
    answers: JsonNode,
    questions: JsonNode?,
): MappedAnswers {
    if (!answers.isObject) return MappedAnswers(emptyMap(), 1)
    // 같은 제목이면 order가 앞선 문항의 형식으로 해석한다(답변 키가 제목이라 값은 하나뿐이다)
    val byTitle = LinkedHashMap<String, Question>()
    snapshotOf(questions).orEmpty().forEach { q -> q.title?.let { byTitle.putIfAbsent(it, q) } }

    val counter = Counter()
    val keys = byTitle.keys.filter(answers::has) + answers.propertyNames().filterNot(byTitle::containsKey)
    return MappedAnswers(keys.associateWith { counter.display(answers.get(it), byTitle[it]) }, counter.count)
}

/** 설문 답변(문항 ID 키)을 제출 당시 제목으로 바꾼다. */
fun surveyAnswers(
    answers: JsonNode,
    questions: JsonNode?,
): MappedAnswers {
    if (!answers.isObject) return MappedAnswers(emptyMap(), 1)
    val snapshot = snapshotOf(questions) ?: return MappedAnswers(emptyMap(), answers.size())

    val counter = Counter()
    val ids = snapshot.mapTo(HashSet()) { it.id }
    counter.count += answers.propertyNames().count { it !in ids }
    val values = LinkedHashMap<String, String?>()
    snapshot.forEach { q ->
        val answer = answers.get(q.id) ?: return@forEach
        if (q.title == null || q.title in values) {
            counter.count++
        } else {
            values[q.title] = counter.display(answer, q)
        }
    }
    return MappedAnswers(values, counter.count)
}

private class Question(
    val id: String,
    val title: String?,
    val formType: String?,
    val jsonData: JsonNode?,
)

/** order 순 문항. 스냅샷이 없거나 배열이 아니면 null. */
private fun snapshotOf(questions: JsonNode?): List<Question>? {
    if (questions == null || !questions.isArray) return null
    return questions
        .values()
        .withIndex()
        .filter { it.value.isObject }
        .sortedBy { (index, q) -> q.get("order")?.takeIf { it.isNumber }?.asInt() ?: index }
        .map { (_, q) ->
            Question(
                id = q.get("id")?.asString().orEmpty(),
                title = q.get("title")?.takeIf { it.isString }?.stringValue(),
                formType = q.get("formType")?.takeIf { it.isString }?.stringValue(),
                jsonData = q.get("jsonData")?.takeIf { it.isObject },
            )
        }
}

private class Counter {
    var count = 0

    fun display(
        value: JsonNode?,
        question: Question?,
    ): String? {
        if (value == null || value.isNull) return null
        val choices = question?.jsonData?.takeIf { question.formType == "DROPDOWN" || question.formType == "MULTIPLE" }
        if (choices == null) return plain(value)

        var unknown = false
        val labels =
            (if (value.isArray) value.values() else listOf(value)).map { choice ->
                labelOf(choices, choice) ?: plain(choice).also { unknown = true }
            }
        if (unknown) count++
        return labels.joinToString(", ")
    }

    /** 키면 보기 문구, 이미 보기 문구면 그대로, 둘 다 아니면 null */
    private fun labelOf(
        choices: JsonNode,
        choice: JsonNode,
    ): String? {
        if (!choice.isString) return null
        val key = choice.stringValue()
        choices.get(key)?.let(::choiceText)?.let { return it }
        return key.takeIf { choices.values().any { choiceText(it) == key } }
    }

    /** jsonData 값은 "문구" 또는 {"value": "문구", "isAlwaysSelected": true} */
    private fun choiceText(node: JsonNode): String? =
        when {
            node.isString -> node.stringValue()
            node.isObject -> node.get("value")?.takeIf { it.isString }?.stringValue()
            else -> null
        }

    private fun plain(value: JsonNode): String =
        when {
            value.isString -> value.stringValue()
            value.isArray -> value.values().joinToString(", ") { if (it.isNull) "" else plain(it) }
            value.isValueNode -> value.asString()
            else -> value.toString()
        }
}
