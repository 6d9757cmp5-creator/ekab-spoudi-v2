package com.example.data.repository

import android.content.Context
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader

class EkabRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("ekab_study_prefs", Context.MODE_PRIVATE)

    private val _questions = MutableStateFlow<List<EoppepQuestion>>(emptyList())
    val questions: StateFlow<List<EoppepQuestion>> = _questions.asStateFlow()

    private val _medications = MutableStateFlow<List<PharmacologyItem>>(emptyList())
    val medications: StateFlow<List<PharmacologyItem>> = _medications.asStateFlow()

    private val _seminars = MutableStateFlow<List<SeminarTrack>>(emptyList())
    val seminars: StateFlow<List<SeminarTrack>> = _seminars.asStateFlow()

    private val _saekCourses = MutableStateFlow<List<SaekCourse>>(emptyList())
    val saekCourses: StateFlow<List<SaekCourse>> = _saekCourses.asStateFlow()

    private val _professionActs = MutableStateFlow<List<ProfessionAct>>(emptyList())
    val professionActs: StateFlow<List<ProfessionAct>> = _professionActs.asStateFlow()

    private val _practicalSections = MutableStateFlow<List<PracticalSection>>(emptyList())
    val practicalSections: StateFlow<List<PracticalSection>> = _practicalSections.asStateFlow()

    private val _contacts = MutableStateFlow<List<ContactCategory>>(emptyList())
    val contacts: StateFlow<List<ContactCategory>> = _contacts.asStateFlow()

    private val _progress = MutableStateFlow(loadProgress())
    val progress: StateFlow<StudyProgress> = _progress.asStateFlow()

    private var isLoaded = false

    suspend fun initialize() {
        if (isLoaded) return
        withContext(Dispatchers.IO) {
            loadQuestionsFromAssets()
            loadPharmacologyFromAssets()
            loadSeminarsFromAssets()
            loadSaekCoursesFromAssets()
            loadProfessionActsFromAssets()
            loadPracticalSectionsFromAssets()
            loadContactsFromAssets()
            isLoaded = true
        }
    }

    private fun loadProgress(): StudyProgress {
        val completed = prefs.getStringSet("completed_questions", emptySet()) ?: emptySet()
        val bookmarked = prefs.getStringSet("bookmarked_questions", emptySet()) ?: emptySet()
        val completedCourses = prefs.getStringSet("completed_courses", emptySet()) ?: emptySet()
        val favoriteMeds = prefs.getStringSet("favorite_meds", emptySet()) ?: emptySet()
        val tests = prefs.getInt("tests_taken", 0)
        val bestScore = prefs.getInt("best_score", 0)
        return StudyProgress(
            completedQuestions = completed.toSet(),
            bookmarkedQuestions = bookmarked.toSet(),
            completedCourses = completedCourses.toSet(),
            favoriteMeds = favoriteMeds.toSet(),
            testsTaken = tests,
            bestScore = bestScore
        )
    }

    fun toggleQuestionBookmark(questionId: String) {
        val current = _progress.value.bookmarkedQuestions.toMutableSet()
        if (current.contains(questionId)) {
            current.remove(questionId)
        } else {
            current.add(questionId)
        }
        prefs.edit().putStringSet("bookmarked_questions", current).apply()
        _progress.value = _progress.value.copy(bookmarkedQuestions = current)
    }

    fun toggleQuestionCompleted(questionId: String) {
        val current = _progress.value.completedQuestions.toMutableSet()
        if (current.contains(questionId)) {
            current.remove(questionId)
        } else {
            current.add(questionId)
        }
        prefs.edit().putStringSet("completed_questions", current).apply()
        _progress.value = _progress.value.copy(completedQuestions = current)
    }

    fun toggleCourseCompleted(courseId: String) {
        val current = _progress.value.completedCourses.toMutableSet()
        if (current.contains(courseId)) {
            current.remove(courseId)
        } else {
            current.add(courseId)
        }
        prefs.edit().putStringSet("completed_courses", current).apply()
        _progress.value = _progress.value.copy(completedCourses = current)
    }

    fun toggleMedFavorite(medId: String) {
        val current = _progress.value.favoriteMeds.toMutableSet()
        if (current.contains(medId)) {
            current.remove(medId)
        } else {
            current.add(medId)
        }
        prefs.edit().putStringSet("favorite_meds", current).apply()
        _progress.value = _progress.value.copy(favoriteMeds = current)
    }

    fun recordQuizResult(score: Int, total: Int) {
        val percent = if (total > 0) (score * 100) / total else 0
        val newCount = _progress.value.testsTaken + 1
        val newBest = maxOf(_progress.value.bestScore, percent)
        prefs.edit()
            .putInt("tests_taken", newCount)
            .putInt("best_score", newBest)
            .apply()
        _progress.value = _progress.value.copy(testsTaken = newCount, bestScore = newBest)
    }

    private fun loadQuestionsFromAssets() {
        try {
            val stream = context.assets.open("eoppep_questions.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val jsonArray = JSONArray(text)
            val list = mutableListOf<EoppepQuestion>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    EoppepQuestion(
                        id = obj.optString("id", "EOP-$i"),
                        number = obj.optInt("number", i + 1),
                        group = obj.optString("group", "A"),
                        page = obj.optInt("page", 0),
                        question = obj.optString("question", ""),
                        answer = obj.optString("answer", ""),
                        topic = obj.optString("topic", "Γενικά"),
                        isSos = obj.optBoolean("isSos", false)
                    )
                )
            }
            _questions.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPharmacologyFromAssets() {
        try {
            val stream = context.assets.open("pharmacology.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val root = JSONObject(text)
            val medsArray = root.optJSONArray("medicines") ?: JSONArray()
            val list = mutableListOf<PharmacologyItem>()
            for (i in 0 until medsArray.length()) {
                val obj = medsArray.getJSONObject(i)
                list.add(
                    PharmacologyItem(
                        id = obj.optString("id", "$i"),
                        category = obj.optString("category", "Επείγοντα"),
                        name = obj.optString("name", ""),
                        english = obj.optString("english", ""),
                        scope = obj.optString("scope", "standard"),
                        mechanism = obj.optString("mechanism", ""),
                        indication = obj.optString("indication", ""),
                        caution = obj.optString("caution", ""),
                        monitor = obj.optString("monitor", ""),
                        notes = obj.optString("notes", ""),
                        source = obj.optString("source", "ERC 2025")
                    )
                )
            }
            _medications.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSeminarsFromAssets() {
        try {
            val stream = context.assets.open("seminars.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val jsonArray = JSONArray(text)
            val list = mutableListOf<SeminarTrack>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val unitsArray = obj.optJSONArray("units") ?: JSONArray()
                val units = mutableListOf<SeminarUnit>()
                for (u in 0 until unitsArray.length()) {
                    val unitItems = unitsArray.optJSONArray(u)
                    if (unitItems != null && unitItems.length() > 0) {
                        val title = unitItems.optString(0, "Ενότητα ${u + 1}")
                        val points = mutableListOf<String>()
                        for (p in 1 until unitItems.length()) {
                            points.add(unitItems.optString(p))
                        }
                        units.add(SeminarUnit(title, points))
                    }
                }
                list.add(
                    SeminarTrack(
                        id = obj.optString("id", "track-$i"),
                        name = obj.optString("name", ""),
                        subtitle = obj.optString("subtitle", ""),
                        units = units
                    )
                )
            }
            _seminars.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadSaekCoursesFromAssets() {
        try {
            val stream = context.assets.open("saek_courses.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val jsonArray = JSONArray(text)
            val list = mutableListOf<SaekCourse>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val sectionsArray = obj.optJSONArray("sections") ?: JSONArray()
                val sections = mutableListOf<SaekSection>()
                for (s in 0 until sectionsArray.length()) {
                    val secObj = sectionsArray.getJSONObject(s)
                    sections.add(
                        SaekSection(
                            title = secObj.optString("title", ""),
                            content = secObj.optString("content", "")
                        )
                    )
                }
                list.add(
                    SaekCourse(
                        id = obj.optString("id", "saek-$i"),
                        number = obj.optInt("number", i + 1),
                        semester = obj.optString("semester", "Α' Εξάμηνο"),
                        title = obj.optString("title", ""),
                        sections = sections
                    )
                )
            }
            _saekCourses.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadProfessionActsFromAssets() {
        try {
            val stream = context.assets.open("profession_acts.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val jsonArray = JSONArray(text)
            val list = mutableListOf<ProfessionAct>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ProfessionAct(
                        id = obj.optString("id", "$i"),
                        title = obj.optString("title", ""),
                        terms = obj.optString("terms", ""),
                        article = obj.optString("article", ""),
                        status = obj.optString("status", ""),
                        summary = obj.optString("summary", ""),
                        limit = obj.optString("limit", "")
                    )
                )
            }
            _professionActs.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadPracticalSectionsFromAssets() {
        try {
            val stream = context.assets.open("practical_exam.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val jsonArray = JSONArray(text)
            val list = mutableListOf<PracticalSection>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val itemsArr = obj.optJSONArray("items") ?: JSONArray()
                val paragraphsArr = obj.optJSONArray("paragraphs") ?: JSONArray()
                val items = mutableListOf<String>()
                val paras = mutableListOf<String>()
                for (j in 0 until itemsArr.length()) items.add(itemsArr.optString(j))
                for (k in 0 until paragraphsArr.length()) paras.add(paragraphsArr.optString(k))
                list.add(
                    PracticalSection(
                        title = obj.optString("title", ""),
                        items = items,
                        paragraphs = paras
                    )
                )
            }
            _practicalSections.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadContactsFromAssets() {
        try {
            val stream = context.assets.open("contacts.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val jsonArray = JSONArray(text)
            val list = mutableListOf<ContactCategory>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val entriesArr = obj.optJSONArray("entries") ?: JSONArray()
                val entries = mutableListOf<ContactEntry>()
                for (j in 0 until entriesArr.length()) {
                    val entryObj = entriesArr.getJSONObject(j)
                    entries.add(
                        ContactEntry(
                            title = entryObj.optString("title", ""),
                            phone = entryObj.optString("phone", ""),
                            desc = entryObj.optString("desc", ""),
                            badge = entryObj.optString("badge", "")
                        )
                    )
                }
                list.add(
                    ContactCategory(
                        category = obj.optString("category", ""),
                        isEmergency = obj.optBoolean("isEmergency", false),
                        entries = entries
                    )
                )
            }
            _contacts.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
