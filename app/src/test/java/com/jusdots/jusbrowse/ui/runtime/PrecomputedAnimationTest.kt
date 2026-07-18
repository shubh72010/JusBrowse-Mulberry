package com.jusdots.jusbrowse.ui.runtime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrecomputedAnimationTest {

    @Test
    fun `linear curve starts at 0 and ends at 1`() {
        val curve = PrecomputedAnimations.linear(200, 16)
        assertEquals(0f, curve.valueAt(0f), 0.001f)
        assertEquals(1f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `easeOutCubic starts at 0 and ends at 1`() {
        val curve = PrecomputedAnimations.easeOutCubic(200, 16)
        assertEquals(0f, curve.valueAt(0f), 0.001f)
        assertEquals(1f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `easeOutCubic is monotonic`() {
        val curve = PrecomputedAnimations.easeOutCubic(200, 32)
        for (i in 0..100 step 5) {
            val t = i / 100f
            val t2 = (i + 5) / 100f
            assertTrue("easeOutCubic not monotonic at $t", curve.valueAt(t) <= curve.valueAt(t2))
        }
    }

    @Test
    fun `inOutQuad starts at 0 and ends at 1`() {
        val curve = PrecomputedAnimations.easeInOutQuad(200, 16)
        assertEquals(0f, curve.valueAt(0f), 0.001f)
        assertEquals(1f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `overshoot exceeds 1 mid-curve`() {
        val curve = PrecomputedAnimations.overshoot(300, 32, 0.15f)
        val maxVal = (0..100).maxOf { curve.valueAt(it / 100f) }
        assertTrue("Overshoot never exceeded 1.0", maxVal > 1.0f)
        assertEquals(1f, curve.valueAt(1f), 0.01f)
    }

    @Test
    fun `fadeIn starts at 0 ends at 1`() {
        val curve = PrecomputedAnimations.fadeIn(150, 8)
        assertEquals(0f, curve.valueAt(0f), 0.001f)
        assertEquals(1f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `fadeOut starts at 1 ends at 0`() {
        val curve = PrecomputedAnimations.fadeOut(100, 8)
        assertEquals(1f, curve.valueAt(0f), 0.001f)
        assertEquals(0f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `scalePulse returns to 1 at end`() {
        val curve = PrecomputedAnimations.scalePulse()
        assertEquals(1.0f, curve.valueAt(0f), 0.001f)
        assertEquals(1.0f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `bounce ends at 1`() {
        val curve = PrecomputedAnimations.bounce(400, 32)
        assertEquals(1f, curve.valueAt(1f), 0.001f)
    }

    @Test
    fun `valueAtMs maps time to progress correctly`() {
        val curve = PrecomputedAnimations.linear(200, 16)
        assertEquals(0.5f, curve.valueAtMs(100), 0.01f)
        assertEquals(0.25f, curve.valueAtMs(50), 0.01f)
    }

    @Test
    fun `valueAt clamps progress below 0`() {
        val curve = PrecomputedAnimations.linear(200, 16)
        assertEquals(curve.valueAt(0f), curve.valueAt(-0.5f), 0.001f)
    }

    @Test
    fun `valueAt clamps progress above 1`() {
        val curve = PrecomputedAnimations.linear(200, 16)
        assertEquals(curve.valueAt(1f), curve.valueAt(1.5f), 0.001f)
    }
}
