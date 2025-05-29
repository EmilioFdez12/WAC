package com.emi.wac.ui.screens.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.emi.wac.common.Constants
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_INDYCAR
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.data.model.auth.UserPreference
import com.emi.wac.data.model.drivers.Driver
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.data.repository.StandingsRepository
import com.emi.wac.data.repository.UserPreferencesRepository
import com.emi.wac.ui.components.profile.LogoutSection
import com.emi.wac.ui.components.profile.NotificationPreferencesCard
import com.emi.wac.ui.components.profile.ProfileHeader
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Data class to represent a category preference for notifications and favorite driver.
 *
 * @param name The category identifier (e.g., CATEGORY_F1)
 * @param enabled Whether notifications are enabled for the category
 * @param favoriteDriver The name of the favorite driver for the category
 */
data class CategoryPreference(
    val name: String,
    val enabled: Boolean = false,
    val favoriteDriver: String = ""
)

/**
 * Composable function to display the profile screen.
 * Shows user profile information, notification preferences, and a logout option.
 *
 * @param authRepository Repository for authentication operations
 * @param onLogout Callback invoked when the user logs out
 * @param modifier Modifier for the composable layout
 */
@Composable
fun ProfileScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundPainter = rememberAsyncImagePainter(model = Constants.BCKG_IMG)
    // Get the current authenticated user
    val currentUser = authRepository.getCurrentUser()
    // State to control the logout dialog visibility
    var showLogoutDialog by remember { mutableStateOf(false) }
    // Scroll state for the vertical column
    val scrollState = rememberScrollState()
    val db = Firebase.firestore
    // Repositories for standings and user preferences
    val standingsRepository = remember { StandingsRepository(db) }
    val userPreferencesRepository = remember { UserPreferencesRepository(db) }

    // State for category preferences (notifications and favorite drivers)
    var categoryPreferences by remember {
        mutableStateOf(
            listOf(
                CategoryPreference(CATEGORY_F1),
                CategoryPreference(CATEGORY_MOTOGP),
                CategoryPreference(CATEGORY_INDYCAR)
            )
        )
    }

    // State for available drivers per category
    var driversMap by remember { mutableStateOf<Map<String, List<Driver>>>(emptyMap()) }

    // State to track which category's driver dropdown is expanded
    var expandedDropdownCategory by remember { mutableStateOf<String?>(null) }

    // Load user preferences and update FCM token when user is authenticated
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            // Update FCM token for push notifications
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                userPreferencesRepository.updateFcmToken(userId, token)
            }

            // Load user preferences from repository
            val userPrefs = userPreferencesRepository.getUserPreferences(userId)
            if (userPrefs != null) {
                categoryPreferences = categoryPreferences.map { pref ->
                    val savedPref = userPrefs.find { it.category == pref.name }
                    if (savedPref != null) {
                        pref.copy(
                            enabled = savedPref.notificationsEnabled,
                            favoriteDriver = savedPref.favoriteDriver ?: ""
                        )
                    } else {
                        pref
                    }
                }
            }
        }
    }

    // Load drivers for each category
    LaunchedEffect(Unit) {
        val categories = listOf(CATEGORY_F1, CATEGORY_MOTOGP, CATEGORY_INDYCAR)
        val driversResult = mutableMapOf<String, List<Driver>>()

        categories.forEach { category ->
            val result = standingsRepository.getDriverStandings(category)
            if (result.isSuccess) {
                result.getOrNull()?.let { drivers ->
                    driversResult[category] = drivers
                }
            }
        }

        driversMap = driversResult
    }

    // Function to save user preferences to the repository
    fun savePreferences() {
        currentUser?.uid?.let { userId ->
            val prefsToSave = categoryPreferences.map { pref ->
                UserPreference(
                    category = pref.name,
                    notificationsEnabled = pref.enabled,
                    favoriteDriver = pref.favoriteDriver.takeIf { it.isNotEmpty() }
                )
            }
            userPreferencesRepository.saveUserPreferences(userId, prefsToSave)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Application background image
        Image(
            painter = backgroundPainter,
            contentDescription = "App Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile header component
            ProfileHeader(currentUser = currentUser)

            // Notification preferences component
            NotificationPreferencesCard(
                categoryPreferences = categoryPreferences,
                driversMap = driversMap,
                expandedDropdownCategory = expandedDropdownCategory,
                onExpandDropdown = { category -> expandedDropdownCategory = category },
                onCategoryToggle = { index, isChecked ->
                    categoryPreferences = categoryPreferences.toMutableList().apply {
                        this[index] = this[index].copy(enabled = isChecked)
                    }
                    savePreferences()
                },
                onDriverSelect = { index, driverName ->
                    categoryPreferences = categoryPreferences.toMutableList().apply {
                        this[index] = this[index].copy(favoriteDriver = driverName)
                    }
                    savePreferences()
                }
            )

            // Logout section component
            LogoutSection(
                showLogoutDialog = showLogoutDialog,
                onShowLogoutDialog = { show -> showLogoutDialog = show },
                onLogout = onLogout
            )
        }
    }
}