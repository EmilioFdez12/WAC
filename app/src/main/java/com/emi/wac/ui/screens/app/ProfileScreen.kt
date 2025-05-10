package com.emi.wac.ui.screens.app

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.emi.wac.R
import com.emi.wac.common.Constants
import com.emi.wac.common.Constants.CATEGORY_F1
import com.emi.wac.common.Constants.CATEGORY_MOTOGP
import com.emi.wac.common.Constants.LEXENDBOLD
import com.emi.wac.data.model.NotificationPreferences
import com.emi.wac.data.repository.AuthRepository
import com.emi.wac.ui.components.login.CustomButton
import com.emi.wac.ui.theme.PrimaryRed
import com.emi.wac.viewmodel.NotificationPreferencesViewModel

@Composable
fun ProfileScreen(
    authRepository: AuthRepository,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    notificationViewModel: NotificationPreferencesViewModel = viewModel()
) {
    val backgroundPainter = rememberAsyncImagePainter(model = Constants.BCKG_IMG)
    val currentUser = authRepository.getCurrentUser()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    // Obtener las preferencias de notificaciones
    val preferences by notificationViewModel.preferencesState.collectAsState()

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
            // Tarjeta de perfil
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF404040),
                                    Color(0xFF151515),
                                    Color(0xFF151515)
                                )
                            )
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Log.d("ProfileScreen", "Photo URL: ${currentUser?.photoUrl}")
                            if (currentUser?.photoUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentUser.photoUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile pic",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.wac_logo),
                                    contentDescription = "Default profile pic",
                                    modifier = Modifier.size(80.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Username
                        Text(
                            text = currentUser?.displayName ?: "User",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Email
                        Text(
                            text = currentUser?.email ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
            
            // Sección de Preferencias de Notificaciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF404040),
                                    Color(0xFF151515),
                                    Color(0xFF151515)
                                )
                            )
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Preferencias de Notificaciones",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Sección de Categorías
                        Text(
                            text = "Categorías",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                        
                        preferences?.selectedCategories?.forEach { (category, isSelected) ->
                            val categoryName = when(category) {
                                "f1" -> "Formula 1"
                                "motogp" -> "MotoGP"
                                "formula_e" -> "Formula E"
                                "wrc" -> "WRC"
                                else -> category.capitalize()
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { 
                                        notificationViewModel.updateCategoryPreference(category, it)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryRed,
                                        uncheckedColor = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                            }
                        }
                        
                        Divider(
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        // Sección de Tipos de Sesiones
                        Text(
                            text = "Tipos de Sesiones",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                        )
                        
                        preferences?.sessionTypes?.forEach { (sessionType, isSelected) ->
                            val sessionName = when(sessionType) {
                                "practice" -> "Entrenamientos"
                                "qualifying" -> "Clasificación"
                                "sprint" -> "Sprint"
                                "race" -> "Carrera"
                                else -> sessionType.capitalize()
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { 
                                        notificationViewModel.updateSessionTypePreference(sessionType, it)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PrimaryRed,
                                        uncheckedColor = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = sessionName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                            }
                        }
                        
                        Divider(
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        
                        // Sección de Notificaciones por Pilotos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notificaciones por Pilotos",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = preferences?.driverNotifications ?: false,
                                onCheckedChange = { 
                                    notificationViewModel.updateDriverNotificationsPreference(it)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = PrimaryRed,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }
                        
                        if (preferences?.driverNotifications == true) {
                            Text(
                                text = "Recibirás notificaciones sobre la posición final de tus pilotos favoritos después de cada carrera.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            // Aquí se podría añadir un selector de pilotos favoritos
                            // que se implementaría en una futura actualización
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de Cerrar Sesión
            CustomButton(
                text = "CERRAR SESIÓN",
                gradientColors = listOf(PrimaryRed, Color(0xFFB71C1C)),
                onClick = { showLogoutDialog = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    fontSize = 20.sp,
                    fontFamily = LEXENDBOLD,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro que deseas cerrar sesión?",
                    fontSize = 16.sp,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        authRepository.signOut()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRed
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showLogoutDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White
                    )
                }
            },
            containerColor = Color(0xFF202020),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}