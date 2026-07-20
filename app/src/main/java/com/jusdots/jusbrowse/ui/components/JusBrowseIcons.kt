package com.jusdots.jusbrowse.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object JusBrowseIcons {

    val Add: ImageVector
        get() {
            if (_Add != null) return _Add!!
            _Add = ImageVector.Builder(
                name = "Add",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 13.0f)
                    lineTo(13.0f, 13.0f)
                    lineTo(13.0f, 19.0f)
                    lineTo(11.0f, 19.0f)
                    lineTo(11.0f, 13.0f)
                    lineTo(5.0f, 13.0f)
                    lineTo(5.0f, 11.0f)
                    lineTo(11.0f, 11.0f)
                    lineTo(11.0f, 5.0f)
                    lineTo(13.0f, 5.0f)
                    lineTo(13.0f, 11.0f)
                    lineTo(19.0f, 11.0f)
                    lineTo(19.0f, 13.0f)
                    close()
                }
            }.build()
            return _Add!!
        }

    private var _Add: ImageVector? = null

    val AddBox: ImageVector
        get() {
            if (_AddBox != null) return _AddBox!!
            _AddBox = ImageVector.Builder(
                name = "AddBox",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 3.0f)
                    lineTo(5.0f, 3.0f)
                    curveTo(3.8899999999999997f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                    lineTo(3.0f, 19.0f)
                    lineTo(17.0f, 19.0f)
                    curveTo(18.1f, 19.0f, 19.0f, 18.1f, 19.0f, 17.0f)
                    lineTo(19.0f, 5.0f)
                    curveTo(19.0f, 3.9f, 18.1f, 3.0f, 17.0f, 3.0f)
                    close()
                    moveTo(17.0f, 13.0f)
                    lineTo(13.0f, 13.0f)
                    lineTo(13.0f, 17.0f)
                    lineTo(11.0f, 17.0f)
                    lineTo(11.0f, 13.0f)
                    lineTo(7.0f, 13.0f)
                    lineTo(7.0f, 11.0f)
                    lineTo(11.0f, 11.0f)
                    lineTo(11.0f, 7.0f)
                    lineTo(13.0f, 7.0f)
                    lineTo(13.0f, 11.0f)
                    lineTo(17.0f, 11.0f)
                    lineTo(17.0f, 13.0f)
                    close()
                }
            }.build()
            return _AddBox!!
        }

    private var _AddBox: ImageVector? = null

    val ArrowBack: ImageVector
        get() {
            if (_ArrowBack != null) return _ArrowBack!!
            _ArrowBack = ImageVector.Builder(
                name = "ArrowBack",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(20.0f, 11.0f)
                    lineTo(7.83f, 11.0f)
                    lineTo(13.42f, 5.41f)
                    lineTo(12.0f, 4.0f)
                    lineTo(4.0f, 12.0f)
                    lineTo(12.0f, 20.0f)
                    lineTo(13.41f, 18.59f)
                    lineTo(7.83f, 13.0f)
                    lineTo(20.0f, 13.0f)
                    lineTo(20.0f, 11.0f)
                    close()
                }
            }.build()
            return _ArrowBack!!
        }

    private var _ArrowBack: ImageVector? = null

    val ArrowBackIosNew: ImageVector
        get() {
            if (_ArrowBackIosNew != null) return _ArrowBackIosNew!!
            _ArrowBackIosNew = ImageVector.Builder(
                name = "ArrowBackIosNew",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(17.77f, 3.77f)
                    lineTo(16.0f, 2.0f)
                    lineTo(6.0f, 12.0f)
                    lineTo(16.0f, 22.0f)
                    lineTo(17.77f, 20.23f)
                    lineTo(9.54f, 12.0f)
                }
            }.build()
            return _ArrowBackIosNew!!
        }

    private var _ArrowBackIosNew: ImageVector? = null

    val ArrowForward: ImageVector
        get() {
            if (_ArrowForward != null) return _ArrowForward!!
            _ArrowForward = ImageVector.Builder(
                name = "ArrowForward",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 4.0f)
                    lineTo(10.59f, 5.41f)
                    lineTo(16.17f, 11.0f)
                    lineTo(4.0f, 11.0f)
                    lineTo(4.0f, 13.0f)
                    lineTo(16.17f, 13.0f)
                    lineTo(10.590000000000002f, 18.59f)
                    lineTo(12.0f, 20.0f)
                    lineTo(20.0f, 12.0f)
                    close()
                }
            }.build()
            return _ArrowForward!!
        }

    private var _ArrowForward: ImageVector? = null

    val ArrowForwardIos: ImageVector
        get() {
            if (_ArrowForwardIos != null) return _ArrowForwardIos!!
            _ArrowForwardIos = ImageVector.Builder(
                name = "ArrowForwardIos",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(6.23f, 20.23f)
                    lineTo(8.0f, 22.0f)
                    lineTo(18.0f, 12.0f)
                    lineTo(8.0f, 2.0f)
                    lineTo(6.23f, 3.77f)
                    lineTo(14.46f, 12.0f)
                }
            }.build()
            return _ArrowForwardIos!!
        }

    private var _ArrowForwardIos: ImageVector? = null

    val ArrowDropDown: ImageVector
        get() {
            if (_ArrowDropDown != null) return _ArrowDropDown!!
            _ArrowDropDown = ImageVector.Builder(
                name = "ArrowDropDown",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(7.0f, 10.0f)
                    lineTo(12.0f, 15.0f)
                    lineTo(17.0f, 10.0f)
                    close()
                }
            }.build()
            return _ArrowDropDown!!
        }

    private var _ArrowDropDown: ImageVector? = null

    val ArrowDropUp: ImageVector
        get() {
            if (_ArrowDropUp != null) return _ArrowDropUp!!
            _ArrowDropUp = ImageVector.Builder(
                name = "ArrowDropUp",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(7.0f, 14.0f)
                    lineTo(12.0f, 9.0f)
                    lineTo(17.0f, 14.0f)
                    close()
                }
            }.build()
            return _ArrowDropUp!!
        }

    private var _ArrowDropUp: ImageVector? = null

    val Assignment: ImageVector
        get() {
            if (_Assignment != null) return _Assignment!!
            _Assignment = ImageVector.Builder(
                name = "Assignment",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 3.0f)
                    lineTo(14.82f, 3.0f)
                    curveTo(14.4f, 1.84f, 13.3f, 1.0f, 12.0f, 1.0f)
                    lineTo(5.0f, 1.0f)
                    curveTo(3.9f, 1.0f, 3.0f, 1.9f, 3.0f, 3.0f)
                    lineTo(3.0f, 17.0f)
                    lineTo(17.0f, 17.0f)
                    curveTo(18.1f, 17.0f, 19.0f, 16.1f, 19.0f, 15.0f)
                    lineTo(19.0f, 5.0f)
                    curveTo(19.0f, 3.9f, 18.1f, 3.0f, 17.0f, 3.0f)
                    close()
                    moveTo(12.0f, 3.0f)
                    curveTo(12.55f, 3.0f, 13.0f, 3.45f, 13.0f, 4.0f)
                    curveTo(13.0f, 4.0f, 12.55f, 5.0f, 12.0f, 5.0f)
                    curveTo(12.0f, 5.0f, 11.0f, 4.55f, 11.0f, 4.0f)
                    curveTo(11.0f, 4.0f, 11.45f, 3.0f, 12.0f, 3.0f)
                    close()
                    moveTo(14.0f, 17.0f)
                    lineTo(7.0f, 17.0f)
                    lineTo(7.0f, 15.0f)
                    lineTo(14.0f, 15.0f)
                    lineTo(14.0f, 17.0f)
                    close()
                    moveTo(17.0f, 13.0f)
                    lineTo(7.0f, 13.0f)
                    lineTo(7.0f, 11.0f)
                    lineTo(17.0f, 11.0f)
                    lineTo(17.0f, 13.0f)
                    close()
                    moveTo(17.0f, 9.0f)
                    lineTo(7.0f, 9.0f)
                    lineTo(7.0f, 7.0f)
                    lineTo(17.0f, 7.0f)
                    lineTo(17.0f, 9.0f)
                    close()
                }
            }.build()
            return _Assignment!!
        }

    private var _Assignment: ImageVector? = null

    val Audiotrack: ImageVector
        get() {
            if (_Audiotrack != null) return _Audiotrack!!
            _Audiotrack = ImageVector.Builder(
                name = "Audiotrack",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 3.0f)
                    lineTo(12.0f, 12.28f)
                    curveTo(11.53f, 12.11f, 11.03f, 12.0f, 10.5f, 12.0f)
                    curveTo(8.01f, 12.0f, 6.0f, 14.01f, 6.0f, 16.5f)
                    curveTo(6.0f, 16.5f, 8.01f, 21.0f, 10.5f, 21.0f)
                    curveTo(12.81f, 21.0f, 14.7f, 19.25f, 14.95f, 17.0f)
                    lineTo(15.0f, 17.0f)
                    lineTo(15.0f, 6.0f)
                    lineTo(19.0f, 6.0f)
                    lineTo(19.0f, 3.0f)
                    lineTo(12.0f, 3.0f)
                    close()
                }
            }.build()
            return _Audiotrack!!
        }

    private var _Audiotrack: ImageVector? = null

    val AutoAwesome: ImageVector
        get() {
            if (_AutoAwesome != null) return _AutoAwesome!!
            _AutoAwesome = ImageVector.Builder(
                name = "AutoAwesome",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 9.0f)
                    lineTo(20.25f, 6.25f)
                    lineTo(23.0f, 5.0f)
                    lineTo(20.25f, 3.75f)
                    lineTo(19.0f, 1.0f)
                    lineTo(17.75f, 3.75f)
                    lineTo(15.0f, 5.0f)
                    lineTo(17.75f, 6.25f)
                    lineTo(19.0f, 9.0f)
                    close()
                    lineTo(9.0f, 4.0f)
                    lineTo(6.5f, 9.5f)
                    lineTo(1.0f, 12.0f)
                    lineTo(6.5f, 14.5f)
                    lineTo(9.0f, 20.0f)
                    lineTo(11.5f, 14.5f)
                    lineTo(17.0f, 12.0f)
                    lineTo(11.5f, 9.5f)
                    close()
                    moveTo(19.0f, 15.0f)
                    lineTo(17.75f, 17.75f)
                    lineTo(15.0f, 19.0f)
                    lineTo(17.75f, 20.25f)
                    lineTo(19.0f, 23.0f)
                    lineTo(20.25f, 20.25f)
                    lineTo(23.0f, 19.0f)
                    lineTo(20.25f, 17.75f)
                    lineTo(19.0f, 15.0f)
                    close()
                }
            }.build()
            return _AutoAwesome!!
        }

    private var _AutoAwesome: ImageVector? = null

    val Block: ImageVector
        get() {
            if (_Block != null) return _Block!!
            _Block = ImageVector.Builder(
                name = "Block",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 6.48f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(4.0f, 12.0f)
                    curveTo(4.0f, 7.58f, 7.58f, 4.0f, 12.0f, 4.0f)
                    lineTo(5.69f, 16.9f)
                    curveTo(4.63f, 15.55f, 4.0f, 13.85f, 4.0f, 12.0f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveTo(10.15f, 20.0f, 8.45f, 19.37f, 7.1f, 18.31f)
                    lineTo(18.31f, 7.1f)
                    curveTo(19.37f, 8.45f, 20.0f, 10.15f, 20.0f, 12.0f)
                    curveTo(20.0f, 16.42f, 16.42f, 20.0f, 12.0f, 20.0f)
                    close()
                }
            }.build()
            return _Block!!
        }

    private var _Block: ImageVector? = null

    val Check: ImageVector
        get() {
            if (_Check != null) return _Check!!
            _Check = ImageVector.Builder(
                name = "Check",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(9.0f, 16.17f)
                    lineTo(4.83f, 12.0f)
                    lineTo(3.41f, 13.41f)
                    lineTo(9.0f, 19.0f)
                    lineTo(21.0f, 7.0f)
                    lineTo(19.59f, 5.59f)
                    close()
                }
            }.build()
            return _Check!!
        }

    private var _Check: ImageVector? = null

    val Clear: ImageVector
        get() {
            if (_Clear != null) return _Clear!!
            _Clear = ImageVector.Builder(
                name = "Clear",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 6.41f)
                    lineTo(17.59f, 5.0f)
                    lineTo(12.0f, 10.59f)
                    lineTo(6.41f, 5.0f)
                    lineTo(5.0f, 6.41f)
                    lineTo(10.59f, 12.0f)
                    lineTo(5.0f, 17.59f)
                    lineTo(6.41f, 19.0f)
                    lineTo(12.0f, 13.41f)
                    lineTo(17.59f, 19.0f)
                    lineTo(19.0f, 17.59f)
                    lineTo(13.41f, 12.0f)
                    close()
                }
            }.build()
            return _Clear!!
        }

    private var _Clear: ImageVector? = null

    val Close: ImageVector
        get() {
            if (_Close != null) return _Close!!
            _Close = ImageVector.Builder(
                name = "Close",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 6.41f)
                    lineTo(17.59f, 5.0f)
                    lineTo(12.0f, 10.59f)
                    lineTo(6.41f, 5.0f)
                    lineTo(5.0f, 6.41f)
                    lineTo(10.59f, 12.0f)
                    lineTo(5.0f, 17.59f)
                    lineTo(6.41f, 19.0f)
                    lineTo(12.0f, 13.41f)
                    lineTo(17.59f, 19.0f)
                    lineTo(19.0f, 17.59f)
                    lineTo(13.41f, 12.0f)
                    close()
                }
            }.build()
            return _Close!!
        }

    private var _Close: ImageVector? = null

    val ContentCopy: ImageVector
        get() {
            if (_ContentCopy != null) return _ContentCopy!!
            _ContentCopy = ImageVector.Builder(
                name = "ContentCopy",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(16.0f, 1.0f)
                    lineTo(4.0f, 1.0f)
                    curveTo(2.9f, 1.0f, 2.0f, 1.9f, 2.0f, 3.0f)
                    lineTo(2.0f, 17.0f)
                    lineTo(4.0f, 17.0f)
                    lineTo(4.0f, 3.0f)
                    lineTo(16.0f, 3.0f)
                    lineTo(16.0f, 1.0f)
                    close()
                    moveTo(19.0f, 5.0f)
                    lineTo(8.0f, 5.0f)
                    curveTo(6.9f, 5.0f, 6.0f, 5.9f, 6.0f, 7.0f)
                    lineTo(6.0f, 21.0f)
                    lineTo(17.0f, 21.0f)
                    curveTo(18.1f, 21.0f, 19.0f, 20.1f, 19.0f, 19.0f)
                    lineTo(19.0f, 7.0f)
                    curveTo(19.0f, 5.9f, 18.1f, 5.0f, 17.0f, 5.0f)
                    close()
                    moveTo(19.0f, 21.0f)
                    lineTo(8.0f, 21.0f)
                    lineTo(8.0f, 7.0f)
                    lineTo(19.0f, 7.0f)
                    lineTo(19.0f, 21.0f)
                    close()
                }
            }.build()
            return _ContentCopy!!
        }

    private var _ContentCopy: ImageVector? = null

    val CropSquare: ImageVector
        get() {
            if (_CropSquare != null) return _CropSquare!!
            _CropSquare = ImageVector.Builder(
                name = "CropSquare",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(18.0f, 4.0f)
                    lineTo(6.0f, 4.0f)
                    curveTo(4.9f, 4.0f, 4.0f, 4.9f, 4.0f, 6.0f)
                    lineTo(4.0f, 18.0f)
                    lineTo(16.0f, 18.0f)
                    curveTo(17.1f, 18.0f, 18.0f, 17.1f, 18.0f, 16.0f)
                    lineTo(18.0f, 6.0f)
                    curveTo(18.0f, 4.9f, 17.1f, 4.0f, 16.0f, 4.0f)
                    close()
                    moveTo(18.0f, 18.0f)
                    lineTo(6.0f, 18.0f)
                    lineTo(6.0f, 6.0f)
                    lineTo(18.0f, 6.0f)
                    lineTo(18.0f, 18.0f)
                    close()
                }
            }.build()
            return _CropSquare!!
        }

    private var _CropSquare: ImageVector? = null

    val DateRange: ImageVector
        get() {
            if (_DateRange != null) return _DateRange!!
            _DateRange = ImageVector.Builder(
                name = "DateRange",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(9.0f, 11.0f)
                    lineTo(7.0f, 11.0f)
                    lineTo(7.0f, 13.0f)
                    lineTo(9.0f, 13.0f)
                    lineTo(9.0f, 11.0f)
                    close()
                    moveTo(13.0f, 11.0f)
                    lineTo(11.0f, 11.0f)
                    lineTo(11.0f, 13.0f)
                    lineTo(13.0f, 13.0f)
                    lineTo(13.0f, 11.0f)
                    close()
                    moveTo(17.0f, 11.0f)
                    lineTo(15.0f, 11.0f)
                    lineTo(15.0f, 13.0f)
                    lineTo(17.0f, 13.0f)
                    lineTo(17.0f, 11.0f)
                    close()
                    moveTo(19.0f, 4.0f)
                    lineTo(18.0f, 4.0f)
                    lineTo(18.0f, 2.0f)
                    lineTo(16.0f, 2.0f)
                    lineTo(16.0f, 4.0f)
                    lineTo(8.0f, 4.0f)
                    lineTo(8.0f, 2.0f)
                    lineTo(6.0f, 2.0f)
                    lineTo(6.0f, 4.0f)
                    lineTo(5.0f, 4.0f)
                    lineTo(3.0f, 20.0f)
                    lineTo(17.0f, 20.0f)
                    curveTo(18.1f, 20.0f, 19.0f, 19.1f, 19.0f, 18.0f)
                    lineTo(19.0f, 6.0f)
                    curveTo(19.0f, 4.9f, 18.1f, 4.0f, 17.0f, 4.0f)
                    close()
                    moveTo(19.0f, 20.0f)
                    lineTo(5.0f, 20.0f)
                    lineTo(5.0f, 9.0f)
                    lineTo(19.0f, 9.0f)
                    lineTo(19.0f, 20.0f)
                    close()
                }
            }.build()
            return _DateRange!!
        }

    private var _DateRange: ImageVector? = null

    val Delete: ImageVector
        get() {
            if (_Delete != null) return _Delete!!
            _Delete = ImageVector.Builder(
                name = "Delete",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(6.0f, 19.0f)
                    lineTo(14.0f, 19.0f)
                    curveTo(15.1f, 19.0f, 16.0f, 18.1f, 16.0f, 17.0f)
                    lineTo(16.0f, 7.0f)
                    lineTo(6.0f, 7.0f)
                    lineTo(6.0f, 19.0f)
                    close()
                    moveTo(19.0f, 4.0f)
                    lineTo(15.5f, 4.0f)
                    lineTo(14.5f, 3.0f)
                    lineTo(9.5f, 3.0f)
                    lineTo(8.5f, 4.0f)
                    lineTo(5.0f, 4.0f)
                    lineTo(5.0f, 6.0f)
                    lineTo(19.0f, 6.0f)
                    lineTo(19.0f, 4.0f)
                    close()
                }
            }.build()
            return _Delete!!
        }

    private var _Delete: ImageVector? = null

    val Download: ImageVector
        get() {
            if (_Download != null) return _Download!!
            _Download = ImageVector.Builder(
                name = "Download",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(5.0f, 20.0f)
                    lineTo(19.0f, 20.0f)
                    lineTo(19.0f, 18.0f)
                    lineTo(5.0f, 18.0f)
                    lineTo(5.0f, 20.0f)
                    close()
                    moveTo(19.0f, 9.0f)
                    lineTo(15.0f, 9.0f)
                    lineTo(15.0f, 3.0f)
                    lineTo(9.0f, 3.0f)
                    lineTo(9.0f, 9.0f)
                    lineTo(5.0f, 9.0f)
                    lineTo(12.0f, 16.0f)
                    lineTo(19.0f, 9.0f)
                    close()
                }
            }.build()
            return _Download!!
        }

    private var _Download: ImageVector? = null

    val FastForward: ImageVector
        get() {
            if (_FastForward != null) return _FastForward!!
            _FastForward = ImageVector.Builder(
                name = "FastForward",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(4.0f, 18.0f)
                    lineTo(12.5f, 12.0f)
                    lineTo(4.0f, 6.0f)
                    lineTo(4.0f, 18.0f)
                    close()
                    moveTo(13.0f, 6.0f)
                    lineTo(13.0f, 18.0f)
                    lineTo(21.5f, 12.0f)
                    lineTo(13.0f, 6.0f)
                    close()
                }
            }.build()
            return _FastForward!!
        }

    private var _FastForward: ImageVector? = null

    val FolderOpen: ImageVector
        get() {
            if (_FolderOpen != null) return _FolderOpen!!
            _FolderOpen = ImageVector.Builder(
                name = "FolderOpen",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(20.0f, 6.0f)
                    lineTo(12.0f, 6.0f)
                    lineTo(10.0f, 4.0f)
                    lineTo(4.0f, 4.0f)
                    lineTo(2.0f, 18.0f)
                    lineTo(18.0f, 18.0f)
                    curveTo(19.1f, 18.0f, 20.0f, 17.1f, 20.0f, 16.0f)
                    lineTo(20.0f, 8.0f)
                    curveTo(20.0f, 6.9f, 19.1f, 6.0f, 18.0f, 6.0f)
                    close()
                    moveTo(20.0f, 18.0f)
                    lineTo(4.0f, 18.0f)
                    lineTo(4.0f, 8.0f)
                    lineTo(20.0f, 8.0f)
                    lineTo(20.0f, 18.0f)
                    close()
                }
            }.build()
            return _FolderOpen!!
        }

    private var _FolderOpen: ImageVector? = null

    val GraphicEq: ImageVector
        get() {
            if (_GraphicEq != null) return _GraphicEq!!
            _GraphicEq = ImageVector.Builder(
                name = "GraphicEq",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(7.0f, 18.0f)
                    lineTo(9.0f, 18.0f)
                    lineTo(9.0f, 6.0f)
                    lineTo(7.0f, 6.0f)
                    lineTo(7.0f, 18.0f)
                    close()
                    moveTo(11.0f, 22.0f)
                    lineTo(13.0f, 22.0f)
                    lineTo(13.0f, 2.0f)
                    lineTo(11.0f, 2.0f)
                    lineTo(11.0f, 22.0f)
                    close()
                    moveTo(3.0f, 14.0f)
                    lineTo(5.0f, 14.0f)
                    lineTo(5.0f, 10.0f)
                    lineTo(3.0f, 10.0f)
                    lineTo(3.0f, 14.0f)
                    close()
                    moveTo(15.0f, 18.0f)
                    lineTo(17.0f, 18.0f)
                    lineTo(17.0f, 6.0f)
                    lineTo(15.0f, 6.0f)
                    lineTo(15.0f, 18.0f)
                    close()
                    moveTo(19.0f, 10.0f)
                    lineTo(19.0f, 14.0f)
                    lineTo(21.0f, 14.0f)
                    lineTo(21.0f, 10.0f)
                    lineTo(19.0f, 10.0f)
                    close()
                }
            }.build()
            return _GraphicEq!!
        }

    private var _GraphicEq: ImageVector? = null

    val GridView: ImageVector
        get() {
            if (_GridView != null) return _GridView!!
            _GridView = ImageVector.Builder(
                name = "GridView",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(3.0f, 3.0f)
                    lineTo(3.0f, 11.0f)
                    lineTo(11.0f, 11.0f)
                    lineTo(11.0f, 3.0f)
                    lineTo(3.0f, 3.0f)
                    close()
                    moveTo(9.0f, 9.0f)
                    lineTo(5.0f, 9.0f)
                    lineTo(5.0f, 5.0f)
                    lineTo(9.0f, 5.0f)
                    lineTo(9.0f, 9.0f)
                    close()
                    moveTo(3.0f, 13.0f)
                    lineTo(3.0f, 21.0f)
                    lineTo(11.0f, 21.0f)
                    lineTo(11.0f, 13.0f)
                    lineTo(3.0f, 13.0f)
                    close()
                    moveTo(9.0f, 19.0f)
                    lineTo(5.0f, 19.0f)
                    lineTo(5.0f, 15.0f)
                    lineTo(9.0f, 15.0f)
                    lineTo(9.0f, 19.0f)
                    close()
                    moveTo(13.0f, 3.0f)
                    lineTo(13.0f, 11.0f)
                    lineTo(21.0f, 11.0f)
                    lineTo(21.0f, 3.0f)
                    lineTo(13.0f, 3.0f)
                    close()
                    moveTo(19.0f, 9.0f)
                    lineTo(15.0f, 9.0f)
                    lineTo(15.0f, 5.0f)
                    lineTo(19.0f, 5.0f)
                    lineTo(19.0f, 9.0f)
                    close()
                    moveTo(13.0f, 13.0f)
                    lineTo(13.0f, 21.0f)
                    lineTo(21.0f, 21.0f)
                    lineTo(21.0f, 13.0f)
                    lineTo(13.0f, 13.0f)
                    close()
                    moveTo(19.0f, 19.0f)
                    lineTo(15.0f, 19.0f)
                    lineTo(15.0f, 15.0f)
                    lineTo(19.0f, 15.0f)
                    lineTo(19.0f, 19.0f)
                    close()
                }
            }.build()
            return _GridView!!
        }

    private var _GridView: ImageVector? = null

    val History: ImageVector
        get() {
            if (_History != null) return _History!!
            _History = ImageVector.Builder(
                name = "History",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(13.0f, 3.0f)
                    curveTo(8.030000000000001f, 3.0f, 4.0f, 7.03f, 4.0f, 12.0f)
                    lineTo(1.0f, 12.0f)
                    lineTo(9.0f, 12.0f)
                    lineTo(6.0f, 12.0f)
                    curveTo(6.0f, 8.129999999999999f, 9.129999999999999f, 5.0f, 13.0f, 5.0f)
                    curveTo(13.0f, 5.0f, 20.0f, 8.129999999999999f, 20.0f, 12.0f)
                    curveTo(20.0f, 12.0f, 16.87f, 19.0f, 13.0f, 19.0f)
                    curveTo(11.07f, 19.0f, 9.32f, 18.21f, 8.059999999999999f, 16.94f)
                    lineTo(6.639999999999999f, 18.36f)
                    curveTo(8.27f, 19.99f, 10.51f, 21.0f, 13.0f, 21.0f)
                    curveTo(17.97f, 21.0f, 22.0f, 16.97f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.97f, 3.0f, 13.0f, 3.0f)
                    close()
                    moveTo(12.0f, 8.0f)
                    lineTo(12.0f, 13.0f)
                    lineTo(16.28f, 11.79f)
                    lineTo(12.780000000000001f, 9.709999999999999f)
                    lineTo(12.780000000000001f, 8.0f)
                    lineTo(12.0f, 8.0f)
                    close()
                }
            }.build()
            return _History!!
        }

    private var _History: ImageVector? = null

    val Home: ImageVector
        get() {
            if (_Home != null) return _Home!!
            _Home = ImageVector.Builder(
                name = "Home",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(10.0f, 20.0f)
                    lineTo(10.0f, 14.0f)
                    lineTo(14.0f, 14.0f)
                    lineTo(14.0f, 20.0f)
                    lineTo(19.0f, 20.0f)
                    lineTo(19.0f, 12.0f)
                    lineTo(22.0f, 12.0f)
                    lineTo(12.0f, 3.0f)
                    lineTo(2.0f, 12.0f)
                    lineTo(5.0f, 12.0f)
                    lineTo(5.0f, 20.0f)
                    close()
                }
            }.build()
            return _Home!!
        }

    private var _Home: ImageVector? = null

    val Image: ImageVector
        get() {
            if (_Image != null) return _Image!!
            _Image = ImageVector.Builder(
                name = "Image",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(21.0f, 19.0f)
                    lineTo(21.0f, 5.0f)
                    curveTo(21.0f, 3.9f, 20.1f, 3.0f, 19.0f, 3.0f)
                    lineTo(5.0f, 3.0f)
                    curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                    lineTo(3.0f, 19.0f)
                    lineTo(17.0f, 19.0f)
                    curveTo(18.1f, 19.0f, 19.0f, 18.1f, 19.0f, 17.0f)
                    close()
                    moveTo(8.5f, 13.5f)
                    lineTo(11.0f, 16.509999999999998f)
                    lineTo(14.5f, 12.0f)
                    lineTo(19.0f, 18.0f)
                    lineTo(5.0f, 18.0f)
                    lineTo(8.5f, 13.5f)
                    close()
                }
            }.build()
            return _Image!!
        }

    private var _Image: ImageVector? = null

    val Info: ImageVector
        get() {
            if (_Info != null) return _Info!!
            _Info = ImageVector.Builder(
                name = "Info",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 6.48f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(13.0f, 17.0f)
                    lineTo(11.0f, 17.0f)
                    lineTo(11.0f, 11.0f)
                    lineTo(13.0f, 11.0f)
                    lineTo(13.0f, 17.0f)
                    close()
                    moveTo(13.0f, 9.0f)
                    lineTo(11.0f, 9.0f)
                    lineTo(11.0f, 7.0f)
                    lineTo(13.0f, 7.0f)
                    lineTo(13.0f, 9.0f)
                    close()
                }
            }.build()
            return _Info!!
        }

    private var _Info: ImageVector? = null

    val Language: ImageVector
        get() {
            if (_Language != null) return _Language!!
            _Language = ImageVector.Builder(
                name = "Language",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(11.99f, 2.0f)
                    curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 6.47f, 22.0f, 11.99f, 22.0f)
                    curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.52f, 2.0f, 11.99f, 2.0f)
                    close()
                    moveTo(18.92f, 8.0f)
                    lineTo(15.970000000000002f, 8.0f)
                    curveTo(15.650000000000002f, 6.75f, 15.190000000000003f, 5.55f, 14.590000000000003f, 4.4399999999999995f)
                    close()
                    moveTo(12.0f, 4.04f)
                    curveTo(12.83f, 5.24f, 13.48f, 6.57f, 13.91f, 8.0f)
                    lineTo(10.09f, 8.0f)
                    curveTo(10.52f, 6.57f, 11.17f, 5.24f, 12.0f, 4.04f)
                    close()
                    moveTo(4.26f, 14.0f)
                    curveTo(4.1f, 13.36f, 4.0f, 12.69f, 4.0f, 12.0f)
                    lineTo(7.38f, 12.0f)
                    curveTo(7.24f, 13.32f, 7.24f, 14.0f, 7.38f, 14.0f)
                    lineTo(4.26f, 14.0f)
                    close()
                    moveTo(5.08f, 16.0f)
                    lineTo(8.030000000000001f, 16.0f)
                    curveTo(8.350000000000001f, 18.45f, 9.41f, 19.56f, 6.190000000000001f, 15.37f)
                    close()
                    moveTo(8.030000000000001f, 8.0f)
                    lineTo(5.08f, 8.0f)
                    curveTo(6.04f, 6.34f, 7.57f, 5.07f, 9.41f, 4.4399999999999995f)
                    curveTo(8.81f, 5.55f, 8.35f, 6.75f, 8.03f, 8.0f)
                    close()
                    moveTo(12.0f, 19.96f)
                    curveTo(11.17f, 18.76f, 10.52f, 17.43f, 10.09f, 16.0f)
                    lineTo(13.91f, 16.0f)
                    curveTo(13.48f, 17.43f, 12.83f, 18.759999999999998f, 12.0f, 19.96f)
                    close()
                    moveTo(14.34f, 14.0f)
                    lineTo(9.66f, 14.0f)
                    curveTo(9.57f, 13.34f, 9.5f, 12.68f, 9.5f, 12.0f)
                    lineTo(14.18f, 12.0f)
                    curveTo(16.18f, 12.0f, 14.86f, 11.93f, 15.52f, 11.84f)
                    close()
                    moveTo(14.59f, 19.56f)
                    curveTo(15.19f, 18.45f, 15.65f, 17.25f, 15.969999999999999f, 15.999999999999998f)
                    lineTo(18.919999999999998f, 15.999999999999998f)
                    curveTo(17.959999999999997f, 17.65f, 16.43f, 18.93f, 14.589999999999998f, 19.56f)
                    close()
                    moveTo(16.36f, 14.0f)
                    curveTo(16.439999999999998f, 12.0f, 16.36f, 13.32f, 16.3f, 12.66f)
                    lineTo(19.68f, 12.66f)
                    curveTo(19.68f, 12.66f, 19.58f, 14.02f, 19.419999999999998f, 14.66f)
                    lineTo(16.04f, 14.66f)
                    close()
                }
            }.build()
            return _Language!!
        }

    private var _Language: ImageVector? = null

    val Layers: ImageVector
        get() {
            if (_Layers != null) return _Layers!!
            _Layers = ImageVector.Builder(
                name = "Layers",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(11.99f, 18.54f)
                    lineTo(4.62f, 12.809999999999999f)
                    lineTo(3.0f, 14.07f)
                    lineTo(12.0f, 21.07f)
                    lineTo(21.0f, 14.07f)
                    lineTo(19.37f, 12.8f)
                    lineTo(11.990000000000002f, 18.54f)
                    close()
                    moveTo(12.0f, 16.0f)
                    lineTo(19.36f, 10.27f)
                    lineTo(21.0f, 9.0f)
                    lineTo(12.0f, 2.0f)
                    lineTo(3.0f, 9.0f)
                    lineTo(4.63f, 10.27f)
                    lineTo(12.0f, 16.0f)
                    close()
                }
            }.build()
            return _Layers!!
        }

    private var _Layers: ImageVector? = null

    val Link: ImageVector
        get() {
            if (_Link != null) return _Link!!
            _Link = ImageVector.Builder(
                name = "Link",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(3.9f, 12.0f)
                    curveTo(3.9f, 10.29f, 5.29f, 8.9f, 7.0f, 8.9f)
                    lineTo(11.0f, 8.9f)
                    lineTo(11.0f, 7.0f)
                    lineTo(7.0f, 7.0f)
                    curveTo(4.24f, 7.0f, 2.0f, 9.24f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 4.24f, 17.0f, 7.0f, 17.0f)
                    lineTo(11.0f, 17.0f)
                    lineTo(11.0f, 15.1f)
                    lineTo(7.0f, 15.1f)
                    curveTo(5.29f, 15.1f, 3.9f, 13.709999999999999f, 3.9f, 12.0f)
                    close()
                    moveTo(8.0f, 13.0f)
                    lineTo(16.0f, 13.0f)
                    lineTo(16.0f, 11.0f)
                    lineTo(8.0f, 11.0f)
                    lineTo(8.0f, 13.0f)
                    close()
                    moveTo(17.0f, 7.0f)
                    lineTo(13.0f, 7.0f)
                    lineTo(13.0f, 8.9f)
                    lineTo(17.0f, 8.9f)
                    curveTo(18.71f, 8.9f, 20.1f, 10.290000000000001f, 20.1f, 12.0f)
                    curveTo(20.1f, 12.0f, 18.71f, 15.1f, 17.0f, 15.1f)
                    lineTo(13.0f, 15.1f)
                    lineTo(13.0f, 17.0f)
                    lineTo(17.0f, 17.0f)
                    curveTo(19.759999999999998f, 17.0f, 22.0f, 14.76f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 19.759999999999998f, 7.0f, 17.0f, 7.0f)
                    close()
                }
            }.build()
            return _Link!!
        }

    private var _Link: ImageVector? = null

    val LocationOff: ImageVector
        get() {
            if (_LocationOff != null) return _LocationOff!!
            _LocationOff = ImageVector.Builder(
                name = "LocationOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 6.5f)
                    curveTo(13.379999999999999f, 6.5f, 14.5f, 7.62f, 14.5f, 9.0f)
                    curveTo(14.5f, 9.74f, 14.17f, 10.39f, 13.67f, 10.85f)
                    lineTo(17.3f, 14.48f)
                    curveTo(18.28f, 12.620000000000001f, 19.0f, 10.68f, 19.0f, 9.0f)
                    curveTo(19.0f, 5.13f, 15.870000000000001f, 2.0f, 12.0f, 2.0f)
                    lineTo(15.19f, 5.1899999999999995f)
                    curveTo(15.65f, 4.67f, 16.3f, 4.35f, 17.04f, 4.35f)
                    close()
                    moveTo(16.37f, 16.1f)
                    lineTo(11.740000000000002f, 11.470000000000002f)
                    lineTo(11.630000000000003f, 11.360000000000003f)
                    lineTo(3.27f, 3.0f)
                    lineTo(2.0f, 4.27f)
                    lineTo(5.18f, 7.449999999999999f)
                    curveTo(5.07f, 7.95f, 5.0f, 8.47f, 5.0f, 9.0f)
                    curveTo(5.0f, 14.25f, 12.0f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 13.67f, 20.15f, 15.379999999999999f, 17.65f)
                    lineTo(18.73f, 21.0f)
                    lineTo(20.0f, 19.73f)
                    lineTo(16.37f, 16.1f)
                    close()
                }
            }.build()
            return _LocationOff!!
        }

    private var _LocationOff: ImageVector? = null

    val LocationOn: ImageVector
        get() {
            if (_LocationOn != null) return _LocationOn!!
            _LocationOn = ImageVector.Builder(
                name = "LocationOn",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 2.0f)
                    curveTo(8.13f, 2.0f, 5.0f, 5.13f, 5.0f, 9.0f)
                    curveTo(5.0f, 14.25f, 12.0f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 19.0f, 14.25f, 19.0f, 9.0f)
                    curveTo(19.0f, 5.13f, 15.870000000000001f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(12.0f, 11.5f)
                    curveTo(10.620000000000001f, 11.5f, 9.5f, 10.379999999999999f, 9.5f, 9.0f)
                    curveTo(9.5f, 9.0f, 10.620000000000001f, 6.5f, 12.0f, 6.5f)
                    curveTo(12.0f, 6.5f, 14.5f, 7.62f, 14.5f, 9.0f)
                    curveTo(14.5f, 9.0f, 13.379999999999999f, 11.5f, 12.0f, 11.5f)
                    close()
                }
            }.build()
            return _LocationOn!!
        }

    private var _LocationOn: ImageVector? = null

    val Lock: ImageVector
        get() {
            if (_Lock != null) return _Lock!!
            _Lock = ImageVector.Builder(
                name = "Lock",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(18.0f, 8.0f)
                    lineTo(17.0f, 8.0f)
                    lineTo(17.0f, 6.0f)
                    curveTo(17.0f, 3.24f, 14.76f, 1.0f, 12.0f, 1.0f)
                    curveTo(12.0f, 1.0f, 7.0f, 3.24f, 7.0f, 6.0f)
                    lineTo(7.0f, 8.0f)
                    lineTo(6.0f, 8.0f)
                    curveTo(4.9f, 8.0f, 4.0f, 8.9f, 4.0f, 10.0f)
                    lineTo(4.0f, 20.0f)
                    lineTo(16.0f, 20.0f)
                    curveTo(17.1f, 20.0f, 18.0f, 19.1f, 18.0f, 18.0f)
                    lineTo(18.0f, 10.0f)
                    curveTo(18.0f, 8.9f, 17.1f, 8.0f, 16.0f, 8.0f)
                    close()
                    moveTo(12.0f, 17.0f)
                    curveTo(10.9f, 17.0f, 10.0f, 16.1f, 10.0f, 15.0f)
                    curveTo(10.0f, 15.0f, 10.9f, 13.0f, 12.0f, 13.0f)
                    curveTo(12.0f, 13.0f, 14.0f, 13.9f, 14.0f, 15.0f)
                    curveTo(14.0f, 15.0f, 13.1f, 17.0f, 12.0f, 17.0f)
                    close()
                    moveTo(15.1f, 8.0f)
                    lineTo(8.9f, 8.0f)
                    lineTo(8.9f, 6.0f)
                    curveTo(8.9f, 4.29f, 10.290000000000001f, 2.9f, 12.0f, 2.9f)
                    curveTo(13.71f, 2.9f, 15.1f, 4.29f, 15.1f, 6.0f)
                    lineTo(15.1f, 8.0f)
                    close()
                }
            }.build()
            return _Lock!!
        }

    private var _Lock: ImageVector? = null

    val LockOpen: ImageVector
        get() {
            if (_LockOpen != null) return _LockOpen!!
            _LockOpen = ImageVector.Builder(
                name = "LockOpen",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 17.0f)
                    curveTo(13.1f, 17.0f, 14.0f, 16.1f, 14.0f, 15.0f)
                    curveTo(14.0f, 15.0f, 13.1f, 13.0f, 12.0f, 13.0f)
                    curveTo(12.0f, 13.0f, 10.0f, 13.9f, 10.0f, 15.0f)
                    curveTo(10.0f, 15.0f, 10.9f, 17.0f, 12.0f, 17.0f)
                    close()
                    moveTo(18.0f, 8.0f)
                    lineTo(17.0f, 8.0f)
                    lineTo(17.0f, 6.0f)
                    curveTo(17.0f, 3.24f, 14.76f, 1.0f, 12.0f, 1.0f)
                    curveTo(12.0f, 1.0f, 7.0f, 3.24f, 7.0f, 6.0f)
                    lineTo(8.9f, 6.0f)
                    curveTo(8.9f, 4.29f, 10.290000000000001f, 2.9f, 12.0f, 2.9f)
                    curveTo(13.71f, 2.9f, 15.1f, 4.29f, 15.1f, 6.0f)
                    lineTo(15.1f, 8.0f)
                    lineTo(6.0f, 8.0f)
                    curveTo(4.9f, 8.0f, 4.0f, 8.9f, 4.0f, 10.0f)
                    lineTo(4.0f, 20.0f)
                    lineTo(16.0f, 20.0f)
                    curveTo(17.1f, 20.0f, 18.0f, 19.1f, 18.0f, 18.0f)
                    lineTo(18.0f, 10.0f)
                    curveTo(18.0f, 8.9f, 17.1f, 8.0f, 16.0f, 8.0f)
                    close()
                    moveTo(18.0f, 20.0f)
                    lineTo(6.0f, 20.0f)
                    lineTo(6.0f, 10.0f)
                    lineTo(18.0f, 10.0f)
                    lineTo(18.0f, 20.0f)
                    close()
                }
            }.build()
            return _LockOpen!!
        }

    private var _LockOpen: ImageVector? = null

    val Mic: ImageVector
        get() {
            if (_Mic != null) return _Mic!!
            _Mic = ImageVector.Builder(
                name = "Mic",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 14.0f)
                    curveTo(13.66f, 14.0f, 14.99f, 12.66f, 14.99f, 11.0f)
                    lineTo(15.0f, 5.0f)
                    curveTo(15.0f, 3.34f, 13.66f, 2.0f, 12.0f, 2.0f)
                    curveTo(12.0f, 2.0f, 9.0f, 3.34f, 9.0f, 5.0f)
                    lineTo(9.0f, 11.0f)
                    curveTo(9.0f, 12.66f, 10.34f, 14.0f, 12.0f, 14.0f)
                    close()
                    moveTo(17.3f, 11.0f)
                    curveTo(17.3f, 14.0f, 14.760000000000002f, 16.1f, 12.0f, 16.1f)
                    curveTo(12.0f, 16.1f, 6.7f, 14.0f, 6.7f, 11.0f)
                    lineTo(5.0f, 11.0f)
                    curveTo(5.0f, 14.41f, 7.720000000000001f, 17.23f, 11.0f, 17.72f)
                    lineTo(11.0f, 21.0f)
                    lineTo(13.0f, 21.0f)
                    lineTo(13.0f, 17.72f)
                    curveTo(16.28f, 17.24f, 19.0f, 14.419999999999998f, 19.0f, 11.0f)
                    lineTo(17.3f, 11.0f)
                    close()
                }
            }.build()
            return _Mic!!
        }

    private var _Mic: ImageVector? = null

    val MicOff: ImageVector
        get() {
            if (_MicOff != null) return _MicOff!!
            _MicOff = ImageVector.Builder(
                name = "MicOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 11.0f)
                    lineTo(17.3f, 11.0f)
                    curveTo(17.3f, 11.74f, 17.14f, 12.43f, 16.87f, 13.05f)
                    lineTo(18.1f, 14.280000000000001f)
                    close()
                    lineTo(19.0f, 5.0f)
                    curveTo(19.0f, 3.34f, 17.66f, 2.0f, 16.0f, 2.0f)
                    curveTo(16.0f, 2.0f, 9.0f, 3.34f, 9.0f, 5.0f)
                    lineTo(9.0f, 5.18f)
                    lineTo(14.98f, 11.17f)
                    close()
                    moveTo(4.27f, 3.0f)
                    lineTo(3.0f, 4.27f)
                    lineTo(9.01f, 10.28f)
                    lineTo(9.01f, 11.0f)
                    curveTo(9.01f, 12.66f, 10.34f, 14.0f, 12.0f, 14.0f)
                    lineTo(13.66f, 15.66f)
                    curveTo(10.9f, 15.66f, 8.36f, 13.56f, 8.36f, 10.56f)
                    lineTo(5.0f, 10.56f)
                    curveTo(5.0f, 13.97f, 7.720000000000001f, 16.79f, 11.0f, 17.28f)
                    lineTo(11.0f, 21.0f)
                    lineTo(13.0f, 21.0f)
                    lineTo(13.0f, 17.72f)
                    curveTo(13.91f, 17.59f, 14.77f, 17.27f, 15.54f, 16.82f)
                    lineTo(19.73f, 21.0f)
                    lineTo(21.0f, 19.73f)
                    lineTo(4.27f, 3.0f)
                    close()
                }
            }.build()
            return _MicOff!!
        }

    private var _MicOff: ImageVector? = null

    val MoreVert: ImageVector
        get() {
            if (_MoreVert != null) return _MoreVert!!
            _MoreVert = ImageVector.Builder(
                name = "MoreVert",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 8.0f)
                    curveTo(13.1f, 8.0f, 14.0f, 7.1f, 14.0f, 6.0f)
                    curveTo(14.0f, 6.0f, 13.1f, 4.0f, 12.0f, 4.0f)
                    curveTo(12.0f, 4.0f, 10.0f, 4.9f, 10.0f, 6.0f)
                    curveTo(10.0f, 6.0f, 10.9f, 8.0f, 12.0f, 8.0f)
                    close()
                    moveTo(12.0f, 10.0f)
                    curveTo(10.9f, 10.0f, 10.0f, 10.9f, 10.0f, 12.0f)
                    curveTo(10.0f, 12.0f, 10.9f, 14.0f, 12.0f, 14.0f)
                    curveTo(12.0f, 14.0f, 14.0f, 13.1f, 14.0f, 12.0f)
                    curveTo(14.0f, 12.0f, 13.1f, 10.0f, 12.0f, 10.0f)
                    close()
                    moveTo(12.0f, 16.0f)
                    curveTo(10.9f, 16.0f, 10.0f, 16.9f, 10.0f, 18.0f)
                    curveTo(10.0f, 18.0f, 10.9f, 20.0f, 12.0f, 20.0f)
                    curveTo(12.0f, 20.0f, 14.0f, 19.1f, 14.0f, 18.0f)
                    curveTo(14.0f, 18.0f, 13.1f, 16.0f, 12.0f, 16.0f)
                    close()
                }
            }.build()
            return _MoreVert!!
        }

    private var _MoreVert: ImageVector? = null

    val Notifications: ImageVector
        get() {
            if (_Notifications != null) return _Notifications!!
            _Notifications = ImageVector.Builder(
                name = "Notifications",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 22.0f)
                    curveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f)
                    lineTo(10.0f, 20.0f)
                    close()
                    moveTo(18.0f, 16.0f)
                    lineTo(18.0f, 11.0f)
                    curveTo(18.0f, 7.93f, 16.36f, 5.36f, 13.5f, 4.68f)
                    lineTo(13.5f, 4.0f)
                    curveTo(13.5f, 3.17f, 12.83f, 2.5f, 12.0f, 2.5f)
                    lineTo(12.0f, 3.18f)
                    curveTo(7.63f, 5.36f, 6.0f, 7.92f, 6.0f, 11.0f)
                    lineTo(6.0f, 16.0f)
                    lineTo(4.0f, 18.0f)
                    lineTo(4.0f, 19.0f)
                    lineTo(20.0f, 19.0f)
                    lineTo(20.0f, 18.0f)
                    lineTo(18.0f, 16.0f)
                    close()
                }
            }.build()
            return _Notifications!!
        }

    private var _Notifications: ImageVector? = null

    val NotificationsOff: ImageVector
        get() {
            if (_NotificationsOff != null) return _NotificationsOff!!
            _NotificationsOff = ImageVector.Builder(
                name = "NotificationsOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(20.0f, 18.69f)
                    lineTo(7.84f, 6.14f)
                    lineTo(5.27f, 3.49f)
                    lineTo(4.0f, 4.76f)
                    lineTo(6.8f, 7.56f)
                    lineTo(6.8f, 7.569999999999999f)
                    lineTo(6.8f, 12.57f)
                    lineTo(4.8f, 14.57f)
                    lineTo(4.8f, 15.57f)
                    lineTo(18.53f, 15.57f)
                    lineTo(20.53f, 17.57f)
                    lineTo(21.0f, 19.72f)
                    lineTo(20.0f, 18.689999999999998f)
                    close()
                    moveTo(12.0f, 22.0f)
                    curveTo(13.11f, 22.0f, 14.0f, 21.11f, 14.0f, 20.0f)
                    lineTo(10.0f, 20.0f)
                    close()
                    moveTo(18.0f, 14.68f)
                    lineTo(18.0f, 11.0f)
                    curveTo(18.0f, 7.92f, 16.36f, 5.36f, 13.5f, 4.68f)
                    lineTo(13.5f, 4.0f)
                    curveTo(13.5f, 3.17f, 12.83f, 2.5f, 12.0f, 2.5f)
                    lineTo(12.0f, 3.18f)
                    lineTo(11.99f, 3.18f)
                    curveTo(11.98f, 3.18f, 11.98f, 3.18f, 11.99f, 3.18f)
                    lineTo(18.0f, 14.68f)
                    close()
                }
            }.build()
            return _NotificationsOff!!
        }

    private var _NotificationsOff: ImageVector? = null

    val Pause: ImageVector
        get() {
            if (_Pause != null) return _Pause!!
            _Pause = ImageVector.Builder(
                name = "Pause",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(6.0f, 19.0f)
                    lineTo(10.0f, 19.0f)
                    lineTo(10.0f, 5.0f)
                    lineTo(6.0f, 5.0f)
                    lineTo(6.0f, 19.0f)
                    close()
                    moveTo(14.0f, 5.0f)
                    lineTo(14.0f, 19.0f)
                    lineTo(18.0f, 19.0f)
                    lineTo(18.0f, 5.0f)
                    lineTo(14.0f, 5.0f)
                    close()
                }
            }.build()
            return _Pause!!
        }

    private var _Pause: ImageVector? = null

    val Photo: ImageVector
        get() {
            if (_Photo != null) return _Photo!!
            _Photo = ImageVector.Builder(
                name = "Photo",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(21.0f, 19.0f)
                    lineTo(21.0f, 5.0f)
                    curveTo(21.0f, 3.9f, 20.1f, 3.0f, 19.0f, 3.0f)
                    lineTo(5.0f, 3.0f)
                    curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                    lineTo(3.0f, 19.0f)
                    lineTo(17.0f, 19.0f)
                    curveTo(18.1f, 19.0f, 19.0f, 18.1f, 19.0f, 17.0f)
                    close()
                    moveTo(8.5f, 13.5f)
                    lineTo(11.0f, 16.509999999999998f)
                    lineTo(14.5f, 12.0f)
                    lineTo(19.0f, 18.0f)
                    lineTo(5.0f, 18.0f)
                    lineTo(8.5f, 13.5f)
                    close()
                }
            }.build()
            return _Photo!!
        }

    private var _Photo: ImageVector? = null

    val PlayArrow: ImageVector
        get() {
            if (_PlayArrow != null) return _PlayArrow!!
            _PlayArrow = ImageVector.Builder(
                name = "PlayArrow",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(8.0f, 5.0f)
                    lineTo(8.0f, 19.0f)
                    lineTo(19.0f, 12.0f)
                    close()
                }
            }.build()
            return _PlayArrow!!
        }

    private var _PlayArrow: ImageVector? = null

    val PlayCircle: ImageVector
        get() {
            if (_PlayCircle != null) return _PlayCircle!!
            _PlayCircle = ImageVector.Builder(
                name = "PlayCircle",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 6.48f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(9.5f, 16.5f)
                    lineTo(9.5f, 7.5f)
                    lineTo(16.5f, 12.0f)
                    lineTo(9.5f, 16.5f)
                    close()
                }
            }.build()
            return _PlayCircle!!
        }

    private var _PlayCircle: ImageVector? = null

    val Public: ImageVector
        get() {
            if (_Public != null) return _Public!!
            _Public = ImageVector.Builder(
                name = "Public",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 6.48f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(11.0f, 19.93f)
                    curveTo(7.05f, 19.44f, 4.0f, 16.08f, 4.0f, 12.0f)
                    lineTo(9.0f, 15.0f)
                    lineTo(9.0f, 16.0f)
                    lineTo(9.0f, 17.93f)
                    close()
                    moveTo(17.9f, 17.39f)
                    curveTo(17.639999999999997f, 16.580000000000002f, 16.9f, 16.0f, 15.999999999999998f, 16.0f)
                    lineTo(14.999999999999998f, 16.0f)
                    lineTo(14.999999999999998f, 13.0f)
                    curveTo(14.999999999999998f, 12.45f, 14.549999999999999f, 12.0f, 13.999999999999998f, 12.0f)
                    lineTo(8.0f, 12.0f)
                    lineTo(8.0f, 10.0f)
                    lineTo(10.0f, 10.0f)
                    curveTo(10.55f, 10.0f, 11.0f, 9.55f, 11.0f, 9.0f)
                    lineTo(11.0f, 7.0f)
                    lineTo(13.0f, 7.0f)
                    curveTo(14.1f, 7.0f, 15.0f, 6.1f, 15.0f, 5.0f)
                    lineTo(15.0f, 4.59f)
                    curveTo(17.93f, 5.779999999999999f, 20.0f, 8.649999999999999f, 20.0f, 12.0f)
                    curveTo(20.0f, 14.08f, 19.2f, 15.97f, 17.9f, 17.39f)
                    close()
                }
            }.build()
            return _Public!!
        }

    private var _Public: ImageVector? = null

    val PushPin: ImageVector
        get() {
            if (_PushPin != null) return _PushPin!!
            _PushPin = ImageVector.Builder(
                name = "PushPin",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(16.0f, 9.0f)
                    lineTo(16.0f, 4.0f)
                    lineTo(17.0f, 4.0f)
                    curveTo(17.55f, 4.0f, 18.0f, 3.55f, 18.0f, 3.0f)
                    lineTo(18.0f, 3.0f)
                    curveTo(18.0f, 2.45f, 17.55f, 2.0f, 17.0f, 2.0f)
                    lineTo(7.0f, 2.0f)
                    curveTo(6.45f, 2.0f, 6.0f, 2.45f, 6.0f, 3.0f)
                    lineTo(6.0f, 3.0f)
                    curveTo(6.0f, 3.55f, 6.45f, 4.0f, 7.0f, 4.0f)
                    lineTo(8.0f, 4.0f)
                    lineTo(8.0f, 9.0f)
                    curveTo(8.0f, 10.66f, 6.66f, 12.0f, 5.0f, 12.0f)
                    lineTo(5.0f, 12.0f)
                    lineTo(5.0f, 14.0f)
                    lineTo(10.969999999999999f, 14.0f)
                    lineTo(10.969999999999999f, 21.0f)
                    lineTo(11.969999999999999f, 22.0f)
                    lineTo(12.969999999999999f, 21.0f)
                    lineTo(12.969999999999999f, 14.0f)
                    lineTo(19.0f, 14.0f)
                    lineTo(19.0f, 12.0f)
                    lineTo(19.0f, 12.0f)
                    curveTo(17.34f, 12.0f, 16.0f, 10.66f, 16.0f, 9.0f)
                    close()
                }
            }.build()
            return _PushPin!!
        }

    private var _PushPin: ImageVector? = null

    val Replay10: ImageVector
        get() {
            if (_Replay10 != null) return _Replay10!!
            _Replay10 = ImageVector.Builder(
                name = "Replay10",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(11.99f, 5.0f)
                    lineTo(11.99f, 1.0f)
                    lineTo(6.99f, 6.0f)
                    lineTo(11.99f, 11.0f)
                    lineTo(11.99f, 7.0f)
                    curveTo(15.3f, 7.0f, 17.990000000000002f, 9.69f, 17.990000000000002f, 13.0f)
                    curveTo(17.990000000000002f, 13.0f, 15.300000000000002f, 19.0f, 11.990000000000002f, 19.0f)
                    curveTo(11.990000000000002f, 19.0f, 5.990000000000002f, 16.31f, 5.990000000000002f, 13.0f)
                    lineTo(3.990000000000002f, 13.0f)
                    curveTo(3.990000000000002f, 17.42f, 7.570000000000002f, 21.0f, 11.990000000000002f, 21.0f)
                    curveTo(11.990000000000002f, 21.0f, 19.990000000000002f, 17.42f, 19.990000000000002f, 13.0f)
                    curveTo(19.990000000000002f, 13.0f, 16.41f, 5.0f, 11.99f, 5.0f)
                    close()
                    moveTo(10.89f, 16.0f)
                    lineTo(10.040000000000001f, 16.0f)
                    lineTo(10.040000000000001f, 12.74f)
                    lineTo(9.030000000000001f, 13.05f)
                    lineTo(9.030000000000001f, 12.360000000000001f)
                    lineTo(10.8f, 11.73f)
                    lineTo(10.89f, 11.73f)
                    lineTo(10.89f, 16.0f)
                    close()
                    moveTo(15.17f, 14.24f)
                    curveTo(15.17f, 14.56f, 15.14f, 14.84f, 15.07f, 15.06f)
                    curveTo(15.07f, 15.06f, 14.9f, 15.48f, 14.780000000000001f, 15.63f)
                    curveTo(14.780000000000001f, 15.63f, 14.500000000000002f, 15.89f, 14.330000000000002f, 15.96f)
                    curveTo(14.330000000000002f, 15.96f, 13.960000000000003f, 16.060000000000002f, 13.740000000000002f, 16.060000000000002f)
                    curveTo(13.740000000000002f, 16.060000000000002f, 13.330000000000002f, 16.03f, 13.150000000000002f, 15.960000000000003f)
                    curveTo(13.150000000000002f, 15.960000000000003f, 12.820000000000002f, 15.780000000000003f, 12.690000000000001f, 15.630000000000003f)
                    curveTo(12.690000000000001f, 15.630000000000003f, 12.46f, 15.290000000000003f, 12.39f, 15.060000000000002f)
                    curveTo(12.39f, 15.060000000000002f, 12.280000000000001f, 14.560000000000002f, 12.280000000000001f, 14.240000000000002f)
                    lineTo(12.280000000000001f, 13.5f)
                    curveTo(12.280000000000001f, 13.18f, 12.31f, 12.9f, 12.38f, 12.68f)
                    curveTo(12.38f, 12.68f, 12.55f, 12.26f, 12.67f, 12.11f)
                    curveTo(12.67f, 12.11f, 12.95f, 11.85f, 13.12f, 11.78f)
                    curveTo(13.12f, 11.78f, 13.489999999999998f, 11.68f, 13.709999999999999f, 11.68f)
                    curveTo(13.709999999999999f, 11.68f, 14.12f, 11.709999999999999f, 14.299999999999999f, 11.78f)
                    curveTo(14.479999999999999f, 11.85f, 14.629999999999999f, 11.959999999999999f, 14.76f, 12.11f)
                    curveTo(14.76f, 12.11f, 14.99f, 12.45f, 15.06f, 12.68f)
                    curveTo(15.06f, 12.68f, 15.17f, 13.18f, 15.17f, 13.5f)
                    lineTo(15.17f, 14.24f)
                    close()
                    moveTo(14.32f, 13.38f)
                    curveTo(14.32f, 13.190000000000001f, 14.31f, 13.030000000000001f, 14.280000000000001f, 12.9f)
                    curveTo(14.280000000000001f, 12.9f, 14.21f, 12.67f, 14.160000000000002f, 12.59f)
                    curveTo(14.160000000000002f, 12.59f, 14.050000000000002f, 12.45f, 13.970000000000002f, 12.42f)
                    curveTo(13.970000000000002f, 12.42f, 13.810000000000002f, 12.37f, 13.720000000000002f, 12.37f)
                    curveTo(13.720000000000002f, 12.37f, 13.540000000000003f, 12.389999999999999f, 13.470000000000002f, 12.42f)
                    curveTo(13.470000000000002f, 12.42f, 13.330000000000002f, 12.51f, 13.280000000000003f, 12.59f)
                    curveTo(13.280000000000003f, 12.59f, 13.190000000000003f, 12.77f, 13.160000000000004f, 12.9f)
                    curveTo(13.160000000000004f, 12.9f, 13.120000000000005f, 13.19f, 13.120000000000005f, 13.38f)
                    lineTo(13.120000000000005f, 14.350000000000001f)
                    curveTo(13.120000000000005f, 14.540000000000001f, 13.130000000000004f, 14.700000000000001f, 13.160000000000004f, 14.830000000000002f)
                    curveTo(13.160000000000004f, 14.830000000000002f, 13.230000000000004f, 15.070000000000002f, 13.280000000000003f, 15.150000000000002f)
                    curveTo(13.280000000000003f, 15.150000000000002f, 13.390000000000002f, 15.290000000000003f, 13.470000000000002f, 15.320000000000002f)
                    curveTo(13.470000000000002f, 15.320000000000002f, 13.630000000000003f, 15.370000000000003f, 13.720000000000002f, 15.370000000000003f)
                    curveTo(13.720000000000002f, 15.370000000000003f, 13.900000000000002f, 15.350000000000003f, 13.970000000000002f, 15.320000000000002f)
                    curveTo(13.970000000000002f, 15.320000000000002f, 14.110000000000003f, 15.230000000000002f, 14.160000000000002f, 15.150000000000002f)
                    curveTo(14.160000000000002f, 15.150000000000002f, 14.250000000000002f, 14.960000000000003f, 14.270000000000001f, 14.830000000000002f)
                    curveTo(14.270000000000001f, 14.830000000000002f, 14.31f, 14.540000000000003f, 14.31f, 14.350000000000001f)
                    lineTo(14.31f, 13.38f)
                    close()
                }
            }.build()
            return _Replay10!!
        }

    private var _Replay10: ImageVector? = null

    val Security: ImageVector
        get() {
            if (_Security != null) return _Security!!
            _Security = ImageVector.Builder(
                name = "Security",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 1.0f)
                    lineTo(3.0f, 5.0f)
                    lineTo(3.0f, 11.0f)
                    curveTo(3.0f, 16.55f, 6.84f, 21.740000000000002f, 12.0f, 23.0f)
                    curveTo(17.16f, 21.74f, 21.0f, 16.55f, 21.0f, 11.0f)
                    lineTo(21.0f, 5.0f)
                    lineTo(12.0f, 1.0f)
                    close()
                    moveTo(12.0f, 11.99f)
                    lineTo(19.0f, 11.99f)
                    curveTo(18.47f, 16.11f, 15.72f, 19.78f, 12.0f, 20.93f)
                    lineTo(12.0f, 12.0f)
                    lineTo(5.0f, 12.0f)
                    lineTo(5.0f, 6.3f)
                    lineTo(12.0f, 3.19f)
                    lineTo(12.0f, 11.99f)
                    close()
                }
            }.build()
            return _Security!!
        }

    private var _Security: ImageVector? = null

    val Settings: ImageVector
        get() {
            if (_Settings != null) return _Settings!!
            _Settings = ImageVector.Builder(
                name = "Settings",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.14f, 12.94f)
                    curveTo(19.18f, 12.639999999999999f, 19.2f, 12.33f, 19.2f, 12.0f)
                    curveTo(19.2f, 11.68f, 19.18f, 11.36f, 19.13f, 11.06f)
                    lineTo(21.16f, 9.48f)
                    curveTo(21.34f, 9.34f, 21.39f, 9.07f, 21.28f, 8.870000000000001f)
                    lineTo(19.36f, 5.550000000000001f)
                    curveTo(19.24f, 5.330000000000001f, 18.99f, 5.260000000000001f, 18.77f, 5.330000000000001f)
                    lineTo(16.38f, 6.290000000000001f)
                    curveTo(15.879999999999999f, 5.910000000000001f, 15.35f, 5.590000000000001f, 14.759999999999998f, 5.350000000000001f)
                    lineTo(14.4f, 2.81f)
                    curveTo(14.360000000000001f, 2.5700000000000003f, 14.16f, 2.4f, 13.92f, 2.4f)
                    lineTo(10.08f, 2.4f)
                    curveTo(9.84f, 2.4f, 9.65f, 2.57f, 9.61f, 2.81f)
                    lineTo(9.25f, 5.35f)
                    curveTo(8.66f, 5.59f, 8.12f, 5.92f, 7.63f, 6.29f)
                    lineTo(5.24f, 5.33f)
                    curveTo(5.0200000000000005f, 5.25f, 4.7700000000000005f, 5.33f, 4.65f, 5.55f)
                    lineTo(2.74f, 8.87f)
                    curveTo(2.62f, 9.08f, 2.66f, 9.34f, 2.86f, 9.48f)
                    lineTo(4.89f, 11.06f)
                    curveTo(4.84f, 11.36f, 4.8f, 11.69f, 4.8f, 12.0f)
                    curveTo(4.8f, 12.0f, 4.819999999999999f, 12.64f, 4.87f, 12.94f)
                    lineTo(2.8400000000000003f, 14.52f)
                    curveTo(2.66f, 14.66f, 2.6100000000000003f, 14.93f, 2.72f, 15.129999999999999f)
                    lineTo(4.640000000000001f, 18.45f)
                    curveTo(4.760000000000001f, 18.669999999999998f, 5.010000000000001f, 18.74f, 5.23f, 18.669999999999998f)
                    lineTo(7.620000000000001f, 17.709999999999997f)
                    curveTo(8.120000000000001f, 18.089999999999996f, 8.65f, 18.409999999999997f, 9.240000000000002f, 18.65f)
                    lineTo(9.600000000000001f, 21.189999999999998f)
                    curveTo(9.650000000000002f, 21.429999999999996f, 9.840000000000002f, 21.599999999999998f, 10.080000000000002f, 21.599999999999998f)
                    lineTo(13.920000000000002f, 21.599999999999998f)
                    curveTo(14.160000000000002f, 21.599999999999998f, 14.360000000000001f, 21.429999999999996f, 14.390000000000002f, 21.189999999999998f)
                    lineTo(14.750000000000002f, 18.65f)
                    curveTo(15.340000000000002f, 18.41f, 15.880000000000003f, 18.09f, 16.37f, 17.709999999999997f)
                    lineTo(18.76f, 18.669999999999998f)
                    curveTo(18.98f, 18.749999999999996f, 19.23f, 18.669999999999998f, 19.35f, 18.45f)
                    lineTo(21.270000000000003f, 15.129999999999999f)
                    curveTo(21.390000000000004f, 14.909999999999998f, 21.340000000000003f, 14.659999999999998f, 21.150000000000002f, 14.52f)
                    lineTo(19.14f, 12.94f)
                    close()
                    moveTo(12.0f, 15.6f)
                    curveTo(10.02f, 15.6f, 8.4f, 13.98f, 8.4f, 12.0f)
                    curveTo(8.4f, 12.0f, 10.02f, 8.4f, 12.0f, 8.4f)
                    curveTo(12.0f, 8.4f, 15.6f, 10.02f, 15.6f, 12.0f)
                    curveTo(15.6f, 12.0f, 13.98f, 15.6f, 12.0f, 15.6f)
                    close()
                }
            }.build()
            return _Settings!!
        }

    private var _Settings: ImageVector? = null

    val Share: ImageVector
        get() {
            if (_Share != null) return _Share!!
            _Share = ImageVector.Builder(
                name = "Share",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(18.0f, 16.08f)
                    lineTo(8.91f, 12.7f)
                    curveTo(8.91f, 12.7f, 8.870000000000001f, 12.229999999999999f, 8.82f, 12.0f)
                    lineTo(15.870000000000001f, 7.89f)
                    curveTo(17.53f, 7.89f, 18.87f, 6.55f, 18.87f, 4.89f)
                    curveTo(18.87f, 4.89f, 17.53f, 1.8899999999999997f, 15.870000000000001f, 1.8899999999999997f)
                    curveTo(15.870000000000001f, 1.8899999999999997f, 12.870000000000001f, 3.2299999999999995f, 12.870000000000001f, 4.89f)
                    lineTo(8.04f, 9.81f)
                    curveTo(7.5f, 9.31f, 6.79f, 9.0f, 6.0f, 9.0f)
                    curveTo(4.34f, 9.0f, 3.0f, 10.34f, 3.0f, 12.0f)
                    curveTo(3.0f, 12.0f, 4.34f, 15.0f, 6.0f, 15.0f)
                    curveTo(6.79f, 15.0f, 7.5f, 14.69f, 8.04f, 14.19f)
                    lineTo(15.16f, 18.35f)
                    curveTo(15.16f, 19.96f, 16.47f, 21.270000000000003f, 18.08f, 21.270000000000003f)
                    curveTo(19.689999999999998f, 21.270000000000003f, 21.0f, 19.960000000000004f, 21.0f, 18.35f)
                    curveTo(21.0f, 18.35f, 19.69f, 15.430000000000001f, 18.08f, 15.430000000000001f)
                    close()
                }
            }.build()
            return _Share!!
        }

    private var _Share: ImageVector? = null

    val SlowMotionVideo: ImageVector
        get() {
            if (_SlowMotionVideo != null) return _SlowMotionVideo!!
            _SlowMotionVideo = ImageVector.Builder(
                name = "SlowMotionVideo",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(13.05f, 9.79f)
                    lineTo(10.0f, 7.5f)
                    lineTo(10.0f, 16.5f)
                    lineTo(13.05f, 14.21f)
                    lineTo(16.0f, 12.0f)
                    close()
                    moveTo(11.0f, 4.07f)
                    lineTo(11.0f, 2.05f)
                    lineTo(7.1f, 5.69f)
                    curveTo(8.209999999999999f, 4.83f, 9.54f, 4.25f, 11.0f, 4.07f)
                    close()
                    moveTo(5.69f, 7.1f)
                    lineTo(4.26f, 5.68f)
                    curveTo(3.05f, 7.16f, 2.25f, 8.99f, 2.05f, 11.0f)
                    lineTo(4.07f, 11.0f)
                    close()
                    moveTo(4.07f, 13.0f)
                    lineTo(2.05f, 13.0f)
                    curveTo(2.25f, 15.01f, 3.05f, 16.84f, 4.26f, 18.32f)
                    lineTo(5.6899999999999995f, 16.89f)
                    curveTo(4.829999999999999f, 15.790000000000001f, 4.25f, 14.46f, 4.069999999999999f, 13.0f)
                    close()
                    moveTo(5.680000000000001f, 19.740000000000002f)
                    curveTo(7.16f, 20.95f, 9.0f, 21.75f, 11.0f, 21.95f)
                    lineTo(11.0f, 19.93f)
                    curveTo(9.54f, 19.75f, 8.21f, 19.169999999999998f, 7.1f, 18.31f)
                    lineTo(5.68f, 19.74f)
                    close()
                    moveTo(22.0f, 12.0f)
                    curveTo(22.0f, 17.16f, 18.08f, 21.42f, 13.05f, 21.95f)
                    lineTo(13.05f, 19.93f)
                    curveTo(16.97f, 19.41f, 20.0f, 16.05f, 20.0f, 12.0f)
                    curveTo(20.0f, 12.0f, 16.97f, 4.59f, 13.05f, 4.07f)
                    lineTo(13.05f, 2.05f)
                    curveTo(18.08f, 2.58f, 22.0f, 6.84f, 22.0f, 12.0f)
                    close()
                }
            }.build()
            return _SlowMotionVideo!!
        }

    private var _SlowMotionVideo: ImageVector? = null

    val TabUnselected: ImageVector
        get() {
            if (_TabUnselected != null) return _TabUnselected!!
            _TabUnselected = ImageVector.Builder(
                name = "TabUnselected",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(1.0f, 9.0f)
                    lineTo(3.0f, 9.0f)
                    lineTo(3.0f, 7.0f)
                    lineTo(1.0f, 7.0f)
                    lineTo(1.0f, 9.0f)
                    close()
                    moveTo(1.0f, 13.0f)
                    lineTo(3.0f, 13.0f)
                    lineTo(3.0f, 11.0f)
                    lineTo(1.0f, 11.0f)
                    lineTo(1.0f, 13.0f)
                    close()
                    moveTo(1.0f, 5.0f)
                    lineTo(3.0f, 5.0f)
                    lineTo(3.0f, 3.0f)
                    curveTo(1.9f, 3.0f, 1.0f, 3.9f, 1.0f, 5.0f)
                    close()
                    moveTo(9.0f, 21.0f)
                    lineTo(11.0f, 21.0f)
                    lineTo(11.0f, 19.0f)
                    lineTo(9.0f, 19.0f)
                    lineTo(9.0f, 21.0f)
                    close()
                    moveTo(1.0f, 17.0f)
                    lineTo(3.0f, 17.0f)
                    lineTo(3.0f, 15.0f)
                    lineTo(1.0f, 15.0f)
                    lineTo(1.0f, 17.0f)
                    close()
                    moveTo(3.0f, 21.0f)
                    lineTo(3.0f, 19.0f)
                    lineTo(1.0f, 19.0f)
                    close()
                    moveTo(21.0f, 3.0f)
                    lineTo(13.0f, 3.0f)
                    lineTo(13.0f, 9.0f)
                    lineTo(23.0f, 9.0f)
                    lineTo(23.0f, 5.0f)
                    curveTo(23.0f, 3.9f, 22.1f, 3.0f, 21.0f, 3.0f)
                    close()
                    moveTo(21.0f, 17.0f)
                    lineTo(23.0f, 17.0f)
                    lineTo(23.0f, 15.0f)
                    lineTo(21.0f, 15.0f)
                    lineTo(21.0f, 17.0f)
                    close()
                    moveTo(9.0f, 5.0f)
                    lineTo(11.0f, 5.0f)
                    lineTo(11.0f, 3.0f)
                    lineTo(9.0f, 3.0f)
                    lineTo(9.0f, 5.0f)
                    close()
                    moveTo(5.0f, 21.0f)
                    lineTo(7.0f, 21.0f)
                    lineTo(7.0f, 19.0f)
                    lineTo(5.0f, 19.0f)
                    lineTo(5.0f, 21.0f)
                    close()
                    moveTo(5.0f, 5.0f)
                    lineTo(7.0f, 5.0f)
                    lineTo(7.0f, 3.0f)
                    lineTo(5.0f, 3.0f)
                    lineTo(5.0f, 5.0f)
                    close()
                    moveTo(21.0f, 21.0f)
                    curveTo(22.1f, 21.0f, 23.0f, 20.1f, 23.0f, 19.0f)
                    lineTo(21.0f, 19.0f)
                    lineTo(21.0f, 21.0f)
                    close()
                    moveTo(21.0f, 13.0f)
                    lineTo(23.0f, 13.0f)
                    lineTo(23.0f, 11.0f)
                    lineTo(21.0f, 11.0f)
                    lineTo(21.0f, 13.0f)
                    close()
                    moveTo(13.0f, 21.0f)
                    lineTo(15.0f, 21.0f)
                    lineTo(15.0f, 19.0f)
                    lineTo(13.0f, 19.0f)
                    lineTo(13.0f, 21.0f)
                    close()
                    moveTo(17.0f, 21.0f)
                    lineTo(19.0f, 21.0f)
                    lineTo(19.0f, 19.0f)
                    lineTo(17.0f, 19.0f)
                    lineTo(17.0f, 21.0f)
                    close()
                }
            }.build()
            return _TabUnselected!!
        }

    private var _TabUnselected: ImageVector? = null

    val UnfoldLess: ImageVector
        get() {
            if (_UnfoldLess != null) return _UnfoldLess!!
            _UnfoldLess = ImageVector.Builder(
                name = "UnfoldLess",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(7.41f, 18.59f)
                    lineTo(8.83f, 20.0f)
                    lineTo(12.0f, 16.83f)
                    lineTo(15.17f, 20.0f)
                    lineTo(16.58f, 18.59f)
                    lineTo(12.0f, 14.0f)
                    lineTo(7.41f, 18.59f)
                    close()
                    moveTo(16.59f, 5.41f)
                    lineTo(15.17f, 4.0f)
                    lineTo(12.0f, 7.17f)
                    lineTo(8.83f, 4.0f)
                    lineTo(7.41f, 5.41f)
                    lineTo(12.0f, 10.0f)
                    lineTo(16.59f, 5.41f)
                    close()
                }
            }.build()
            return _UnfoldLess!!
        }

    private var _UnfoldLess: ImageVector? = null

    val VerifiedUser: ImageVector
        get() {
            if (_VerifiedUser != null) return _VerifiedUser!!
            _VerifiedUser = ImageVector.Builder(
                name = "VerifiedUser",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 1.0f)
                    lineTo(3.0f, 5.0f)
                    lineTo(3.0f, 11.0f)
                    curveTo(3.0f, 16.55f, 6.84f, 21.740000000000002f, 12.0f, 23.0f)
                    curveTo(17.16f, 21.74f, 21.0f, 16.55f, 21.0f, 11.0f)
                    lineTo(21.0f, 5.0f)
                    lineTo(12.0f, 1.0f)
                    close()
                    moveTo(10.0f, 17.0f)
                    lineTo(6.0f, 13.0f)
                    lineTo(7.41f, 11.59f)
                    lineTo(10.0f, 14.17f)
                    lineTo(16.59f, 7.58f)
                    lineTo(18.0f, 9.0f)
                    lineTo(10.0f, 17.0f)
                    close()
                }
            }.build()
            return _VerifiedUser!!
        }

    private var _VerifiedUser: ImageVector? = null

    val Videocam: ImageVector
        get() {
            if (_Videocam != null) return _Videocam!!
            _Videocam = ImageVector.Builder(
                name = "Videocam",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(17.0f, 10.5f)
                    lineTo(17.0f, 7.0f)
                    curveTo(17.0f, 6.45f, 16.55f, 6.0f, 16.0f, 6.0f)
                    lineTo(4.0f, 6.0f)
                    curveTo(3.45f, 6.0f, 3.0f, 6.45f, 3.0f, 7.0f)
                    lineTo(3.0f, 17.0f)
                    lineTo(15.0f, 17.0f)
                    curveTo(15.55f, 17.0f, 16.0f, 16.55f, 16.0f, 16.0f)
                    lineTo(16.0f, 12.5f)
                    lineTo(20.0f, 16.5f)
                    lineTo(20.0f, 5.5f)
                    lineTo(16.0f, 9.5f)
                    close()
                }
            }.build()
            return _Videocam!!
        }

    private var _Videocam: ImageVector? = null

    val VideocamOff: ImageVector
        get() {
            if (_VideocamOff != null) return _VideocamOff!!
            _VideocamOff = ImageVector.Builder(
                name = "VideocamOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(21.0f, 6.5f)
                    lineTo(17.0f, 10.5f)
                    lineTo(17.0f, 7.0f)
                    curveTo(17.0f, 6.45f, 16.55f, 6.0f, 16.0f, 6.0f)
                    lineTo(9.82f, 6.0f)
                    lineTo(21.0f, 17.18f)
                    lineTo(21.0f, 6.5f)
                    close()
                    moveTo(3.27f, 2.0f)
                    lineTo(2.0f, 3.27f)
                    lineTo(4.73f, 6.0f)
                    lineTo(4.0f, 6.0f)
                    curveTo(3.45f, 6.0f, 3.0f, 6.45f, 3.0f, 7.0f)
                    lineTo(3.0f, 17.0f)
                    lineTo(15.0f, 17.0f)
                    lineTo(19.73f, 21.0f)
                    lineTo(21.0f, 19.73f)
                    lineTo(3.27f, 2.0f)
                    close()
                }
            }.build()
            return _VideocamOff!!
        }

    private var _VideocamOff: ImageVector? = null

    val VisibilityOff: ImageVector
        get() {
            if (_VisibilityOff != null) return _VisibilityOff!!
            _VisibilityOff = ImageVector.Builder(
                name = "VisibilityOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 7.0f)
                    curveTo(14.76f, 7.0f, 17.0f, 9.24f, 17.0f, 12.0f)
                    curveTo(17.0f, 12.65f, 16.87f, 13.26f, 16.64f, 13.83f)
                    lineTo(19.560000000000002f, 16.75f)
                    curveTo(21.070000000000004f, 15.49f, 22.26f, 13.86f, 22.990000000000002f, 12.0f)
                    curveTo(21.26f, 7.61f, 16.990000000000002f, 4.5f, 11.990000000000002f, 4.5f)
                    lineTo(14.150000000000002f, 6.66f)
                    curveTo(10.74f, 7.13f, 11.35f, 7.0f, 12.0f, 7.0f)
                    close()
                    moveTo(2.0f, 4.27f)
                    curveTo(3.08f, 8.3f, 1.78f, 10.02f, 1.0f, 12.0f)
                    curveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f)
                    curveTo(13.55f, 19.5f, 15.03f, 19.2f, 16.38f, 18.66f)
                    lineTo(19.73f, 22.0f)
                    lineTo(21.0f, 20.73f)
                    lineTo(3.27f, 3.0f)
                    lineTo(2.0f, 4.27f)
                    close()
                    moveTo(7.53f, 9.8f)
                    lineTo(9.08f, 11.350000000000001f)
                    curveTo(9.08f, 13.010000000000002f, 10.42f, 14.350000000000001f, 12.08f, 14.350000000000001f)
                    lineTo(13.63f, 15.900000000000002f)
                    curveTo(10.870000000000001f, 15.900000000000002f, 8.63f, 13.660000000000002f, 8.63f, 10.900000000000002f)
                    close()
                    moveTo(11.84f, 9.020000000000001f)
                    lineTo(14.99f, 8.860000000000001f)
                    curveTo(14.99f, 7.200000000000001f, 13.65f, 5.860000000000001f, 11.99f, 5.860000000000001f)
                    close()
                }
            }.build()
            return _VisibilityOff!!
        }

    private var _VisibilityOff: ImageVector? = null

    val VolumeOff: ImageVector
        get() {
            if (_VolumeOff != null) return _VolumeOff!!
            _VolumeOff = ImageVector.Builder(
                name = "VolumeOff",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(16.5f, 12.0f)
                    curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14.0f, 7.97f)
                    lineTo(14.0f, 10.18f)
                    lineTo(16.45f, 12.629999999999999f)
                    close()
                    moveTo(19.0f, 12.0f)
                    curveTo(19.0f, 12.94f, 18.8f, 13.82f, 18.46f, 14.64f)
                    lineTo(19.970000000000002f, 16.150000000000002f)
                    curveTo(20.63f, 14.91f, 21.0f, 13.5f, 21.0f, 12.0f)
                    curveTo(21.0f, 7.72f, 18.009999999999998f, 4.14f, 14.0f, 3.2300000000000004f)
                    lineTo(14.0f, 5.290000000000001f)
                    close()
                    moveTo(4.27f, 3.0f)
                    lineTo(3.0f, 4.27f)
                    lineTo(7.73f, 9.0f)
                    lineTo(3.0f, 9.0f)
                    lineTo(3.0f, 15.0f)
                    lineTo(7.0f, 15.0f)
                    lineTo(12.0f, 20.0f)
                    lineTo(12.0f, 13.27f)
                    lineTo(16.25f, 17.52f)
                    lineTo(16.25f, 19.58f)
                    curveTo(17.63f, 19.27f, 18.88f, 18.63f, 19.94f, 17.77f)
                    lineTo(19.73f, 21.0f)
                    lineTo(21.0f, 19.73f)
                    lineTo(12.0f, 10.73f)
                    lineTo(4.27f, 3.0f)
                    close()
                    moveTo(12.0f, 4.0f)
                    lineTo(9.91f, 6.09f)
                    lineTo(12.0f, 8.18f)
                    lineTo(12.0f, 4.0f)
                    close()
                }
            }.build()
            return _VolumeOff!!
        }

    private var _VolumeOff: ImageVector? = null

    val VolumeUp: ImageVector
        get() {
            if (_VolumeUp != null) return _VolumeUp!!
            _VolumeUp = ImageVector.Builder(
                name = "VolumeUp",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(3.0f, 9.0f)
                    lineTo(3.0f, 15.0f)
                    lineTo(7.0f, 15.0f)
                    lineTo(12.0f, 20.0f)
                    lineTo(12.0f, 4.0f)
                    lineTo(7.0f, 9.0f)
                    lineTo(3.0f, 9.0f)
                    close()
                    moveTo(16.5f, 12.0f)
                    curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14.0f, 7.97f)
                    lineTo(14.0f, 16.02f)
                    curveTo(15.48f, 15.29f, 16.5f, 13.77f, 16.5f, 12.0f)
                    close()
                    moveTo(14.0f, 3.23f)
                    lineTo(14.0f, 5.29f)
                    curveTo(16.89f, 6.15f, 19.0f, 8.83f, 19.0f, 12.0f)
                    curveTo(19.0f, 15.17f, 16.89f, 17.85f, 14.0f, 18.71f)
                    lineTo(14.0f, 20.77f)
                    curveTo(18.01f, 19.86f, 21.0f, 16.28f, 21.0f, 12.0f)
                    curveTo(21.0f, 7.72f, 18.01f, 4.14f, 14.0f, 3.23f)
                    close()
                }
            }.build()
            return _VolumeUp!!
        }

    private var _VolumeUp: ImageVector? = null

    val VpnKey: ImageVector
        get() {
            if (_VpnKey != null) return _VpnKey!!
            _VpnKey = ImageVector.Builder(
                name = "VpnKey",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.65f, 10.0f)
                    curveTo(11.83f, 7.67f, 9.61f, 6.0f, 7.0f, 6.0f)
                    curveTo(3.69f, 6.0f, 1.0f, 8.69f, 1.0f, 12.0f)
                    curveTo(1.0f, 12.0f, 3.69f, 18.0f, 7.0f, 18.0f)
                    curveTo(9.61f, 18.0f, 11.83f, 16.33f, 12.65f, 14.0f)
                    lineTo(17.0f, 14.0f)
                    lineTo(17.0f, 18.0f)
                    lineTo(21.0f, 18.0f)
                    lineTo(21.0f, 14.0f)
                    lineTo(23.0f, 14.0f)
                    lineTo(23.0f, 10.0f)
                    lineTo(12.65f, 10.0f)
                    close()
                    moveTo(7.0f, 14.0f)
                    curveTo(5.9f, 14.0f, 5.0f, 13.1f, 5.0f, 12.0f)
                    curveTo(5.0f, 12.0f, 5.9f, 10.0f, 7.0f, 10.0f)
                    curveTo(7.0f, 10.0f, 9.0f, 10.9f, 9.0f, 12.0f)
                    curveTo(9.0f, 12.0f, 8.1f, 14.0f, 7.0f, 14.0f)
                    close()
                }
            }.build()
            return _VpnKey!!
        }

    private var _VpnKey: ImageVector? = null

    val Warning: ImageVector
        get() {
            if (_Warning != null) return _Warning!!
            _Warning = ImageVector.Builder(
                name = "Warning",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(1.0f, 21.0f)
                    lineTo(23.0f, 21.0f)
                    lineTo(12.0f, 2.0f)
                    lineTo(1.0f, 21.0f)
                    close()
                    moveTo(13.0f, 18.0f)
                    lineTo(11.0f, 18.0f)
                    lineTo(11.0f, 16.0f)
                    lineTo(13.0f, 16.0f)
                    lineTo(13.0f, 18.0f)
                    close()
                    moveTo(13.0f, 14.0f)
                    lineTo(11.0f, 14.0f)
                    lineTo(11.0f, 10.0f)
                    lineTo(13.0f, 10.0f)
                    lineTo(13.0f, 14.0f)
                    close()
                }
            }.build()
            return _Warning!!
        }

    private var _Warning: ImageVector? = null

    val ImageOutlined: ImageVector
        get() {
            if (_ImageOutlined != null) return _ImageOutlined!!
            _ImageOutlined = ImageVector.Builder(
                name = "ImageOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19.0f, 5.0f)
                    lineTo(19.0f, 19.0f)
                    lineTo(5.0f, 19.0f)
                    lineTo(5.0f, 5.0f)
                    lineTo(19.0f, 5.0f)
                    moveTo(19.0f, 3.0f)
                    lineTo(5.0f, 3.0f)
                    curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                    lineTo(3.0f, 19.0f)
                    lineTo(17.0f, 19.0f)
                    curveTo(18.1f, 19.0f, 19.0f, 18.1f, 19.0f, 17.0f)
                    lineTo(19.0f, 5.0f)
                    curveTo(19.0f, 3.9f, 18.1f, 3.0f, 17.0f, 3.0f)
                    close()
                    moveTo(14.14f, 11.86f)
                    lineTo(11.14f, 15.73f)
                    lineTo(9.0f, 13.14f)
                    lineTo(6.0f, 17.0f)
                    lineTo(18.0f, 17.0f)
                    lineTo(14.14f, 11.86f)
                    close()
                }
            }.build()
            return _ImageOutlined!!
        }

    private var _ImageOutlined: ImageVector? = null

    val InfoOutlined: ImageVector
        get() {
            if (_InfoOutlined != null) return _InfoOutlined!!
            _InfoOutlined = ImageVector.Builder(
                name = "InfoOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(11.0f, 7.0f)
                    lineTo(13.0f, 7.0f)
                    lineTo(13.0f, 9.0f)
                    lineTo(11.0f, 9.0f)
                    close()
                    moveTo(11.0f, 11.0f)
                    lineTo(13.0f, 11.0f)
                    lineTo(13.0f, 17.0f)
                    lineTo(11.0f, 17.0f)
                    close()
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 6.48f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(12.0f, 20.0f)
                    curveTo(7.59f, 20.0f, 4.0f, 16.41f, 4.0f, 12.0f)
                    curveTo(4.0f, 12.0f, 7.59f, 4.0f, 12.0f, 4.0f)
                    curveTo(12.0f, 4.0f, 20.0f, 7.59f, 20.0f, 12.0f)
                    curveTo(20.0f, 12.0f, 16.41f, 20.0f, 12.0f, 20.0f)
                    close()
                }
            }.build()
            return _InfoOutlined!!
        }

    private var _InfoOutlined: ImageVector? = null

    val LinkOutlined: ImageVector
        get() {
            if (_LinkOutlined != null) return _LinkOutlined!!
            _LinkOutlined = ImageVector.Builder(
                name = "LinkOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(17.0f, 7.0f)
                    lineTo(13.0f, 7.0f)
                    lineTo(13.0f, 9.0f)
                    lineTo(17.0f, 9.0f)
                    curveTo(18.65f, 9.0f, 20.0f, 10.35f, 20.0f, 12.0f)
                    curveTo(20.0f, 12.0f, 18.65f, 15.0f, 17.0f, 15.0f)
                    lineTo(13.0f, 15.0f)
                    lineTo(13.0f, 17.0f)
                    lineTo(17.0f, 17.0f)
                    curveTo(19.759999999999998f, 17.0f, 22.0f, 14.76f, 22.0f, 12.0f)
                    curveTo(22.0f, 12.0f, 19.759999999999998f, 7.0f, 17.0f, 7.0f)
                    close()
                    moveTo(11.0f, 15.0f)
                    lineTo(7.0f, 15.0f)
                    curveTo(5.35f, 15.0f, 4.0f, 13.65f, 4.0f, 12.0f)
                    curveTo(4.0f, 12.0f, 5.35f, 9.0f, 7.0f, 9.0f)
                    lineTo(11.0f, 9.0f)
                    lineTo(11.0f, 7.0f)
                    lineTo(7.0f, 7.0f)
                    curveTo(4.24f, 7.0f, 2.0f, 9.24f, 2.0f, 12.0f)
                    curveTo(2.0f, 12.0f, 4.24f, 17.0f, 7.0f, 17.0f)
                    lineTo(11.0f, 17.0f)
                    lineTo(11.0f, 15.0f)
                    close()
                    moveTo(8.0f, 11.0f)
                    lineTo(16.0f, 11.0f)
                    lineTo(16.0f, 13.0f)
                    lineTo(8.0f, 13.0f)
                    close()
                }
            }.build()
            return _LinkOutlined!!
        }

    private var _LinkOutlined: ImageVector? = null

    val LocationOffOutlined: ImageVector
        get() {
            if (_LocationOffOutlined != null) return _LocationOffOutlined!!
            _LocationOffOutlined = ImageVector.Builder(
                name = "LocationOffOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 4.0f)
                    curveTo(14.76f, 4.0f, 17.0f, 6.24f, 17.0f, 9.0f)
                    curveTo(17.0f, 10.06f, 16.61f, 11.32f, 16.0f, 12.620000000000001f)
                    lineTo(17.49f, 14.110000000000001f)
                    curveTo(18.37f, 12.36f, 19.0f, 10.57f, 19.0f, 9.0f)
                    curveTo(19.0f, 5.13f, 15.870000000000001f, 2.0f, 12.0f, 2.0f)
                    lineTo(13.43f, 3.4299999999999997f)
                    curveTo(9.56f, 4.5f, 10.72f, 4.0f, 12.0f, 4.0f)
                    close()
                    moveTo(12.0f, 6.5f)
                    lineTo(15.5f, 10.0f)
                    curveTo(15.85f, 8.44f, 15.5f, 8.620000000000001f, 14.379999999999999f, 7.5f)
                    close()
                    moveTo(3.41f, 2.86f)
                    lineTo(2.0f, 4.27f)
                    lineTo(5.18f, 7.449999999999999f)
                    curveTo(5.07f, 7.95f, 5.0f, 8.47f, 5.0f, 9.0f)
                    curveTo(5.0f, 14.25f, 12.0f, 22.0f, 12.0f, 22.0f)
                    curveTo(12.0f, 22.0f, 13.67f, 20.15f, 15.379999999999999f, 17.65f)
                    lineTo(18.73f, 21.0f)
                    lineTo(20.14f, 19.59f)
                    lineTo(3.41f, 2.86f)
                    close()
                    moveTo(12.0f, 18.88f)
                    curveTo(9.99f, 16.299999999999997f, 7.2f, 12.139999999999999f, 7.02f, 9.29f)
                    lineTo(13.94f, 16.21f)
                    close()
                }
            }.build()
            return _LocationOffOutlined!!
        }

    private var _LocationOffOutlined: ImageVector? = null

    val MicOffOutlined: ImageVector
        get() {
            if (_MicOffOutlined != null) return _MicOffOutlined!!
            _MicOffOutlined = ImageVector.Builder(
                name = "MicOffOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(10.8f, 4.9f)
                    lineTo(10.790000000000001f, 8.81f)
                    lineTo(15.0f, 10.6f)
                    lineTo(15.0f, 5.0f)
                    curveTo(15.0f, 3.34f, 13.66f, 2.0f, 12.0f, 2.0f)
                    curveTo(10.46f, 2.0f, 9.21f, 3.16f, 9.04f, 4.65f)
                    lineTo(10.799999999999999f, 6.41f)
                    lineTo(10.799999999999999f, 4.9f)
                    close()
                    moveTo(19.0f, 11.0f)
                    lineTo(17.3f, 11.0f)
                    curveTo(17.3f, 11.58f, 17.2f, 12.129999999999999f, 17.03f, 12.64f)
                    lineTo(18.3f, 13.91f)
                    close()
                    moveTo(4.41f, 2.86f)
                    lineTo(3.0f, 4.27f)
                    lineTo(9.0f, 10.27f)
                    lineTo(9.0f, 11.0f)
                    curveTo(9.0f, 12.66f, 10.34f, 14.0f, 12.0f, 14.0f)
                    lineTo(13.66f, 15.66f)
                    curveTo(10.9f, 15.66f, 8.36f, 13.56f, 8.36f, 10.56f)
                    lineTo(5.0f, 10.56f)
                    curveTo(5.0f, 13.97f, 7.720000000000001f, 16.79f, 11.0f, 17.28f)
                    lineTo(11.0f, 21.0f)
                    lineTo(13.0f, 21.0f)
                    lineTo(13.0f, 17.72f)
                    curveTo(13.91f, 17.59f, 14.77f, 17.27f, 15.55f, 16.82f)
                    lineTo(19.75f, 21.02f)
                    lineTo(21.16f, 19.61f)
                    lineTo(4.41f, 2.86f)
                    close()
                }
            }.build()
            return _MicOffOutlined!!
        }

    private var _MicOffOutlined: ImageVector? = null

    val NotificationsOffOutlined: ImageVector
        get() {
            if (_NotificationsOffOutlined != null) return _NotificationsOffOutlined!!
            _NotificationsOffOutlined = ImageVector.Builder(
                name = "NotificationsOffOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 22.0f)
                    curveTo(13.1f, 22.0f, 14.0f, 21.1f, 14.0f, 20.0f)
                    lineTo(10.0f, 20.0f)
                    close()
                    moveTo(12.0f, 6.5f)
                    curveTo(14.49f, 6.5f, 16.0f, 8.52f, 16.0f, 11.0f)
                    lineTo(16.0f, 11.1f)
                    lineTo(18.0f, 13.1f)
                    lineTo(18.0f, 11.0f)
                    curveTo(18.0f, 7.93f, 16.37f, 5.36f, 13.5f, 4.68f)
                    lineTo(13.5f, 4.0f)
                    curveTo(13.5f, 3.17f, 12.83f, 2.5f, 12.0f, 2.5f)
                    lineTo(12.0f, 3.18f)
                    lineTo(13.64f, 4.82f)
                    close()
                    moveTo(5.41f, 3.35f)
                    lineTo(4.0f, 4.76f)
                    lineTo(6.8100000000000005f, 7.57f)
                    curveTo(6.29f, 8.57f, 6.0f, 9.74f, 6.0f, 11.0f)
                    lineTo(6.0f, 16.0f)
                    lineTo(4.0f, 18.0f)
                    lineTo(4.0f, 19.0f)
                    lineTo(18.240000000000002f, 19.0f)
                    lineTo(19.98f, 20.74f)
                    lineTo(21.39f, 19.33f)
                    lineTo(5.41f, 3.35f)
                    close()
                    moveTo(16.0f, 17.0f)
                    lineTo(8.0f, 17.0f)
                    lineTo(8.0f, 11.0f)
                    lineTo(16.0f, 16.76f)
                    lineTo(16.0f, 17.0f)
                    close()
                }
            }.build()
            return _NotificationsOffOutlined!!
        }

    private var _NotificationsOffOutlined: ImageVector? = null

    val SecurityOutlined: ImageVector
        get() {
            if (_SecurityOutlined != null) return _SecurityOutlined!!
            _SecurityOutlined = ImageVector.Builder(
                name = "SecurityOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12.0f, 1.0f)
                    lineTo(3.0f, 5.0f)
                    lineTo(3.0f, 11.0f)
                    curveTo(3.0f, 16.55f, 6.84f, 21.740000000000002f, 12.0f, 23.0f)
                    curveTo(17.16f, 21.74f, 21.0f, 16.55f, 21.0f, 11.0f)
                    lineTo(21.0f, 5.0f)
                    lineTo(12.0f, 1.0f)
                    close()
                    moveTo(12.0f, 11.99f)
                    lineTo(19.0f, 11.99f)
                    curveTo(18.47f, 16.11f, 15.72f, 19.78f, 12.0f, 20.93f)
                    lineTo(12.0f, 12.0f)
                    lineTo(5.0f, 12.0f)
                    lineTo(5.0f, 6.3f)
                    lineTo(12.0f, 3.19f)
                    lineTo(12.0f, 11.99f)
                    close()
                }
            }.build()
            return _SecurityOutlined!!
        }

    private var _SecurityOutlined: ImageVector? = null

    val VideocamOffOutlined: ImageVector
        get() {
            if (_VideocamOffOutlined != null) return _VideocamOffOutlined!!
            _VideocamOffOutlined = ImageVector.Builder(
                name = "VideocamOffOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(9.56f, 8.0f)
                    lineTo(7.5600000000000005f, 6.0f)
                    lineTo(3.41f, 1.8600000000000003f)
                    lineTo(2.0f, 3.27f)
                    lineTo(4.73f, 6.0f)
                    lineTo(4.0f, 6.0f)
                    curveTo(3.45f, 6.0f, 3.0f, 6.45f, 3.0f, 7.0f)
                    lineTo(3.0f, 17.0f)
                    lineTo(15.0f, 17.0f)
                    lineTo(19.73f, 21.0f)
                    lineTo(21.14f, 19.59f)
                    lineTo(12.280000000000001f, 10.73f)
                    lineTo(9.56f, 8.0f)
                    close()
                    moveTo(5.0f, 16.0f)
                    lineTo(5.0f, 8.0f)
                    lineTo(6.73f, 8.0f)
                    lineTo(14.73f, 16.0f)
                    lineTo(5.0f, 16.0f)
                    close()
                    moveTo(15.0f, 8.0f)
                    lineTo(15.0f, 10.61f)
                    lineTo(21.0f, 16.61f)
                    lineTo(21.0f, 6.5f)
                    lineTo(17.0f, 10.5f)
                    lineTo(17.0f, 7.0f)
                    curveTo(17.0f, 6.45f, 16.55f, 6.0f, 16.0f, 6.0f)
                    lineTo(10.39f, 6.0f)
                    lineTo(12.39f, 8.0f)
                    lineTo(15.0f, 8.0f)
                    close()
                }
            }.build()
            return _VideocamOffOutlined!!
        }

    private var _VideocamOffOutlined: ImageVector? = null

    val WarningOutlined: ImageVector
        get() {
            if (_WarningOutlined != null) return _WarningOutlined!!
            _WarningOutlined = ImageVector.Builder(
                name = "WarningOutlined",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(1.0f, 21.0f)
                    lineTo(23.0f, 21.0f)
                    lineTo(12.0f, 2.0f)
                    lineTo(1.0f, 21.0f)
                    close()
                    moveTo(13.0f, 18.0f)
                    lineTo(11.0f, 18.0f)
                    lineTo(11.0f, 16.0f)
                    lineTo(13.0f, 16.0f)
                    lineTo(13.0f, 18.0f)
                    close()
                    moveTo(13.0f, 14.0f)
                    lineTo(11.0f, 14.0f)
                    lineTo(11.0f, 10.0f)
                    lineTo(13.0f, 10.0f)
                    lineTo(13.0f, 14.0f)
                    close()
                }
            }.build()
            return _WarningOutlined!!
        }

    private var _WarningOutlined: ImageVector? = null

    val Bookmark: ImageVector
        get() {
            if (_Bookmark != null) return _Bookmark!!
            _Bookmark = ImageVector.Builder(
                name = "Bookmark",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(17.0f, 3.0f)
                    lineTo(7.0f, 3.0f)
                    curveTo(5.9f, 3.0f, 5.0f, 3.9f, 5.0f, 5.0f)
                    lineTo(5.0f, 21.0f)
                    lineTo(12.0f, 18.0f)
                    lineTo(19.0f, 21.0f)
                    lineTo(19.0f, 5.0f)
                    curveTo(19.0f, 3.9f, 18.1f, 3.0f, 17.0f, 3.0f)
                    close()
                }
            }.build()
            return _Bookmark!!
        }

    private var _Bookmark: ImageVector? = null

    val Extension: ImageVector
        get() {
            if (_Extension != null) return _Extension!!
            _Extension = ImageVector.Builder(
                name = "Extension",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(20.0f, 11.0f)
                    lineTo(20.0f, 7.0f)
                    curveTo(20.0f, 5.9f, 19.1f, 5.0f, 18.0f, 5.0f)
                    lineTo(14.0f, 5.0f)
                    curveTo(14.0f, 3.62f, 12.88f, 2.5f, 11.5f, 2.5f)
                    curveTo(10.12f, 2.5f, 9.0f, 3.62f, 9.0f, 5.0f)
                    lineTo(5.0f, 5.0f)
                    curveTo(3.9f, 5.0f, 3.0f, 5.9f, 3.0f, 7.0f)
                    lineTo(3.0f, 10.8f)
                    curveTo(4.38f, 10.8f, 5.5f, 11.92f, 5.5f, 13.3f)
                    curveTo(5.5f, 14.68f, 4.38f, 15.8f, 3.0f, 15.8f)
                    lineTo(3.0f, 19.0f)
                    curveTo(3.0f, 20.1f, 3.9f, 21.0f, 5.0f, 21.0f)
                    lineTo(8.8f, 21.0f)
                    curveTo(8.8f, 19.62f, 9.92f, 18.5f, 11.3f, 18.5f)
                    curveTo(12.68f, 18.5f, 13.8f, 19.62f, 13.8f, 21.0f)
                    lineTo(17.0f, 21.0f)
                    curveTo(18.1f, 21.0f, 19.0f, 20.1f, 19.0f, 19.0f)
                    lineTo(19.0f, 15.2f)
                    curveTo(20.38f, 15.2f, 21.5f, 14.08f, 21.5f, 12.7f)
                    curveTo(21.5f, 11.32f, 20.38f, 11.0f, 20.0f, 11.0f)
                    close()
                }
            }.build()
            return _Extension!!
        }

    private var _Extension: ImageVector? = null
}