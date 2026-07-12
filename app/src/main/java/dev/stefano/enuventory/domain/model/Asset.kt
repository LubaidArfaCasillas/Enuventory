package dev.stefano.enuventory.domain.model

/**
 * Domain entity untuk sebuah asset/alat yang ada di inventori.
 *
 * Ini adalah representasi "murni" dari data bisnis — tidak ada
 * dependency ke Android framework, Room entity, maupun Firestore DTO.
 * Mapping dari/ke layer lain dilakukan di masing-masing layer.
 */
data class Asset(
    val id: String,
    val title: String,
    val stock: Int,
    val status: AssetStatus,
    val category: String,
    val description: String,
    val imageUrl: String? = null
)
