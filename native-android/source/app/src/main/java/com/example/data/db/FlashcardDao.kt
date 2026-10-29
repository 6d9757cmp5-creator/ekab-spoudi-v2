package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcards ORDER BY isStarred DESC, id DESC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE category = :category ORDER BY isStarred DESC, id DESC")
    fun getFlashcardsByCategory(category: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE isStarred = 1 ORDER BY id DESC")
    fun getStarredFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(flashcards: List<FlashcardEntity>)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE flashcards SET isStarred = NOT isStarred WHERE id = :id")
    suspend fun toggleStar(id: Long)

    @Query("UPDATE flashcards SET masteryLevel = :masteryLevel, timesReviewed = timesReviewed + 1, lastReviewedTimestamp = :timestamp WHERE id = :id")
    suspend fun recordReview(id: Long, masteryLevel: Int, timestamp: Long)
}
