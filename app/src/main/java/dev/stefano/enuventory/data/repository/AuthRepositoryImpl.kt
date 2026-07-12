package dev.stefano.enuventory.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.stefano.enuventory.domain.model.User
import dev.stefano.enuventory.domain.model.UserRole
import dev.stefano.enuventory.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val mockUser = kotlinx.coroutines.flow.MutableStateFlow<User?>(null)

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        // Listener yang otomatis emit tiap kali auth state berubah (login/logout)
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(mockUser.value)
            } else {
                // Ambil role dari Firestore collection "users"
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val roleString = doc.getString("role") ?: "user"
                        val role = if (roleString == "admin") UserRole.Admin else UserRole.RegularUser
                        trySend(
                            User(
                                uid = firebaseUser.uid,
                                name = doc.getString("name") ?: firebaseUser.displayName ?: "",
                                email = firebaseUser.email ?: "",
                                role = role
                            )
                        )
                    }
                    .addOnFailureListener {
                        // Fallback: user ada tapi data Firestore gagal diambil
                        trySend(
                            User(
                                uid = firebaseUser.uid,
                                name = firebaseUser.displayName ?: "",
                                email = firebaseUser.email ?: "",
                                role = UserRole.RegularUser
                            )
                        )
                    }
            }
        }

        // Coroutine to collect mockUser flow and emit when mockUser changes and firebaseUser is null
        val job = launch {
            mockUser.collect { user ->
                if (firebaseAuth.currentUser == null) {
                    trySend(user)
                }
            }
        }

        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
            job.cancel()
        }
    }

    override suspend fun signIn(email: String, password: String) {
        if (email.endsWith("@enuventory.com")) {
            val role = if (email.startsWith("admin")) UserRole.Admin else UserRole.RegularUser
            mockUser.value = User(
                uid = "demo-uid",
                name = if (role == UserRole.Admin) "Demo Admin" else "Demo User",
                email = email,
                role = role
            )
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }
    }

    override suspend fun signOut() {
        mockUser.value = null
        firebaseAuth.signOut()
    }
}
