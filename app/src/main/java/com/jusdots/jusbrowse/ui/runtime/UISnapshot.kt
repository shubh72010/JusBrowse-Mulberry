package com.jusdots.jusbrowse.ui.runtime

import android.util.JsonReader
import android.util.JsonWriter
import java.io.StringReader
import java.io.StringWriter

data class UISnapshot(
    val toolbarWidthPx: Int = 0,
    val toolbarHeightPx: Int = 0,
    val pillWidthPx: Float = 260f,
    val pillHeightPx: Float = 56f,
    val tabChipCount: Int = 0,
    val activeTabChipIndex: Int = 0,
    val tabChipWidthsPx: FloatArray = floatArrayOf(),
    val securityStateKey: Int = 2,
    val animationPresets: Map<String, Int> = emptyMap(),
    val bottomBarOffsetPx: Float = 0f,
    val isKeyboardVisible: Boolean = false,
    val displayDensity: Float = 2f,
    val displayWidthPx: Int = 0,
    val displayHeightPx: Int = 0
) {
    fun toJson(): String {
        val sw = StringWriter()
        JsonWriter(sw).use { writer ->
            writer.beginObject()
            writer.name("toolbarWidthPx").value(toolbarWidthPx)
            writer.name("toolbarHeightPx").value(toolbarHeightPx)
            writer.name("pillWidthPx").value(pillWidthPx.toDouble())
            writer.name("pillHeightPx").value(pillHeightPx.toDouble())
            writer.name("tabChipCount").value(tabChipCount)
            writer.name("activeTabChipIndex").value(activeTabChipIndex)
            writer.name("securityStateKey").value(securityStateKey)
            writer.name("bottomBarOffsetPx").value(bottomBarOffsetPx.toDouble())
            writer.name("isKeyboardVisible").value(isKeyboardVisible)
            writer.name("displayDensity").value(displayDensity.toDouble())
            writer.name("displayWidthPx").value(displayWidthPx)
            writer.name("displayHeightPx").value(displayHeightPx)

            writer.name("tabChipWidthsPx")
            writer.beginArray()
            for (w in tabChipWidthsPx) {
                writer.value(w.toDouble())
            }
            writer.endArray()

            writer.name("animationPresets")
            writer.beginObject()
            for ((key, value) in animationPresets) {
                writer.name(key).value(value)
            }
            writer.endObject()

            writer.endObject()
        }
        return sw.toString()
    }

    fun estimatedByteSize(): Int {
        return 4 * 10 + tabChipWidthsPx.size * 4 + animationPresets.size * 32 + 128
    }

    companion object {
        fun fromJson(json: String): UISnapshot {
            try {
                val sr = StringReader(json)
                JsonReader(sr).use { reader ->
                    var toolbarWidthPx = 0
                    var toolbarHeightPx = 0
                    var pillWidthPx = 260f
                    var pillHeightPx = 56f
                    var tabChipCount = 0
                    var activeTabChipIndex = 0
                    var tabChipWidthsPx = floatArrayOf()
                    var securityStateKey = 2
                    var animationPresets = emptyMap<String, Int>()
                    var bottomBarOffsetPx = 0f
                    var isKeyboardVisible = false
                    var displayDensity = 2f
                    var displayWidthPx = 0
                    var displayHeightPx = 0

                    reader.beginObject()
                    while (reader.hasNext()) {
                        when (reader.nextName()) {
                            "toolbarWidthPx" -> toolbarWidthPx = reader.nextInt()
                            "toolbarHeightPx" -> toolbarHeightPx = reader.nextInt()
                            "pillWidthPx" -> pillWidthPx = reader.nextDouble().toFloat()
                            "pillHeightPx" -> pillHeightPx = reader.nextDouble().toFloat()
                            "tabChipCount" -> tabChipCount = reader.nextInt()
                            "activeTabChipIndex" -> activeTabChipIndex = reader.nextInt()
                            "securityStateKey" -> securityStateKey = reader.nextInt()
                            "bottomBarOffsetPx" -> bottomBarOffsetPx = reader.nextDouble().toFloat()
                            "isKeyboardVisible" -> isKeyboardVisible = reader.nextBoolean()
                            "displayDensity" -> displayDensity = reader.nextDouble().toFloat()
                            "displayWidthPx" -> displayWidthPx = reader.nextInt()
                            "displayHeightPx" -> displayHeightPx = reader.nextInt()
                            "tabChipWidthsPx" -> {
                                reader.beginArray()
                                val list = mutableListOf<Float>()
                                while (reader.hasNext()) {
                                    list.add(reader.nextDouble().toFloat())
                                }
                                reader.endArray()
                                tabChipWidthsPx = list.toFloatArray()
                            }
                            "animationPresets" -> {
                                reader.beginObject()
                                val map = mutableMapOf<String, Int>()
                                while (reader.hasNext()) {
                                    map[reader.nextName()] = reader.nextInt()
                                }
                                reader.endObject()
                                animationPresets = map
                            }
                        }
                    }
                    reader.endObject()

                    return UISnapshot(
                        toolbarWidthPx = toolbarWidthPx,
                        toolbarHeightPx = toolbarHeightPx,
                        pillWidthPx = pillWidthPx,
                        pillHeightPx = pillHeightPx,
                        tabChipCount = tabChipCount,
                        activeTabChipIndex = activeTabChipIndex,
                        tabChipWidthsPx = tabChipWidthsPx,
                        securityStateKey = securityStateKey,
                        animationPresets = animationPresets,
                        bottomBarOffsetPx = bottomBarOffsetPx,
                        isKeyboardVisible = isKeyboardVisible,
                        displayDensity = displayDensity,
                        displayWidthPx = displayWidthPx,
                        displayHeightPx = displayHeightPx
                    )
                }
            } catch (e: Exception) {
                return UISnapshot()
            }
        }

        val EMPTY = UISnapshot()
    }
}

class UISnapshotStorage(private val maxSnapshots: Int = 3) {
    private val snapshots = ArrayDeque<UISnapshot>(maxSnapshots)

    fun push(snapshot: UISnapshot) {
        if (snapshots.size >= maxSnapshots) {
            snapshots.removeFirst()
        }
        snapshots.addLast(snapshot)
    }

    fun peek(): UISnapshot? = snapshots.lastOrNull()

    fun pop(): UISnapshot? = if (snapshots.isNotEmpty()) snapshots.removeLast() else null

    fun clear() = snapshots.clear()

    fun size(): Int = snapshots.size
}
