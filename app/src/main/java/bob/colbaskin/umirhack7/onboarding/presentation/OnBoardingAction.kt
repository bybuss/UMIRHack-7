package bob.colbaskin.umirhack7.onboarding.presentation

interface OnBoardingAction {
    data object OnboardingInProgress: OnBoardingAction
    data object OnboardingComplete: OnBoardingAction
}
