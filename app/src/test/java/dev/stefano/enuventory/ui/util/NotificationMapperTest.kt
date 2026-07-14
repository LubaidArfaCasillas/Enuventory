package dev.stefano.enuventory.ui.util

import dev.stefano.enuventory.domain.model.BorrowRecord
import dev.stefano.enuventory.domain.model.BorrowStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Menguji [nearDeadlineRecords] & [deadlineMessage]: deteksi batas kembali yang mendekat/lewat. */
class NotificationMapperTest {

    private val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    private fun record(
        id: String,
        status: BorrowStatus,
        returnEstimate: Date
    ) = BorrowRecord(
        id = id,
        assetId = "HW-1",
        assetTitle = "Laptop",
        assetStock = 1,
        borrowerId = "u1",
        borrowerName = "Budi",
        status = status,
        borrowDate = "1 Jan 2026, 08:00",
        returnEstimate = formatter.format(returnEstimate)
    )

    @Test
    fun `nearDeadlineRecords includes a Borrowed record within the threshold`() {
        val now = Date()
        val soon = Date(now.time + 60 * 60 * 1000) // 1 jam lagi
        val records = listOf(record("r1", BorrowStatus.Borrowed, soon))

        val result = records.nearDeadlineRecords(thresholdHours = 24, now = now)

        assertEquals(1, result.size)
        assertEquals("r1", result.first().id)
    }

    @Test
    fun `nearDeadlineRecords includes an already overdue Borrowed record`() {
        val now = Date()
        val overdue = Date(now.time - 60 * 60 * 1000) // 1 jam lalu
        val records = listOf(record("r1", BorrowStatus.Borrowed, overdue))

        val result = records.nearDeadlineRecords(thresholdHours = 24, now = now)

        assertEquals(1, result.size)
    }

    @Test
    fun `nearDeadlineRecords excludes records far beyond the threshold`() {
        val now = Date()
        val far = Date(now.time + 10L * 24 * 60 * 60 * 1000) // 10 hari lagi
        val records = listOf(record("r1", BorrowStatus.Borrowed, far))

        val result = records.nearDeadlineRecords(thresholdHours = 24, now = now)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `nearDeadlineRecords excludes non-Borrowed records even if the date is close`() {
        val now = Date()
        val soon = Date(now.time + 60 * 60 * 1000)
        val records = listOf(
            record("r1", BorrowStatus.Pending, soon),
            record("r2", BorrowStatus.Completed, soon),
            record("r3", BorrowStatus.Rejected, soon)
        )

        val result = records.nearDeadlineRecords(thresholdHours = 24, now = now)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `deadlineMessage flags an overdue record differently from an upcoming one`() {
        val now = Date()
        val overdue = record("r1", BorrowStatus.Borrowed, Date(now.time - 60 * 60 * 1000))
        val upcoming = record("r2", BorrowStatus.Borrowed, Date(now.time + 60 * 60 * 1000))

        assertTrue(overdue.deadlineMessage(now).contains("lewat"))
        assertTrue(upcoming.deadlineMessage(now).contains("Batas kembali"))
    }
}
