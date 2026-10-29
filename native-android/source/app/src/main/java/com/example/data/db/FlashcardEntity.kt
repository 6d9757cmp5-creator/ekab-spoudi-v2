package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val term: String,
    val definition: String,
    val category: String, // e.g. "Φαρμακολογία", "Ιατρική Ορολογία", "Τραύμα & ALS", "Ανατομία"
    val hint: String = "",
    val masteryLevel: Int = 0, // 0: Νέα, 1: Σε εξάσκηση, 2: Κατακτήθηκε
    val timesReviewed: Int = 0,
    val lastReviewedTimestamp: Long = 0L,
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
