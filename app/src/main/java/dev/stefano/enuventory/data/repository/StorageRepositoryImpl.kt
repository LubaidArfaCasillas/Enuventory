package dev.stefano.enuventory.data.repository

import com.google.firebase.storage.FirebaseStorage
import dev.stefano.enuventory.domain.repository.StorageRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
) : StorageRepository {

    override suspend fun uploadAssetImage(assetId: String, imageBytes: ByteArray): String {
        val ref = firebaseStorage.reference.child("assets/$assetId.jpg")
        ref.putBytes(imageBytes).await()
        return ref.downloadUrl.await().toString()
    }
}
