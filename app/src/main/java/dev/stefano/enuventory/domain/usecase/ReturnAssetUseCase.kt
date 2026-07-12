package dev.stefano.enuventory.domain.usecase

import dev.stefano.enuventory.domain.repository.BorrowRepository
import javax.inject.Inject

/** UseCase untuk menandai asset dikembalikan. Dipakai di Pengembalian/Detail Riwayat page. */
class ReturnAssetUseCase @Inject constructor(
    private val borrowRepository: BorrowRepository
) {
    suspend operator fun invoke(recordId: String, proofImageUrl: String? = null) =
        borrowRepository.returnAsset(recordId, proofImageUrl)
}
