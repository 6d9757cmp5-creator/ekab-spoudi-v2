package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.EkabDatabase
import com.example.data.db.FlashcardEntity
import com.example.data.db.FlashcardRepository
import com.example.data.model.*
import com.example.data.repository.EkabRepository
import com.example.mentor.models.*
import com.example.mentor.repository.MentorRepository
import com.example.mentor.screens.MentorSubModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    EOPPEP,
    SAEK,
    PHARMA,
    SEMINARS,
    PRACTICAL,
    LAW,
    CONTACTS,
    QUIZ,
    COURSE_DETAIL,
    FLASHCARDS,
    MENTOR
}

enum class EoppepFilter {
    ALL,
    GROUP_A,
    GROUP_B,
    SOS,
    BOOKMARKED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EkabRepository(application)
    private val flashcardRepository = FlashcardRepository(EkabDatabase.getInstance(application).flashcardDao())
    private val mentorRepository = MentorRepository(application)

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<AppScreen>()

    // MENTOR OS State
    private val _mentorSubModule = MutableStateFlow(MentorSubModule.HUB)
    val mentorSubModule: StateFlow<MentorSubModule> = _mentorSubModule.asStateFlow()

    val mentorSources = mentorRepository.sources
    val mentorMissions = mentorRepository.missions
    val mentorMci = mentorRepository.mciIncidents
    val mentorEquipment = mentorRepository.equipmentList
    val mentorEcg = mentorRepository.ecgRhythms
    val mentorFaculty = mentorRepository.faculty
    val mentorOrgans = mentorRepository.organs
    val mentorAudit = mentorRepository.auditItems

    // Questions State & Filters
    private val _eoppepFilter = MutableStateFlow(EoppepFilter.ALL)
    val eoppepFilter: StateFlow<EoppepFilter> = _eoppepFilter.asStateFlow()

    private val _eoppepSearch = MutableStateFlow("")
    val eoppepSearch: StateFlow<String> = _eoppepSearch.asStateFlow()

    // SAEK State & Filters
    private val _saekSemesterFilter = MutableStateFlow("Όλα")
    val saekSemesterFilter: StateFlow<String> = _saekSemesterFilter.asStateFlow()

    private val _selectedCourse = MutableStateFlow<SaekCourse?>(null)
    val selectedCourse: StateFlow<SaekCourse?> = _selectedCourse.asStateFlow()

    // Pharma State & Filters
    private val _pharmaCategoryFilter = MutableStateFlow("Όλα")
    val pharmaCategoryFilter: StateFlow<String> = _pharmaCategoryFilter.asStateFlow()

    private val _pharmaSearch = MutableStateFlow("")
    val pharmaSearch: StateFlow<String> = _pharmaSearch.asStateFlow()

    private val _selectedMedication = MutableStateFlow<PharmacologyItem?>(null)
    val selectedMedication: StateFlow<PharmacologyItem?> = _selectedMedication.asStateFlow()

    // Seminars State
    private val _selectedSeminarId = MutableStateFlow("bls")
    val selectedSeminarId: StateFlow<String> = _selectedSeminarId.asStateFlow()

    // Law State & Search
    private val _lawSearch = MutableStateFlow("")
    val lawSearch: StateFlow<String> = _lawSearch.asStateFlow()

    // Quiz State
    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished: StateFlow<Boolean> = _quizFinished.asStateFlow()

    // Flashcards State (Room DB)
    val flashcards: StateFlow<List<FlashcardEntity>> = flashcardRepository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _flashcardCategoryFilter = MutableStateFlow("Όλα")
    val flashcardCategoryFilter: StateFlow<String> = _flashcardCategoryFilter.asStateFlow()

    private val _flashcardSearch = MutableStateFlow("")
    val flashcardSearch: StateFlow<String> = _flashcardSearch.asStateFlow()

    private val _activeFlashcardIndex = MutableStateFlow(0)
    val activeFlashcardIndex: StateFlow<Int> = _activeFlashcardIndex.asStateFlow()

    private val _isCardFlipped = MutableStateFlow(false)
    val isCardFlipped: StateFlow<Boolean> = _isCardFlipped.asStateFlow()

    private val _flashcardsMode = MutableStateFlow("review") // "review" or "manage"
    val flashcardsMode: StateFlow<String> = _flashcardsMode.asStateFlow()

    val filteredFlashcards: StateFlow<List<FlashcardEntity>> = combine(
        flashcards,
        _flashcardCategoryFilter,
        _flashcardSearch
    ) { list, category, query ->
        val qTrim = query.trim().lowercase()
        list.filter { item ->
            val matchesCategory = when (category) {
                "Όλα" -> true
                "Αγαπημένα ⭐" -> item.isStarred
                else -> item.category == category
            }
            val matchesQuery = if (qTrim.isEmpty()) true else {
                item.term.lowercase().contains(qTrim) ||
                        item.definition.lowercase().contains(qTrim) ||
                        item.hint.lowercase().contains(qTrim)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions = repository.questions
    val medications = repository.medications
    val seminars = repository.seminars
    val saekCourses = repository.saekCourses
    val professionActs = repository.professionActs
    val practicalSections = repository.practicalSections
    val contacts = repository.contacts
    val progress = repository.progress

    // Filtered Questions
    val filteredQuestions: StateFlow<List<EoppepQuestion>> = combine(
        questions,
        _eoppepFilter,
        _eoppepSearch,
        progress
    ) { list, filter, query, prog ->
        val qTrim = query.trim().lowercase()
        list.filter { item ->
            val matchesFilter = when (filter) {
                EoppepFilter.ALL -> true
                EoppepFilter.GROUP_A -> item.group.equals("A", ignoreCase = true)
                EoppepFilter.GROUP_B -> item.group.equals("B", ignoreCase = true)
                EoppepFilter.SOS -> item.isSos
                EoppepFilter.BOOKMARKED -> prog.bookmarkedQuestions.contains(item.id)
            }
            val matchesQuery = if (qTrim.isEmpty()) true else {
                item.question.lowercase().contains(qTrim) ||
                        item.answer.lowercase().contains(qTrim) ||
                        item.topic.lowercase().contains(qTrim) ||
                        item.number.toString() == qTrim
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Medications
    val filteredMedications: StateFlow<List<PharmacologyItem>> = combine(
        medications,
        _pharmaCategoryFilter,
        _pharmaSearch,
        progress
    ) { list, category, query, _ ->
        val qTrim = query.trim().lowercase()
        list.filter { item ->
            val matchesCategory = if (category == "Όλα") true else item.category == category
            val matchesQuery = if (qTrim.isEmpty()) true else {
                item.name.lowercase().contains(qTrim) ||
                        item.english.lowercase().contains(qTrim) ||
                        item.indication.lowercase().contains(qTrim) ||
                        item.mechanism.lowercase().contains(qTrim)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered SAEK Courses
    val filteredSaekCourses: StateFlow<List<SaekCourse>> = combine(
        saekCourses,
        _saekSemesterFilter
    ) { list, sem ->
        if (sem == "Όλα") list else list.filter { it.semester.contains(sem) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Profession Acts
    val filteredProfessionActs: StateFlow<List<ProfessionAct>> = combine(
        professionActs,
        _lawSearch
    ) { list, query ->
        val qTrim = query.trim().lowercase()
        if (qTrim.isEmpty()) list else list.filter {
            it.title.lowercase().contains(qTrim) ||
                    it.terms.lowercase().contains(qTrim) ||
                    it.summary.lowercase().contains(qTrim) ||
                    it.article.lowercase().contains(qTrim)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initialize()
            flashcardRepository.checkAndSeedIfNeeded()
            mentorRepository.initialize()
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_currentScreen.value == AppScreen.MENTOR && _mentorSubModule.value != MentorSubModule.HUB) {
            _mentorSubModule.value = MentorSubModule.HUB
            return true
        }
        return if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
            true
        } else if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
            true
        } else {
            false
        }
    }

    fun setMentorSubModule(subModule: MentorSubModule) {
        _mentorSubModule.value = subModule
    }

    fun setEoppepFilter(filter: EoppepFilter) {
        _eoppepFilter.value = filter
    }

    fun setEoppepSearch(query: String) {
        _eoppepSearch.value = query
    }

    fun setSaekSemesterFilter(sem: String) {
        _saekSemesterFilter.value = sem
    }

    fun selectCourse(course: SaekCourse) {
        _selectedCourse.value = course
        navigateTo(AppScreen.COURSE_DETAIL)
    }

    fun setPharmaCategoryFilter(cat: String) {
        _pharmaCategoryFilter.value = cat
    }

    fun setPharmaSearch(query: String) {
        _pharmaSearch.value = query
    }

    fun selectMedication(med: PharmacologyItem?) {
        _selectedMedication.value = med
    }

    fun selectSeminar(trackId: String) {
        _selectedSeminarId.value = trackId
    }

    fun setLawSearch(query: String) {
        _lawSearch.value = query
    }

    fun toggleQuestionBookmark(id: String) {
        repository.toggleQuestionBookmark(id)
    }

    fun toggleQuestionCompleted(id: String) {
        repository.toggleQuestionCompleted(id)
    }

    fun toggleCourseCompleted(id: String) {
        repository.toggleCourseCompleted(id)
    }

    fun toggleMedFavorite(id: String) {
        repository.toggleMedFavorite(id)
    }

    // Flashcard Actions (Room Database)
    fun setFlashcardCategoryFilter(category: String) {
        _flashcardCategoryFilter.value = category
        _activeFlashcardIndex.value = 0
        _isCardFlipped.value = false
    }

    fun setFlashcardSearch(query: String) {
        _flashcardSearch.value = query
        _activeFlashcardIndex.value = 0
        _isCardFlipped.value = false
    }

    fun setFlashcardsMode(mode: String) {
        _flashcardsMode.value = mode
    }

    fun flipCard() {
        _isCardFlipped.value = !_isCardFlipped.value
    }

    fun nextFlashcard() {
        val total = filteredFlashcards.value.size
        if (total > 0) {
            _activeFlashcardIndex.value = (_activeFlashcardIndex.value + 1) % total
            _isCardFlipped.value = false
        }
    }

    fun prevFlashcard() {
        val total = filteredFlashcards.value.size
        if (total > 0) {
            _activeFlashcardIndex.value = if (_activeFlashcardIndex.value > 0) _activeFlashcardIndex.value - 1 else total - 1
            _isCardFlipped.value = false
        }
    }

    fun recordFlashcardReview(id: Long, mastered: Boolean) {
        viewModelScope.launch {
            flashcardRepository.recordReview(id, mastered)
            nextFlashcard()
        }
    }

    fun toggleFlashcardStar(id: Long) {
        viewModelScope.launch {
            flashcardRepository.toggleStar(id)
        }
    }

    fun createFlashcard(term: String, definition: String, category: String, hint: String) {
        viewModelScope.launch {
            flashcardRepository.insert(
                FlashcardEntity(
                    term = term.trim(),
                    definition = definition.trim(),
                    category = category.trim(),
                    hint = hint.trim()
                )
            )
        }
    }

    fun deleteFlashcard(id: Long) {
        viewModelScope.launch {
            flashcardRepository.deleteById(id)
            val currentIdx = _activeFlashcardIndex.value
            val size = filteredFlashcards.value.size - 1
            if (currentIdx >= size && size >= 0) {
                _activeFlashcardIndex.value = maxOf(0, size - 1)
            }
        }
    }

    // Quiz Generation
    fun startQuiz(count: Int = 10, group: String? = null) {
        val allQuestions = questions.value.ifEmpty { return }
        val pool = if (group != null) {
            allQuestions.filter { it.group.equals(group, ignoreCase = true) }
        } else {
            allQuestions
        }.shuffled().take(count)

        val quizList = pool.map { currentQ ->
            val distractors = allQuestions
                .filter { it.id != currentQ.id }
                .shuffled()
                .take(3)
                .map { distractorQ ->
                    distractorQ.answer.split(". ").take(2).joinToString(". ").take(180) + "..."
                }
            val correctAnswer = currentQ.answer.split(". ").take(2).joinToString(". ").take(180) + "..."
            val options = (distractors + correctAnswer).shuffled()
            val correctIdx = options.indexOf(correctAnswer)
            QuizQuestion(
                question = currentQ,
                options = options,
                correctIndex = if (correctIdx >= 0) correctIdx else 0
            )
        }

        _quizQuestions.value = quizList
        _currentQuizIndex.value = 0
        _quizFinished.value = false
        navigateTo(AppScreen.QUIZ)
    }

    fun answerQuizQuestion(selectedIdx: Int) {
        val list = _quizQuestions.value.toMutableList()
        val currIdx = _currentQuizIndex.value
        if (currIdx in list.indices) {
            list[currIdx] = list[currIdx].copy(selectedIndex = selectedIdx)
            _quizQuestions.value = list
        }
    }

    fun nextQuizQuestion() {
        if (_currentQuizIndex.value < _quizQuestions.value.size - 1) {
            _currentQuizIndex.value = _currentQuizIndex.value + 1
        } else {
            finishQuiz()
        }
    }

    fun prevQuizQuestion() {
        if (_currentQuizIndex.value > 0) {
            _currentQuizIndex.value = _currentQuizIndex.value - 1
        }
    }

    fun finishQuiz() {
        _quizFinished.value = true
        val total = _quizQuestions.value.size
        val score = _quizQuestions.value.count { it.selectedIndex == it.correctIndex }
        repository.recordQuizResult(score, total)
    }
}
