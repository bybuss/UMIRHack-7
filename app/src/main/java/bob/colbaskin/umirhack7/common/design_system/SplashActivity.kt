package bob.colbaskin.umirhack7.common.design_system

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bob.colbaskin.umirhack7.MainActivity
import bob.colbaskin.umirhack7.R
import bob.colbaskin.umirhack7.common.design_system.utils.calculateImageScaleToFullscreen

import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {

    val context = LocalContext.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWidthPx: Float = with (density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx: Float = with (density) { configuration.screenHeightDp.dp.toPx() }

    val screenCenter: Dp = with (density) { (screenWidthPx / 2).toDp() }
    val screenEnd: Dp = with (density) { screenWidthPx.toDp() }

    val logoPainter = painterResource(R.drawable.agrohub_logo)
    val logoHalfWidth: Dp = with (density) { logoPainter.intrinsicSize.width.toDp() / 2 }
    val logoScale: Float = calculateImageScaleToFullscreen(
        image = logoPainter,
        screenWidth = screenWidthPx,
        screenHeight = screenHeightPx,
    )

    var isLogoMoving by remember { mutableStateOf(false) }
    val logoTransition = updateTransition(targetState = isLogoMoving)
    val logoSize = remember { Animatable(1f) }

    val logoXOffset by logoTransition.animateDp(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { state ->
        if (state) screenCenter - logoHalfWidth else -screenEnd
    }
    val logoRotation by logoTransition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { state ->
        if (state) 360f else 0f
    }

    LaunchedEffect(true) {
        delay(500)
        isLogoMoving = true
        delay(1_500)
        logoSize.animateTo(
            targetValue = logoScale,
            animationSpec = tween(500, easing = LinearEasing)
        )
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as? Activity)?.finish()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.agrohub_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = -logoXOffset)
                .layout { measurable, constraints ->
                    val scaleFactor = (logoSize.value * 2).coerceAtLeast(1f)
                    val scaledConstraints = constraints.copy(
                        minWidth = (constraints.minWidth * scaleFactor).toInt(),
                        maxWidth = (constraints.maxWidth * scaleFactor).toInt(),
                        minHeight = (constraints.minHeight * scaleFactor).toInt(),
                        maxHeight = (constraints.maxHeight * scaleFactor).toInt()
                    )
                    val placeable = measurable.measure(scaledConstraints)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
                .scale(logoSize.value)
                .rotate(-logoRotation)
        )
    }
}
