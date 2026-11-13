package com.nivara.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser

    fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User UID is null")

            val userMap = hashMapOf(
                "uid" to uid,
                "email" to email,
                "role" to "CUSTOMER",
                "displayName" to name,
                "photoUrl" to "",
                "phoneNumber" to "",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            try {
                firestore.collection("users").document(uid).set(userMap).await()
            } catch (e: Exception) {
                firestore.collection("users").document(uid).set(userMap).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            syncUserProfileIfNeeded()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun fetchUserProfile(): Map<String, Any>? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.data
    }

    private suspend fun syncUserProfileIfNeeded() {
        val user = auth.currentUser ?: return
        val docRef = firestore.collection("users").document(user.uid)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            val fallbackMap = hashMapOf(
                "uid" to user.uid,
                "email" to (user.email ?: ""),
                "role" to "CUSTOMER",
                "displayName" to (user.displayName ?: "Nivara User"),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(fallbackMap).await()
        }
    }
}
