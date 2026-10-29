package com.example.data.model

data class EoppepQuestion(
    val id: String,
    val number: Int,
    val group: String,
    val page: Int,
    val question: String,
    val answer: String,
    val topic: String,
    val isSos: Boolean
)

data class PharmacologyItem(
    val id: String,
    val category: String,
    val name: String,
    val english: String,
    val scope: String,
    val mechanism: String,
    val indication: String,
    val caution: String,
    val monitor: String,
    val notes: String,
    val source: String
)

data class SeminarTrack(
    val id: String,
    val name: String,
    val subtitle: String,
    val units: List<SeminarUnit>
)

data class SeminarUnit(
    val title: String,
    val keyPoints: List<String>
)

data class SaekCourse(
    val id: String,
    val number: Int,
    val semester: String,
    val title: String,
    val sections: List<SaekSection>
)

data class SaekSection(
    val title: String,
    val content: String
)

data class ProfessionAct(
    val id: String,
    val title: String,
    val terms: String,
    val article: String,
    val status: String,
    val summary: String,
    val limit: String
)

data class ContactCategory(
    val category: String,
    val isEmergency: Boolean,
    val entries: List<ContactEntry>
)

data class ContactEntry(
    val title: String,
    val phone: String,
    val desc: String,
    val badge: String
)

data class PracticalSection(
    val title: String,
    val items: List<String>,
    val paragraphs: List<String>
)

data class StudyProgress(
    val completedQuestions: Set<String> = emptySet(),
    val bookmarkedQuestions: Set<String> = emptySet(),
    val completedCourses: Set<String> = emptySet(),
    val favoriteMeds: Set<String> = emptySet(),
    val testsTaken: Int = 0,
    val bestScore: Int = 0
)

data class QuizQuestion(
    val question: EoppepQuestion,
    val options: List<String>,
    val correctIndex: Int,
    var selectedIndex: Int? = null
)
