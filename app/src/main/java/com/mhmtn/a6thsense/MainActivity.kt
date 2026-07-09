package com.mhmtn.a6thsense

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.mhmtn.a6thsense.activity.domain.DailyActivityContract
import com.mhmtn.a6thsense.activity.presentation.components.AlreadyCompletedScreen
import com.mhmtn.a6thsense.activity.presentation.components.SoftBackground
import com.mhmtn.a6thsense.activity.presentation.DailyActivityRoute
import com.mhmtn.a6thsense.activity.presentation.NoMatchScreen
import com.mhmtn.a6thsense.auth.presentation.AuthRoute
import com.mhmtn.a6thsense.billing.domain.BillingRepository
import com.mhmtn.a6thsense.contact.presentation.ContactUsRoute
import com.mhmtn.a6thsense.conversations.domain.ConversationRepository
import com.mhmtn.a6thsense.conversations.presentation.ConversationListRoute
import com.mhmtn.a6thsense.core.data.NetworkConnectivityObserver
import com.mhmtn.a6thsense.core.domain.ConnectivityObserver
import com.mhmtn.a6thsense.core.presentation.AppBottomNavigation
import com.mhmtn.a6thsense.core.presentation.AppStartRoute
import com.mhmtn.a6thsense.core.presentation.NoInternetScreen
import com.mhmtn.a6thsense.core.presentation.Routes
import com.mhmtn.a6thsense.core.presentation.fadeInTransition
import com.mhmtn.a6thsense.core.presentation.fadeOutTransition
import com.mhmtn.a6thsense.core.presentation.scaleInTransition
import com.mhmtn.a6thsense.core.presentation.scaleOutTransition
import com.mhmtn.a6thsense.core.presentation.slideInFromBottom
import com.mhmtn.a6thsense.core.presentation.slideInFromLeft
import com.mhmtn.a6thsense.core.presentation.slideInFromRight
import com.mhmtn.a6thsense.core.presentation.slideOutToBottom
import com.mhmtn.a6thsense.core.presentation.slideOutToLeft
import com.mhmtn.a6thsense.core.presentation.slideOutToRight
import com.mhmtn.a6thsense.discover.presentation.DiscoverRoute
import com.mhmtn.a6thsense.firebase.NotificationHelper
import com.mhmtn.a6thsense.firebase.data.FirebaseSelectionDataSource.Companion.CHANNEL_ID
import com.mhmtn.a6thsense.friends.presentation.FriendsRoute
import com.mhmtn.a6thsense.home.presentation.HomeRoute
import com.mhmtn.a6thsense.invite.presentation.InviteFriendsRoute
import com.mhmtn.a6thsense.matchhistory.presentation.MatchHistoryRoute
import com.mhmtn.a6thsense.messaging.presentation.MessagingRoute
import com.mhmtn.a6thsense.onboarding.domain.OnboardingRepository
import com.mhmtn.a6thsense.onboarding.presentation.OnboardingRoute
import com.mhmtn.a6thsense.premium.presentation.PaywallRoute
import com.mhmtn.a6thsense.profile.presentation.ProfileRoute
import com.mhmtn.a6thsense.settings.domain.SettingsRepository
import com.mhmtn.a6thsense.settings.presentation.SettingsRoute
import com.mhmtn.a6thsense.similarity.presentation.NoMatchYetScreen
import com.mhmtn.a6thsense.similarity.presentation.SimilarityRoute
import com.mhmtn.a6thsense.soulsync.presentation.SoulSyncRoute
import com.mhmtn.a6thsense.ui.theme._6thSenseTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var conversationRepository: ConversationRepository

    @Inject
    lateinit var onboardingRepository: OnboardingRepository

    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var connectivityObserver: ConnectivityObserver

    private var pendingNavigation by mutableStateOf<PendingNavigation?>(null)
    private var isFirstComposition by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        createNotificationChannels()
        handleDeepLink(intent)

        enableEdgeToEdge()
        lifecycleScope.launch {
            billingRepository.checkSubscriptionStatus()
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            val isDarkThemeFlow = remember(settingsRepository) {
                settingsRepository.getSettings().map { it.isDarkTheme }
            }
            val isDarkTheme by isDarkThemeFlow.collectAsStateWithLifecycle(initialValue = true)

            AppCompatDelegate.setDefaultNightMode(
                if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            val unreadCount by remember {
                auth.currentUser?.uid?.let { uid ->
                    conversationRepository.getTotalUnreadCount(uid)
                } ?: flowOf(0)
            }.collectAsStateWithLifecycle(initialValue = 0)

            _6thSenseTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.ui.graphics.Color.Transparent
                ) {
                    val networkStatus by connectivityObserver.observe()
                        .collectAsState(initial = ConnectivityObserver.Status.Available)

                    when (networkStatus) {
                        ConnectivityObserver.Status.Available -> {
                            val navController = rememberNavController()
                            val startDestination = remember {
                                when {
                                    pendingNavigation is PendingNavigation.SoulSync -> Routes.HOME
                                    auth.currentUser == null -> Routes.ONBOARDING
                                    else -> {
                                        val hasCompletedOnboarding = runBlocking { onboardingRepository.hasCompletedOnboarding().first() }
                                        if (hasCompletedOnboarding) Routes.HOME else Routes.ONBOARDING
                                    }
                                }
                            }
                            val bottomNavRoutes = listOf(Routes.HOME, Routes.DISCOVER, Routes.FRIENDS, Routes.CONVERSATIONS, Routes.PROFILE)

                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                contentWindowInsets = WindowInsets(0),
                                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                                bottomBar = {
                                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                                    if (currentRoute in bottomNavRoutes) {
                                        AppBottomNavigation(
                                            currentRoute = currentRoute,
                                            onItemSelected = { route ->
                                                navController.navigate(route) {
                                                    popUpTo(Routes.HOME) { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            unreadCount = unreadCount
                                        )
                                    }
                                }
                            ) { innerPadding ->
                                SoftBackground {
                                    NavHost(
                                        navController = navController,
                                        startDestination = startDestination,
                                        modifier = Modifier.padding(innerPadding),
                                        enterTransition = { slideInFromRight() },
                                        exitTransition = { slideOutToLeft() }
                                    ) {
                                        composable(Routes.START) { AppStartRoute(navController) }
                                        composable(Routes.ONBOARDING) {
                                            OnboardingRoute(isDark = isDarkTheme, onComplete = {
                                                navController.navigate(Routes.AUTH) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                                            })
                                        }

                                        composable(
                                            route = Routes.NO_MATCH,
                                            enterTransition = { slideInFromBottom() },
                                            exitTransition = { slideOutToBottom() }
                                        ) {
                                            NoMatchYetScreen(
                                                isDark = isDarkTheme
                                            ) {
                                                navController.navigate(Routes.HOME) {
                                                    popUpTo(Routes.NO_MATCH) { inclusive = true }
                                                }
                                            }
                                        }

                                        composable(Routes.HOME) {
                                            HomeRoute(
                                                isDark = isDarkTheme,
                                                onNavigateToDaily = { navController.navigate(Routes.DAILY) },
                                                onNavigateToSimilarity = { matchId, userName, userPhoto, similarity ->
                                                    navController.navigate(Routes.similarityRoute(matchId, userName, userPhoto, similarity))
                                                },
                                                onNavigateToAuth = { navController.navigate(Routes.AUTH) { popUpTo(Routes.HOME) { inclusive = true } } },
                                                onNavigateToMessaging = { route -> navController.navigate(route) },
                                                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                                                onNavigateToPaywall = { navController.navigate(Routes.PAYWALL) },
                                                onNavigatetoSession = { sessionType, minSimilarity -> // ✅ minSimilarity passed from effect
                                                    Log.d("Threshold"," minSimilarity: $minSimilarity")
                                                    navController.navigate(Routes.dailyActivityRoute(sessionType, minSimilarity))
                                                }
                                            )
                                        }
                                        composable(
                                            route = Routes.DAILY,
                                            arguments = listOf(
                                                navArgument("sessionType") { type = NavType.StringType },
                                                navArgument("minSimilarity") { type = NavType.IntType } // ✅ minSimilarity arg used
                                            )
                                        ) { backStackEntry ->
                                            val typeString = backStackEntry.arguments?.getString("sessionType") ?: "INTUITION"
                                            val type = try { DailyActivityContract.SessionType.valueOf(typeString) } catch (e: Exception) { DailyActivityContract.SessionType.INTUITION }
                                            val minSimilarity = backStackEntry.arguments?.getInt("minSimilarity") ?: 0 // ✅ minSimilarity arg used

                                            DailyActivityRoute(navController = navController, sessionType = type, threshold = minSimilarity)
                                        }
                                        composable(Routes.AUTH) {
                                            AuthRoute(onNavigateHome = {
                                                navController.navigate(Routes.HOME) { popUpTo(Routes.AUTH) { inclusive = true } }
                                            })
                                        }
                                        composable(Routes.SIMILARITY) {
                                            SimilarityRoute(
                                                isDark = isDarkTheme,
                                                onFinish = {
                                                    navController.navigate(Routes.HOME) {
                                                        popUpTo(Routes.HOME) { inclusive = true }
                                                    }
                                                },
                                                onNavigateToMessaging = { route ->
                                                    navController.navigate(route)
                                                },
                                                onNavigateToSoulSync = { roomId -> // 👈
                                                    navController.navigate(
                                                        Routes.soulSyncRoute(
                                                            roomId
                                                        )
                                                    )
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.MESSAGING,
                                            arguments = listOf(
                                                navArgument("conversationId") {
                                                    type = NavType.StringType
                                                },
                                                navArgument("matchedUserName") {
                                                    type = NavType.StringType
                                                },
                                                navArgument("matchedUserPhotoUrl") {
                                                    type = NavType.StringType
                                                }
                                            ),
                                            enterTransition = { slideInFromRight() },
                                            exitTransition = { slideOutToLeft() },
                                            popEnterTransition = { slideInFromLeft() },
                                            popExitTransition = { slideOutToRight() }
                                        ) {
                                            MessagingRoute(
                                                isDark = isDarkTheme,
                                                onBackClick = { navController.popBackStack() },
                                                onNavigateToPaywall = {
                                                    navController.navigate(
                                                        Routes.PAYWALL
                                                    )
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.CONVERSATIONS,
                                            enterTransition = { fadeInTransition() },
                                            exitTransition = { fadeOutTransition() },
                                            popEnterTransition = { fadeInTransition() },
                                            popExitTransition = { fadeOutTransition() }
                                        ) {
                                            ConversationListRoute(
                                                isDark = isDarkTheme,
                                                onNavigateToMessaging = { route ->
                                                    navController.navigate(route)
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.PROFILE,
                                            enterTransition = { fadeInTransition() },
                                            exitTransition = { fadeOutTransition() },
                                            popEnterTransition = { fadeInTransition() },
                                            popExitTransition = { fadeOutTransition() }
                                        ) {
                                            ProfileRoute(
                                                isDark = isDarkTheme,
                                                onNavigateToAuth = {
                                                    navController.navigate(Routes.AUTH) {
                                                        popUpTo(Routes.HOME) { inclusive = true }
                                                    }
                                                },
                                                onNavigateToMatchHistory = {
                                                    navController.navigate(Routes.MATCH_HISTORY)
                                                },
                                                onNavigateToInvite = {
                                                    navController.navigate(Routes.INVITE_FRIENDS)
                                                },
                                                onNavigateToFriends = {
                                                    navController.navigate(Routes.FRIENDS)
                                                },
                                            )
                                        }

                                        composable(
                                            route = Routes.ALREADY_COMPLETED,
                                            enterTransition = { slideInFromBottom() },
                                            exitTransition = { slideOutToBottom() }
                                        ) {
                                            AlreadyCompletedScreen(
                                                isDark = isDarkTheme,
                                                onNavigateHome = {
                                                    navController.navigate(Routes.HOME) {
                                                        popUpTo(Routes.HOME) { inclusive = true }
                                                    }
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.SETTINGS,
                                            enterTransition = { slideInFromRight() },
                                            exitTransition = { slideOutToRight() },
                                            popEnterTransition = { slideInFromLeft() },
                                            popExitTransition = { slideOutToRight() }
                                        ) {
                                            SettingsRoute(
                                                onBackClick = { navController.popBackStack() },
                                                onNavigateToContactUs = {
                                                    navController.navigate(Routes.CONTACT_US)
                                                },
                                                onNavigateToLogin = {
                                                    navController.navigate(Routes.AUTH) {
                                                        popUpTo(Routes.HOME) { inclusive = true }
                                                    }
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.MATCH_HISTORY,
                                            enterTransition = { slideInFromRight() },
                                            exitTransition = { slideOutToLeft() },
                                            popEnterTransition = { slideInFromLeft() },
                                            popExitTransition = { slideOutToRight() }
                                        ) {
                                            MatchHistoryRoute(
                                                onBackClick = { navController.popBackStack() },
                                                onNavigateToMessaging = { route ->
                                                    navController.navigate(route)
                                                },
                                                onNavigateToPaywall = {
                                                    navController.navigate(
                                                        Routes.PAYWALL
                                                    )
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.FRIENDS,
                                            enterTransition = { fadeInTransition() },
                                            exitTransition = { fadeOutTransition() },
                                            popEnterTransition = { fadeInTransition() },
                                            popExitTransition = { fadeOutTransition() }
                                        ) {
                                            FriendsRoute(
                                                isDark = isDarkTheme,
                                                onBackClick = { navController.popBackStack() },
                                                onNavigateToSoulSync = { roomId ->
                                                    navController.navigate(
                                                        Routes.soulSyncRoute(roomId)
                                                    )
                                                },
                                                onNavigateToPremium = {
                                                    navController.navigate(
                                                        Routes.PAYWALL
                                                    )
                                                },
                                                onNavigateToInvite = {
                                                    navController.navigate(Routes.INVITE_FRIENDS)
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.DISCOVER,
                                            enterTransition = { fadeInTransition() },
                                            exitTransition = { fadeOutTransition() },
                                            popEnterTransition = { fadeInTransition() },
                                            popExitTransition = { fadeOutTransition() }
                                        ) {
                                            DiscoverRoute(
                                                isDark = isDarkTheme,
                                                onNavigateToMessaging = { route ->
                                                    navController.navigate(
                                                        route
                                                    )
                                                },
                                                onNavigateToPaywall = {
                                                    navController.navigate(
                                                        Routes.PAYWALL
                                                    )
                                                }
                                            )
                                        }
                                        composable(
                                            route = Routes.SOUL_SYNC,
                                            arguments = listOf(
                                                navArgument("roomId") { type = NavType.StringType }
                                            )
                                        ) {
                                            SoulSyncRoute(
                                                isDark = isDarkTheme,
                                                onExit = {
                                                    navController.navigate(Routes.HOME) {
                                                        popUpTo(Routes.HOME) { inclusive = true }
                                                    }
                                                }
                                            )
                                        }

                                        composable(
                                            route = Routes.INVITE_FRIENDS,
                                            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
                                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
                                        ) {
                                            InviteFriendsRoute(
                                                isDark = isDarkTheme,
                                                onBackClick = { navController.popBackStack() }
                                            )
                                        }

                                        composable(
                                            route = Routes.CONTACT_US,
                                            enterTransition = { slideInFromRight() },
                                            exitTransition = { slideOutToLeft() },
                                            popEnterTransition = { slideInFromLeft() },
                                            popExitTransition = { slideOutToRight() }
                                        ) {
                                            ContactUsRoute(
                                                onNavigateBack = { navController.popBackStack() }
                                            )
                                        }

                                        composable(
                                            route = Routes.PAYWALL,
                                            enterTransition = { slideInFromBottom() },
                                            exitTransition = { slideOutToBottom() }
                                        ) {
                                            PaywallRoute(
                                                isDark = isDarkTheme,
                                                onDismiss = { navController.popBackStack() }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        ConnectivityObserver.Status.Unavailable,
                        ConnectivityObserver.Status.Lost -> {
                            NoInternetScreen(
                                onRetryClick = {}
                            )
                        }

                        ConnectivityObserver.Status.Losing -> {
                            // İsteğe bağlı
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (intent == null) return

        val navigateTo = intent.getStringExtra("navigate_to")

        Log.d("MainActivity", "Deep link: $navigateTo") // 👈 Log
        Log.d("MainActivity", "Intent extras: ${intent.extras}") // 👈 Log

        val data = intent.data
        if (data != null && data.scheme == "aurania" && data.host == "invite") {
            val referralCode = data.getQueryParameter("code")

            if (!referralCode.isNullOrBlank()) {
                Log.d("MainActivity", "Referral code from deep link: $referralCode")
                pendingNavigation = PendingNavigation.Referral(referralCode)
                isFirstComposition = true
                return
            }
        }

        when (navigateTo) {
            "soul_sync" -> {
                val roomId = intent.getStringExtra("room_id")

                if (roomId.isNullOrBlank()) {
                    Log.e("MainActivity", "Room ID is null or blank!")
                    return
                }

                Log.d("MainActivity", "Navigating to Soul Sync: $roomId")
                pendingNavigation = PendingNavigation.SoulSync(roomId)
                isFirstComposition = true
            }

            else -> {
                Log.d("MainActivity", "No deep link to handle")
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 1. Match notifications channel
            val matchChannel = NotificationChannel(
                CHANNEL_ID,
                "Eşleşmeler",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Match Notifications"
                enableLights(true)
                lightColor = Color.parseColor("#4568DC")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }

            // 2. Soul Sync channel
            val soulSyncChannel = NotificationChannel(
                "soul_sync",
                "Soul Sync",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Soul Sync Invites"
                enableLights(true)
                lightColor = Color.parseColor("#4568DC")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 100, 300, 100, 300)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }

            val messageChannel = NotificationChannel(
                "message_channel",
                "Mesajlar",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Message Notifications"
                enableLights(true)
                lightColor = Color.parseColor("#4568DC")
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(matchChannel)
            notificationManager.createNotificationChannel(soulSyncChannel)
            notificationManager.createNotificationChannel(messageChannel)
        }
    }
}

sealed class PendingNavigation {
    data class SoulSync(val roomId: String) : PendingNavigation()
    data class Referral(val code: String) : PendingNavigation()
}