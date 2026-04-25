package com.jusdots.jusbrowse.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Suspicion Scorer: Monitors browser behavior for fingerprinting attempts.
 * Weighted Scoring Logic:
 * - Canvas call: +20
 * - AudioContext: +30
 * - Accelerometer/Sensor access: +50
 * - Suspicious API burst: +40
 * 
 * Threshold > 100 triggers ADM (Automatic Defense Mode).
 */
object SuspicionScorer {

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private const val THRESHOLD_ADM = 100
    private const val THRESHOLD_VOID = 250

    private var lastReportTime = 0L
    private var pointsInCurrentWindow = 0
    private const val MAX_POINTS_PER_SECOND = 10

    fun reportSuspiciousActivity(points: Int) {
        val now = System.currentTimeMillis()
        if (now - lastReportTime > 1000) {
            // Reset window
            lastReportTime = now
            pointsInCurrentWindow = 0
        }

        if (pointsInCurrentWindow + points <= MAX_POINTS_PER_SECOND) {
            pointsInCurrentWindow += points
            _score.value += points
        }
    }

    fun reset() {
        _score.value = 0
    }

    fun getRecommendedState(): PrivacyState {
        return when {
            _score.value >= THRESHOLD_VOID -> PrivacyState.VOID
            _score.value >= THRESHOLD_ADM -> PrivacyState.ADM
            else -> PrivacyState.RAW
        }
    }
}
