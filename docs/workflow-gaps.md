# Gap & Bug antara Diagram Alur MVP dan Implementasi Saat Ini

Ditemukan waktu cross-check diagram `ENUVENTORY WORKFLOW` (alur peminjaman & pengembalian)
terhadap kode yang ada, sebelum nulis unit test & checklist QA. Diurutkan dari yang paling
mempengaruhi kebenaran alur.

## 1. ~~Bug: request yang ditolak (Rejected) tampil seolah masih "Menunggu Persetujuan"~~ (FIXED)

`DetailRiwayatViewModel.kt` sebelumnya memetakan `BorrowStatus.Rejected` ke
`DetailRiwayatState.MenungguPersetujuan` — sama persis dengan `Pending`, kemungkinan besar
copy-paste bug. Efeknya: user yang requestnya ditolak admin akan melihat halaman riwayat yang
bilang "Menunggu Persetujuan", padahal requestnya sudah selesai/ditolak.

**Sudah diperbaiki**: ditambah `DetailRiwayatState.Ditolak` (`ui/pages/DetailRiwayatPage.kt`)
dengan timeline "Diajukan" → "Ditolak" (pakai `EnuTimelineNodeStatus.Rejected` &
`EnuBorrowStatus.Ditolak` yang ternyata sudah lebih dulu ada di komponen UI, cuma belum dipakai
di halaman ini), dan mapping di `DetailRiwayatViewModel` sudah diarahkan ke situ.
`DetailRiwayatViewModelTest.\`Rejected status shows Ditolak\`` sudah diupdate mengikuti.

## 2. Step "Scan QR di produk" (diagram) tidak wired ke apapun

Tiga bukti kongkret bahwa step ini di diagram cuma ada di UI, belum ada logic-nya:

- `BorrowStatus` (`domain/model/BorrowStatus.kt`) cuma punya 4 nilai: `Pending, Borrowed,
  Rejected, Completed`. Tidak ada status "disetujui admin tapi belum diambil user" — begitu
  admin approve (`BorrowRepositoryImpl.approveRequest`, dipanggil dari
  `DetailRequestViewModel.approveRequest`), status langsung lompat ke `Borrowed`.
- `DetailRiwayatState.MenungguPengambilan` (`ui/pages/DetailRiwayatPage.kt:43`) — state yang
  nampilin tombol **"Scan QR"** (`DetailRiwayatPage.kt:193-198`) — **tidak pernah diproduksi**
  oleh `DetailRiwayatViewModel`, karena tidak ada `BorrowStatus` yang dipetakan ke situ. Tombol
  "Scan QR" secara efektif tidak pernah muncul di flow normal.
- `ScanQRPage` (`ui/pages/ScanQRPage.kt`) sendiri murni mock: tap kotak scan → set
  `showSuccessDialog = true` (baris ~185), tap "Ya" → `onKonfirmasiYaClick()` yang di
  `EnuNavGraph.kt` cuma manggil `navController.popBackStack()`. Tidak ada kamera/QR scanning
  library, tidak ada use case yang dipanggil, tidak ada perubahan Firestore.
- `Asset` (`domain/model/Asset.kt`) juga belum punya field kode QR/identifier fisik yang bisa
  divalidasi terhadap hasil scan.

**Saran** (kalau mau benar-benar mengimplementasikan step ini sesuai diagram):

1. Tambah status baru di `BorrowStatus`, misal `Approved` (disetujui, menunggu diambil),
   dan ubah `approveRequest` supaya set ke `Approved`, bukan langsung `Borrowed`.
2. Petakan `Approved` → `DetailRiwayatState.MenungguPengambilan` di `DetailRiwayatViewModel`.
3. Tambah use case baru (mis. `ConfirmPickupUseCase`) yang dipanggil dari `ScanQRPage` setelah
   scan sukses, mengubah status `Approved` → `Borrowed`.
4. Kalau butuh validasi QR fisik: tambah field `qrCode`/`id` yang di-embed sebagai QR code di
   tiap asset, dan cocokkan hasil scan terhadap `record.assetId` sebelum confirm.
5. Kalau ternyata step scan-QR ini **tidak** krusial buat MVP (mis. cukup percaya admin sudah
   approve = barang siap diambil tanpa perlu scan), opsi lebih murah: hapus step ini dari
   diagram & `ScanQRPage`/route terkait, supaya diagram & kode tetap sinkron.

## 3. "Admin dapat notifikasi" belum ada push notification asli

Tidak ada FCM (`firebase-messaging`) atau `NotificationManager` di manapun di kodebase — sudah
dicek lewat grep menyeluruh. Yang ada cuma realtime Firestore listener
(`BorrowRepositoryImpl.getPendingRequests()`, dipakai `ApprovalViewModel`) yang meng-update UI
kalau halaman Approval sedang kebuka. Kalau app admin di-background/ditutup, admin **tidak
dapat notifikasi apapun** soal request baru.

**Saran:** tambah Firebase Cloud Messaging — perlu Cloud Function (atau backend lain) yang
trigger on-create di collection `borrows` dengan `status: Pending`, kirim push ke device admin.
Di luar scope client-only Android app ini kalau belum ada Cloud Functions project-nya.

## 4. "Upload bukti foto" masih dummy, belum upload asli

`ReturnAssetViewModel.submitReturn()` (`ui/screen/history/ReturnAssetViewModel.kt:60-70`):

```kotlin
// Return via ReturnAssetUseCase using a dummy proof image for MVP
val dummyProofUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500"
returnAssetUseCase(recordId, dummyProofUrl)
```

Foto yang "di-capture" user di `UploadBuktiFotoPage` tidak pernah benar-benar disimpan/di-upload
— `onCapturePhoto()` di ViewModel cuma ganti UI state ke `PreviewImage`, tidak ada file/bitmap
yang disimpan. `submitReturn()` selalu mengirim URL placeholder yang sama, terlepas dari apa yang
di-capture.

**Update**: `domain/repository/StorageRepository.kt` + `data/repository/StorageRepositoryImpl.kt`
sekarang sudah ada dan dipakai beneran untuk upload **foto asset** (fitur "Tambah Asset", lihat
`TambahAssetViewModel.addAsset()` + `UploadAssetImageUseCase`) — jadi klaim "belum dipakai sama
sekali" di atas sudah gak akurat untuk kasus itu. Tapi alur **pengembalian** ini masih belum
disambungkan ke repository yang sama; masih pakai `dummyProofUrl` di atas.

Catatan: backend upload-nya **bukan Firebase Storage**, tapi **Supabase Storage**
(`di/SupabaseModule.kt`, bucket `Enuventory`) — Firebase Storage butuh upgrade ke Blaze plan
(gak bisa lagi Spark plan sejak kebijakan Google Okt 2024), jadi dipindah ke Supabase yang free
tier-nya gak perlu billing sama sekali. Perlu `SUPABASE_URL`/`SUPABASE_ANON_KEY` di
`local.properties` (gitignored) buat provider ini jalan; lihat `SupabaseModule.kt`.

**Saran:** implementasikan capture foto asli (CameraX/`ActivityResultContracts.TakePicture`) di
`UploadBuktiFotoPage`/`ReturnAssetViewModel`, lalu panggil `UploadAssetImageUseCase` yang sudah
ada (atau bikin use case sejenis, mis. `UploadReturnProofUseCase`, kalau mau bucket/path Storage
yang beda dari foto asset) buat upload beneran, dan pakai URL hasilnya di
`returnAssetUseCase(recordId, uploadedUrl)`.

## 5. ~~Bug: ID asset bisa collision & diam-diam overwrite asset lain~~ (FIXED)

Ketemu waktu mendesain format QR code untuk asset (QR bakal encode `assetId`, jadi ID-nya
harus benar-benar unik). `TambahAssetViewModel.kt` sebelumnya generate ID lewat:

```kotlin
val assetId = "HW-${(System.currentTimeMillis() % 100000).toString().padStart(5, '0')}"
```

Collision-nya cuma soal waktu — pola berulang tiap 100 detik (`% 100000` ms). Parahnya,
`AssetRepositoryImpl.addAsset()` pakai `.document(asset.id).set(...)` (bukan `.add()`), jadi
kalau ID bentrok, asset lama **ke-overwrite diam-diam** tanpa error/warning apapun.

**Sudah diperbaiki**: ID sekarang di-generate lewat `domain/util/AssetIdGenerator.kt` — prefix
`HW-` + 5 karakter acak dari alfabet aman-dibaca (tanpa 0/O/1/I/L/U, gaya Crockford base32,
~24 juta kombinasi). `TambahAssetViewModel.generateUniqueAssetId()` juga sekarang cek dulu ke
`GetAssetByIdUseCase` sebelum pakai ID hasil generate, retry sampai 5x kalau ternyata sudah
dipakai, baru gagal dengan error state yang jelas kalau semua percobaan collision (kemungkinan
ini sangat kecil, tapi sekarang di-cover eksplisit, bukan cuma diperkecil probabilitasnya).
Dites di `AssetIdGeneratorTest.kt` & `TambahAssetViewModelTest.kt`.

## 6. Tidak ada validasi stock/concurrency saat approve

Belum dikonfirmasi lewat testing manual (ada di checklist QA), tapi dari membaca
`BorrowRepositoryImpl.approveRequest()` — tidak ada pengecekan sisa stock asset sebelum
approve. Kalau stock asset = 1 dan ada 2 request Pending untuk asset yang sama, admin bisa
approve keduanya dan dua-duanya jadi `Borrowed` meski cuma ada 1 barang fisik. Perlu
diverifikasi manual dulu (lihat checklist QA bagian "Stock/concurrency") sebelum diputuskan
perlu fix atau tidak — kemungkinan bukan prioritas kalau volume pemakaian rendah & admin
mengawasi manual.

## Catatan minor: `Log.d`/`Log.e` langsung di `DetailAssetUserViewModel`

`requestBorrow()` (`ui/screen/asset/DetailAssetUserViewModel.kt:77-104`) manggil
`android.util.Log.d/e` langsung berkali-kali — ini debug logging yang kelihatannya tertinggal
dari sesi debugging bug sebelumnya (lihat commit history soal "blank action saat submit borrow").
Efeknya cuma noise di Logcat produksi + bikin unit test butuh
`testOptions.unitTests.isReturnDefaultValues = true` di `app/build.gradle.kts` biar gak crash
"not mocked". Bukan bug fungsional, tapi worth dibersihkan kalau sempat.
