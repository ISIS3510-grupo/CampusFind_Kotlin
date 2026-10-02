package com.CampusFind.com.navigation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.CampusFind.com.service.MatchNotificationService
import com.CampusFind.com.ui.components.rememberLocationPermissionLauncher
import com.CampusFind.com.ui.screens.AdminClaimsScreen
import com.CampusFind.com.ui.screens.AdminHomeScreen
import com.CampusFind.com.ui.screens.AdminReportTimesScreen
import com.CampusFind.com.ui.screens.LoginScreen
import com.CampusFind.com.ui.screens.LostReportDetailScreen
import com.CampusFind.com.ui.screens.MatchAlertScreen
import com.CampusFind.com.ui.screens.ProfileScreen
import com.CampusFind.com.ui.screens.ReportFoundItemScreen
import com.CampusFind.com.ui.screens.ReportLostItemScreen
import com.CampusFind.com.viewmodel.AuthenticationViewModel
import com.CampusFind.com.viewmodel.LocationViewModel
import com.CampusFind.com.viewmodel.LostReportsViewModel
import com.CampusFind.com.viewmodel.NotificationsViewModel
import com.CampusFind.com.viewmodel.PossibleMatchesViewModel
import com.CampusFind.com.viewmodel.ProfileViewModel
import com.CampusFind.com.viewmodel.LostReportDetailViewModel
import com.CampusFind.com.viewmodel.FoundItemDetailViewModel
import com.CampusFind.com.ui.screens.DonationPotentialScreen
import com.CampusFind.com.viewmodel.DonationPotentialViewModel

object Routes {

    const val ADMIN_CLAIMS = "admin_claims"
    const val ADMIN_REPORT_TIMES = "admin_report_times"
    const val ADMIN_HOME = "admin_home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val REPORT_LOST = "report_lost"
    const val DONATION_POTENTIAL = "donation_potential"

    const val LOST_REPORT_DETAIL =
        "lost_report_detail/{reportId}"

    const val MATCH_ALERT = "match_alert"
    const val REPORT_FOUND = "report_found"

    fun lostReportDetail(
        reportId: String
    ): String {
        return "lost_report_detail/$reportId"
    }
}

@Composable
fun AppNavigation(
    notificationMatchId: String? = null,
    onNotificationNavigationHandled: () -> Unit = {}
) {

    val navController =
        rememberNavController()

    LaunchedEffect(
        notificationMatchId
    ) {

        if (
            !notificationMatchId.isNullOrBlank()
        ) {

            navController.navigate(
                Routes.MATCH_ALERT
            ) {
                launchSingleTop = true
            }

            onNotificationNavigationHandled()
        }
    }

    val context =
        LocalContext.current

    val notificationsViewModel:
            NotificationsViewModel =
        viewModel()

    val matchNotificationService =
        remember {
            MatchNotificationService(
                context
            )
        }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                notificationsViewModel
                    .startObserving()
            }
        }

    LaunchedEffect(Unit) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {

                notificationsViewModel
                    .startObserving()

            } else {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }

        } else {

            notificationsViewModel
                .startObserving()
        }
    }

    LaunchedEffect(
        notificationsViewModel
            .latestNotification
            ?.id
    ) {

        val notification =
            notificationsViewModel
                .latestNotification
                ?: return@LaunchedEffect

        val wasShown =
            matchNotificationService
                .showSmartMatchNotification(
                    notification
                )

        if (wasShown) {

            notificationsViewModel
                .markAsNotified(
                    notification.id
                )
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(
            Routes.LOGIN
        ) {

            LoginScreen(
                onStudentLogin = {

                    notificationsViewModel
                        .startObserving()

                    navController.navigate(
                        Routes.PROFILE
                    )
                },

                onStaffLogin = {

                    navController.navigate(
                        Routes.ADMIN_HOME
                    ) {
                        popUpTo(
                            Routes.LOGIN
                        ) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            Routes.ADMIN_HOME
        ) {

            val authenticationViewModel:
                    AuthenticationViewModel =
                viewModel()

            AdminHomeScreen(
                email =
                    authenticationViewModel
                        .getCurrentUserEmail(),

                onRegisterFound = {
                    navController.navigate(
                        Routes.REPORT_FOUND
                    )
                },

                onReportTimes = {
                    navController.navigate(
                        Routes.ADMIN_REPORT_TIMES
                    )
                },

                onDonationPotential = {
                    navController.navigate(
                        Routes.DONATION_POTENTIAL
                    )
                },

                onReviewClaims = {
                    navController.navigate(
                        Routes.ADMIN_CLAIMS
                    )
                },

                onLogout = {

                    authenticationViewModel
                        .logout()

                    navController.navigate(
                        Routes.LOGIN
                    ) {
                        popUpTo(
                            Routes.ADMIN_HOME
                        ) {
                            inclusive = true
                        }
                    }
                }
            )


        }

        composable(
            Routes.ADMIN_CLAIMS
        ) {

            AdminClaimsScreen(
                onBack = {
                    navController
                        .popBackStack()
                }
            )
        }

        composable(
            Routes.ADMIN_REPORT_TIMES
        ) {

            AdminReportTimesScreen(
                onBack = {
                    navController
                        .popBackStack()
                }
            )
        }

        composable(
            Routes.PROFILE
        ) {

            val authenticationViewModel:
                    AuthenticationViewModel =
                viewModel()

            val lostReportsViewModel:
                    LostReportsViewModel =
                viewModel()

            val profileViewModel:
                    ProfileViewModel =
                viewModel()

            val currentUserUid =
                authenticationViewModel
                    .getCurrentUserUid()

            LaunchedEffect(
                currentUserUid
            ) {

                lostReportsViewModel
                    .observeReports(
                        currentUserUid
                    )

                profileViewModel
                    .loadUser(
                        currentUserUid
                    )
            }

            val currentUserEmail =
                authenticationViewModel
                    .getCurrentUserEmail()

            ProfileScreen(
                email = currentUserEmail,
                displayName =
                    profileViewModel.displayName,
                role =
                    profileViewModel.role,
                reports =
                    lostReportsViewModel.reports,

                onBack = {
                    navController
                        .popBackStack()
                },

                onReportLost = {
                    navController.navigate(
                        Routes.REPORT_LOST
                    )
                },

                onOpenReport = { reportId ->
                    navController.navigate(
                        Routes.lostReportDetail(
                            reportId
                        )
                    )
                }
            )
        }
        composable(
            Routes.REPORT_LOST
        ) {

            val locationViewModel:
                    LocationViewModel =
                viewModel()

            val requestLocationPermission =
                rememberLocationPermissionLauncher { status ->

                    locationViewModel
                        .onPermissionResult(
                            status
                        )
                }

            LaunchedEffect(Unit) {
                requestLocationPermission()
            }

            val suggestedLocation =
                locationViewModel
                    .suggestedCampusLocation

            ReportLostItemScreen(
                suggestedLocation =
                    suggestedLocation,

                locationError =
                    locationViewModel
                        .errorMessage,

                isLocationLoading =
                    locationViewModel
                        .isLoading,

                onBack = {
                    navController
                        .popBackStack()
                },

                onSubmit = {
                    navController
                        .popBackStack()
                }
            )
        }
        composable(
            route =
                Routes.LOST_REPORT_DETAIL,

            arguments =
                listOf(
                    navArgument(
                        "reportId"
                    ) {
                        type =
                            NavType.StringType
                    }
                )
        ) { backStackEntry ->

            val reportId =
                backStackEntry
                    .arguments
                    ?.getString(
                        "reportId"
                    )
                    ?: ""

            val possibleMatchesViewModel:
                    PossibleMatchesViewModel =
                viewModel()

            val foundItemDetailViewModel:
                    FoundItemDetailViewModel =
                viewModel()

            val lostReportDetailViewModel:
                    LostReportDetailViewModel =
                viewModel()

            LaunchedEffect(
                reportId
            ) {

                lostReportDetailViewModel
                    .loadReport(
                        reportId
                    )

                if (
                    reportId.isNotBlank()
                ) {

                    possibleMatchesViewModel
                        .observeMatches(
                            reportId =
                                reportId
                        )
                }
            }

            LaunchedEffect(
                possibleMatchesViewModel
                    .matches
                    .firstOrNull()
                    ?.foundItemId
            ) {

                val foundItemId =
                    possibleMatchesViewModel
                        .matches
                        .firstOrNull()
                        ?.foundItemId
                        ?: return@LaunchedEffect

                foundItemDetailViewModel
                    .loadFoundItem(
                        foundItemId
                    )
            }

            LostReportDetailScreen(
                report =
                    lostReportDetailViewModel
                        .report,

                foundItem =
                    foundItemDetailViewModel
                        .foundItem,

                matchCount =
                    possibleMatchesViewModel
                        .matchCount,

                onBack = {
                    navController
                        .popBackStack()
                },

                onMatchClick = {
                    navController.navigate(
                        Routes.MATCH_ALERT
                    )
                },

                onRecoveredElsewhere = {
                    navController
                        .popBackStack()
                }
            )
        }

        composable(
            Routes.MATCH_ALERT
        ) {

            MatchAlertScreen(
                onBack = {
                    navController
                        .navigateUp()
                },

                onReviewMatch = {
                    // Después conectaremos
                    // el detalle real del found item.
                },

                onNotMine = {
                    navController
                        .popBackStack()
                }
            )
        }

        composable(
            Routes.REPORT_FOUND
        ) {

            ReportFoundItemScreen(
                onBack = {
                    navController
                        .popBackStack()
                },

                onContinue = {
                    // Posteriormente irá
                    // al flujo de drop-off.
                },

                onCancel = {
                    navController
                        .popBackStack()
                }
            )
        }
        composable(Routes.DONATION_POTENTIAL) {

            val donationPotentialViewModel:
                    DonationPotentialViewModel =
                viewModel()

            LaunchedEffect(Unit) {
                donationPotentialViewModel
                    .loadDonationPotential()
            }

            DonationPotentialScreen(
                semesterId =
                    donationPotentialViewModel
                        .semesterId,

                results =
                    donationPotentialViewModel
                        .results,

                isLoading =
                    donationPotentialViewModel
                        .isLoading,

                errorMessage =
                    donationPotentialViewModel
                        .errorMessage,

                onBack = {
                    navController
                        .popBackStack()
                }
            )
        }
    }
}