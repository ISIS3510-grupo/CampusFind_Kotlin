package com.CampusFind.com.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.CampusFind.com.ui.screens.*
import com.CampusFind.com.viewmodel.AuthenticationViewModel

object Routes {
    const val ADMIN_OWNERSHIP = "admin_ownership"
    const val ADMIN_CLAIMS = "admin_claims"
    const val ADMIN_REPORT_TIMES = "admin_report_times"
    const val ADMIN_HOME = "admin_home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val REPORT_LOST = "report_lost"
    const val LOST_REPORT_DETAIL = "lost_report_detail"
    const val MATCH_ALERT = "match_alert"
    const val REPORT_FOUND = "report_found"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onStudentLogin = {
                    navController.navigate(Routes.PROFILE)
                },
                onStaffLogin = {
                    navController.navigate(Routes.ADMIN_HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_HOME) {
            val authenticationViewModel: AuthenticationViewModel = viewModel()

            AdminHomeScreen(
                email = authenticationViewModel.getCurrentUserEmail(),
                onRegisterFound = {
                    navController.navigate(Routes.REPORT_FOUND)
                },
                onReportTimes = {
                    navController.navigate(Routes.ADMIN_REPORT_TIMES)
                },
                onReviewClaims = {
                    navController.navigate(Routes.ADMIN_CLAIMS)
                },
                onOwnershipRates = {
                    navController.navigate(Routes.ADMIN_OWNERSHIP)
                },
                onLogout = {
                    authenticationViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ADMIN_HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_OWNERSHIP) {
            AdminOwnershipScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ADMIN_CLAIMS) {
            AdminClaimsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ADMIN_REPORT_TIMES) {
            AdminReportTimesScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onReportLost = {
                    navController.navigate(Routes.REPORT_LOST)
                },
                onOpenReport = {
                    navController.navigate(Routes.LOST_REPORT_DETAIL)
                }
            )
        }

        composable(Routes.REPORT_LOST) {
            ReportLostItemScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLocationClick = {
                    // Aquí conectaremos GPS / location-aware.
                },
                onSubmit = {
                    navController.navigate(Routes.LOST_REPORT_DETAIL)
                }
            )
        }

        composable(Routes.LOST_REPORT_DETAIL) {
            LostReportDetailScreen(
                onBack = {
                    navController.popBackStack()
                },
                onMatchClick = {
                    navController.navigate(Routes.MATCH_ALERT)
                },
                onRecoveredElsewhere = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.MATCH_ALERT) {
            MatchAlertScreen(
                onBack = {
                    navController.popBackStack()
                },
                onReviewMatch = {
                    // Más adelante irá al detalle real del found item.
                },
                onNotMine = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.REPORT_FOUND) {
            ReportFoundItemScreen(
                onBack = {
                    navController.popBackStack()
                },
                onContinue = {
                    // Luego irá a Drop-off Instructions.
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}

