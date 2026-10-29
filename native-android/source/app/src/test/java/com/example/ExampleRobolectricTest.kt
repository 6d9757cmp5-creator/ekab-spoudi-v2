package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.EkabDatabase
import com.example.data.db.FlashcardEntity
import com.example.data.repository.EkabRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: EkabDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, EkabDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ΕΚΑΒ Σπουδή", appName)
    }

    @Test
    fun `repository loads questions from assets`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = EkabRepository(context)
        repository.initialize()
        val questions = repository.questions.value
        assertTrue("Expected 314 questions", questions.isNotEmpty())
        assertEquals(314, questions.size)
    }

    @Test
    fun `room flashcard insert and query`() = runBlocking {
        val dao = db.flashcardDao()
        val card = FlashcardEntity(
            term = "Αδρεναλίνη",
            definition = "1 mg IV/IO σε ανακοπή",
            category = "Φαρμακολογία"
        )
        val id = dao.insertFlashcard(card)
        assertTrue(id > 0)

        val list = dao.getAllFlashcards().first()
        assertEquals(1, list.size)
        assertEquals("Αδρεναλίνη", list[0].term)
    }

    @Test
    fun `room flashcard toggle star and review`() = runBlocking {
        val dao = db.flashcardDao()
        val card = FlashcardEntity(
            term = "GCS",
            definition = "Κλίμακα Γλασκόβης 3-15",
            category = "Ιατρική Ορολογία"
        )
        val id = dao.insertFlashcard(card)
        dao.toggleStar(id)
        dao.recordReview(id, 2, System.currentTimeMillis())

        val list = dao.getAllFlashcards().first()
        assertEquals(true, list[0].isStarred)
        assertEquals(2, list[0].masteryLevel)
        assertEquals(1, list[0].timesReviewed)
    }

    @Test
    fun `mentor repository loads PR 83 library sources and faculty`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mentorRepo = com.example.mentor.repository.MentorRepository(context)
        mentorRepo.initialize()

        val sources = mentorRepo.sources.value
        assertEquals(15, sources.size)

        val fullTextSources = sources.filter { it.verifiedStatus.contains("Πλήρες Κείμενο") }
        assertEquals(7, fullTextSources.size)

        val faculty = mentorRepo.faculty.value
        assertEquals(5, faculty.size)

        val missions = mentorRepo.missions.value
        assertTrue(missions.isNotEmpty())

        val ecg = mentorRepo.ecgRhythms.value
        assertEquals(6, ecg.size)

        val mci = mentorRepo.mciIncidents.value
        assertEquals(2, mci.size)

        val audit = mentorRepo.auditItems.value
        assertEquals(8, audit.size)
    }
}
