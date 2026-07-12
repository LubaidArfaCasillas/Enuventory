package dev.stefano.enuventory.domain.model

/**
 * Status ketersediaan sebuah asset/alat di inventori.
 * Ini adalah domain enum — tidak bergantung pada layer UI manapun.
 */
enum class AssetStatus {
    Available,
    Unavailable,
    Maintenance
}
