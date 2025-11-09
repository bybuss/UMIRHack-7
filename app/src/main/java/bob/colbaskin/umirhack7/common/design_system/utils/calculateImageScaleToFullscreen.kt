package bob.colbaskin.umirhack7.common.design_system.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun calculateImageScaleToFullscreen(
    image: Painter,
    screenWidth: Float,
    screenHeight: Float
): Float {

    val imageWidth = image.intrinsicSize.width
    val imageHeight = image.intrinsicSize.height

    val scaleX = screenWidth / imageWidth
    val scaleY = screenHeight / imageHeight

    return maxOf(scaleX, scaleY)
}
