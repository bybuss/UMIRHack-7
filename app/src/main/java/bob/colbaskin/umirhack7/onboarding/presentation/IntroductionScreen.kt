package bob.colbaskin.umirhack7.onboarding.presentation

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bob.colbaskin.umirhack7.R
import bob.colbaskin.umirhack7.common.design_system.Lottie
import bob.colbaskin.umirhack7.common.design_system.PagerWithIndicator
import kotlinx.coroutines.launch

@Composable
fun IntroductionScreen(
    onNextScreen: () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    OnBoarding(onNextScreen, viewModel::action)
}

@Composable
private fun OnBoarding(
    onNextScreen: () -> Unit,
    dispatch: (OnBoardingAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coroutineScope = rememberCoroutineScope()
        val pageCount = OnBoardingPage.allPages.size
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { pageCount }
        )
        val buttonText = if (pagerState.currentPage == pageCount - 1) "Начать!" else "Дальше"

        PagerWithIndicator(
            pageCount = pageCount,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 24.dp),
            pagerState = pagerState
        ) { position ->
            val page = OnBoardingPage.allPages[position]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier
                        .weight(0.4f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Lottie(lottieJson = page.lottieJson)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.currentPage < pageCount - 1) {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    } else {
                        dispatch(OnBoardingAction.OnboardingComplete)
                        onNextScreen()
                    }
                }
            }
        ) {
            Text(text = buttonText)
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = {
                dispatch(OnBoardingAction.OnboardingComplete)
                onNextScreen()
            },
        ) {
            Text(text = "Пропустить")
        }
    }
}

private sealed class OnBoardingPage(
    @RawRes val lottieJson: Int,
    val title: String,
    val description: String
) {
    data object First : OnBoardingPage(
        lottieJson = R.raw.first,
        title = "Ваши поля под контролем",
        description = "Добавляйте границы полей на карте, ведите историю посадок. Весь ваш севооборот — в одном месте."
    )

    data object Second : OnBoardingPage(
        lottieJson = R.raw.second,
        title = "Умные подсказки для почвы",
        description = "Приложение подскажет, что посадить следующим, чтобы восстановить плодородие и повысить урожай."
    )

    data object Third : OnBoardingPage(
        lottieJson = R.raw.third,
        title = "Расчеты и оффлайн-работа",
        description = "Оценивайте рентабельность культур и вносите данные о почве прямо в поле. Все доступно без интернета."
    )

    companion object {
        val allPages = listOf(First, Second, Third)
    }
}
