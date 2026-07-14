package dev.stefano.enuventory.ui.util

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val RETURN_ESTIMATE_PATTERN = "dd MMM yyyy, HH:mm"
private const val DEFAULT_DEADLINE_THRESHOLD_HOURS = 24L

/**
 * Filter [BorrowRecord] yang statusnya masih Borrowed dan batas kembalinya sudah lewat atau
 * tinggal [thresholdHours] jam lagi -- dipakai buat notifikasi in-app "batas pengembalian
 * segera" di Home (User). [BorrowRecord.returnEstimate] disimpan sebagai String berformat
 * "dd MMM yyyy, HH:mm" (lihat BorrowRepositoryImpl), jadi perlu di-parse dulu.
 */
fun List<BorrowRecord>.nearDeadlineRecords(
    thresholdHours: Long = DEFAULT_DEADLINE_THRESHOLD_HOURS,
    now: Date = Date()
): List<BorrowRecord> {
    val thresholdMillis = thresholdHours * 60 * 60 * 1000
    return filter { record ->
        record.status == BorrowStatus.Borrowed &&
                parseReturnEstimate(record.returnEstimate)?.let { deadline ->
                    deadline.time - now.time <= thresholdMillis
                } == true
    }
}

/** Pesan singkat buat notifikasi, membedakan yang udah lewat batas vs yang masih mendekati. */
fun BorrowRecord.deadlineMessage(now: Date = Date()): String {
    val deadline = parseReturnEstimate(returnEstimate) ?: return "Batas kembali: $returnEstimate"
    return if (deadline.before(now)) {
        "Sudah lewat batas kembali ($returnEstimate)"
    } else {
        "Batas kembali: $returnEstimate"
    }
}

private fun parseReturnEstimate(value: String): Date? = try {
    SimpleDateFormat(RETURN_ESTIMATE_PATTERN, Locale.getDefault()).parse(value)
} catch (e: Exception) {
    null
}
