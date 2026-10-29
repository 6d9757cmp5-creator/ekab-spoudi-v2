package com.example.data.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FlashcardRepository(private val dao: FlashcardDao) {

    val allFlashcards: Flow<List<FlashcardEntity>> = dao.getAllFlashcards()

    val starredFlashcards: Flow<List<FlashcardEntity>> = dao.getStarredFlashcards()

    fun getByCategory(category: String): Flow<List<FlashcardEntity>> {
        return dao.getFlashcardsByCategory(category)
    }

    suspend fun checkAndSeedIfNeeded() = withContext(Dispatchers.IO) {
        if (dao.getCount() == 0) {
            EkabDatabase.prepopulateFlashcards(dao)
        }
    }

    suspend fun insert(flashcard: FlashcardEntity): Long = withContext(Dispatchers.IO) {
        dao.insertFlashcard(flashcard)
    }

    suspend fun update(flashcard: FlashcardEntity) = withContext(Dispatchers.IO) {
        dao.updateFlashcard(flashcard)
    }

    suspend fun delete(flashcard: FlashcardEntity) = withContext(Dispatchers.IO) {
        dao.deleteFlashcard(flashcard)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun toggleStar(id: Long) = withContext(Dispatchers.IO) {
        dao.toggleStar(id)
    }

    suspend fun recordReview(id: Long, mastered: Boolean) = withContext(Dispatchers.IO) {
        val level = if (mastered) 2 else 1
        dao.recordReview(id, level, System.currentTimeMillis())
    }
}
