package com.emi.wac.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.emi.wac.R
import com.emi.wac.data.model.auth.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GoogleSignInUtils {

    companion object {
        /**
         * Launches the Google Sign In flow.
         */
        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: () -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()
            scope.launch {
                try {
                    val result = credentialManager.getCredential(context, request)
                    when (result.credential) {
                        is CustomCredential -> {
                            if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential =
                                    GoogleIdTokenCredential.createFrom(result.credential.data)
                                val googleTokenId = googleIdTokenCredential.idToken
                                val authCredential =
                                    GoogleAuthProvider.getCredential(googleTokenId, null)
                                val user =
                                    Firebase.auth.signInWithCredential(authCredential).await().user
                                user?.let {
                                    if (it.isAnonymous.not()) {
                                        saveUserToFirestore(it)
                                        login.invoke()
                                    }
                                }
                            }
                        }
                    }
                } catch (_: NoCredentialException) {
                    launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }

        // Gets the intent to launch the Google Sign In
        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        // Gets the options for the credential manager
        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setNonce(getNonce())
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        }

        private fun getNonce(): String? {
            return UUID.randomUUID().toString()
        }

        /**
         * Saves information of Firebase Auth user in Firestore
         */
        private suspend fun saveUserToFirestore(firebaseUser: FirebaseUser) {
            try {
                val userRef = Firebase.firestore.collection("users").document(firebaseUser.uid)

                // Verify if user exists
                val existingUser = userRef.get().await()

                if (existingUser.exists()) {
                    // Update last login
                    userRef.update(
                        "lastLogin", System.currentTimeMillis(),
                        "photoUrl", firebaseUser.photoUrl?.toString(),
                        "displayName", firebaseUser.displayName
                    ).await()
                } else {
                    // New user
                    val user = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        displayName = firebaseUser.displayName,
                        photoUrl = firebaseUser.photoUrl?.toString(),
                        isAnonymous = firebaseUser.isAnonymous,
                        createdAt = System.currentTimeMillis(),
                        lastLogin = System.currentTimeMillis()
                    )
                    userRef.set(user).await()
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "GoogleSignInUtils",
                    "Error al guardar usuario en Firestore: ${e.message}",
                )
            }
        }
    }
}