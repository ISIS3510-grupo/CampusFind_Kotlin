package com.CampusFind.com.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.CampusFind.com.ui.screens.*
import com.CampusFind.com.viewmodel.AuthenticationViewModel

object Routes {
    const val ADMIN_REPORT_TIMES = "admin_report_times"
    const val ADMIN_HOME = "admin_home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val REPORT_LOST = "report_lost"
    const val LOST_REPORT_DETAIL = "lost_report_detail"
    const val MATCH_ALERT = "match_alert"
    const val REPORT_FOUND = "report_found"
    const val HOME = "home"
    const val DROP_OFF = "drop_off"
    const val ADMIN_REPORT_BOTTLENECK = "admin_report_bottleneck"
    const val BIOMETRIC_VERIFY = "biometric_verify"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        /*
         * LOGIN
         *
         * Student -> Student Home
         * Admin   -> Admin Home
         */
        composable(Routes.LOGIN) {

            LoginScreen(
                onStudentLogin = {

                    navController.navigate(Routes.HOME) {

                        /*
                         * Removes Login from the back stack.
                         * The authenticated student cannot
                         * return to Login by pressing Back.
                         */
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },

                onStaffLogin = {

                    navController.navigate(Routes.ADMIN_HOME) {

                        /*
                         * Same behavior for staff/admin.
                         */
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        /*
         * STUDENT HOME
         */
        composable(Routes.HOME) {

            val authenticationViewModel:
                    AuthenticationViewModel = viewModel()

            HomeScreen(
                onProfile = {
                    navController.navigate(Routes.PROFILE)
                },

                onReportFound = {
                    navController.navigate(Routes.REPORT_FOUND)
                },

                onLogout = {

                    authenticationViewModel.logout()

                    navController.navigate(Routes.LOGIN) {

                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        /*
         * ADMIN HOME
         */
        composable(Routes.ADMIN_HOME) {

            val authenticationViewModel:
                    AuthenticationViewModel = viewModel()

            AdminHomeScreen(
                email =
                    authenticationViewModel.getCurrentUserEmail(),

                onRegisterFound = {
                    navController.navigate(Routes.REPORT_FOUND)
                },

                onReportTimes = {
                    navController.navigate(
                        Routes.ADMIN_REPORT_TIMES
                    )
                },

                onReportBottleneck = {
                    navController.navigate(
                        Routes.ADMIN_REPORT_BOTTLENECK
                    )
                },

                onLogout = {

                    authenticationViewModel.logout()

                    navController.navigate(Routes.LOGIN) {

                        popUpTo(Routes.ADMIN_HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        /*
         * ADMIN ANALYTICS
         */
        composable(
            Routes.ADMIN_REPORT_BOTTLENECK
        ) {

            AdminReportBottleneckScreen(
                onBack = {
                    navController
                        .popBackStack()
                }
            )
        }

        /*
         * STUDENT PROFILE
         */
        composable(Routes.PROFILE) {

            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },

                onReportLost = {
                    navController.navigate(
                        Routes.REPORT_LOST
                    )
                },

                onOpenReport = {
                    navController.navigate(
                        Routes.LOST_REPORT_DETAIL
                    )
                }
            )
        }

        /*
         * REPORT LOST ITEM
         */
        composable(Routes.REPORT_LOST) {

            ReportLostItemScreen(
                onBack = {
                    navController.popBackStack()
                },

                onLocationClick = {
                    // Aquí conectaremos GPS / location-aware.
                },

                onSubmit = {
                    navController.navigate(
                        Routes.LOST_REPORT_DETAIL
                    )
                }
            )
        }

        /*
         * LOST REPORT DETAIL
         */
        composable(Routes.LOST_REPORT_DETAIL) {

            LostReportDetailScreen(
                onBack = {
                    navController.popBackStack()
                },

                onMatchClick = {
                    navController.navigate(
                        Routes.MATCH_ALERT
                    )
                },

                onRecoveredElsewhere = {
                    navController.popBackStack()
                }
            )
        }

        /*
         * POSSIBLE MATCH
         */
        composable(Routes.MATCH_ALERT) {

            MatchAlertScreen(
                onBack = {
                    navController.popBackStack()
                },

                onReviewMatch = {

                    navController.navigate(
                        Routes.BIOMETRIC_VERIFY
                    )
                },

                onNotMine = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            Routes.BIOMETRIC_VERIFY
        ) {

            BiometricVerificationScreen(
                onBack = {

                    navController
                        .popBackStack()
                }
            )
        }

        /*
         * REPORT FOUND ITEM
         */
        composable(Routes.REPORT_FOUND) {

            ReportFoundItemScreen(
                onBack = {
                    navController.popBackStack()
                },

                onContinue = {
                    navController.navigate(
                        Routes.DROP_OFF
                    )
                },

                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DROP_OFF) {

            DropOffInstructionsScreen(

                onBack = {
                    navController.popBackStack()
                },

                onDone = {
                    navController.popBackStack()
                }
            )
        }
    }
}

