package ru.mpei.md.qrscanner.data.models

data class AccessDecision(
    val userId: String,
    val eventId: String,
    val status: AccessStatus,
    val decision: DecisionResult?,
    val counter: Int
)

enum class AccessStatus {
    NOT_PASSED,
    ALREADY_PASSED
}

enum class DecisionResult {
    ALLOWED,
    DENIED
}