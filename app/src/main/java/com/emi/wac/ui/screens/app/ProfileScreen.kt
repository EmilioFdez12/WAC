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

data class CategoryPreference(
    val name: String,
    val enabled: Boolean = false,
    val favoriteDriver: String = ""
)

@Composable
fun ProfileScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundPainter = rememberAsyncImagePainter(model = Constants.BCKG_IMG)
    val currentUser = authRepository.getCurrentUser()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Firebase
    val db = Firebase.firestore
    val standingsRepository = remember { StandingsRepository(db) }
    val userPreferencesRepository = remember { UserPreferencesRepository(db) }

    // Estado para las preferencias de categorías
    var categoryPreferences by remember {
        mutableStateOf(
            listOf(
                CategoryPreference(CATEGORY_F1),
                CategoryPreference(CATEGORY_MOTOGP),
                CategoryPreference(CATEGORY_INDYCAR)
            )
        )
    }

    // Estado para los pilotos disponibles por categoría
    var driversMap by remember { mutableStateOf<Map<String, List<Driver>>>(emptyMap()) }

    // Estado para controlar los dropdowns de selección de pilotos
    var expandedDropdownCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            // Actualizar FCM token
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                userPreferencesRepository.updateFcmToken(userId, token)
            }

            // Cargar preferencias
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

    // Cargar los pilotos de cada categoría
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

    // Función para guardar las preferencias
    fun savePreferences() {
        currentUser?.uid?.let { userId ->
            val prefsToSave = categoryPreferences.map { pref ->
                UserPreferencesRepository.UserPreference(
                    category = pref.name,
                    notificationsEnabled = pref.enabled,
                    favoriteDriver = pref.favoriteDriver.takeIf { it.isNotEmpty() }
                )
            }
            userPreferencesRepository.saveUserPreferences(userId, prefsToSave)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Fondo de la aplicación
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
            // Componente de cabecera del perfil
            ProfileHeader(currentUser = currentUser)

            // Componente de preferencias de notificaciones
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

            // Componente de cierre de sesión
            LogoutSection(
                showLogoutDialog = showLogoutDialog,
                onShowLogoutDialog = { show -> showLogoutDialog = show },
                onLogout = onLogout
            )
        }
    }
}