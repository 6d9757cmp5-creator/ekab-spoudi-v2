package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.EkabBottomNav
import com.example.ui.components.EkabTopBar
import com.example.ui.screens.*
import com.example.mentor.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val progress by viewModel.progress.collectAsStateWithLifecycle()
                val questions by viewModel.filteredQuestions.collectAsStateWithLifecycle()
                val allQuestions by viewModel.questions.collectAsStateWithLifecycle()
                val courses by viewModel.filteredSaekCourses.collectAsStateWithLifecycle()
                val allCourses by viewModel.saekCourses.collectAsStateWithLifecycle()
                val medications by viewModel.filteredMedications.collectAsStateWithLifecycle()
                val allMedications by viewModel.medications.collectAsStateWithLifecycle()
                val seminars by viewModel.seminars.collectAsStateWithLifecycle()
                val professionActs by viewModel.filteredProfessionActs.collectAsStateWithLifecycle()
                val practicalSections by viewModel.practicalSections.collectAsStateWithLifecycle()
                val contacts by viewModel.contacts.collectAsStateWithLifecycle()

                val eoppepFilter by viewModel.eoppepFilter.collectAsStateWithLifecycle()
                val eoppepSearch by viewModel.eoppepSearch.collectAsStateWithLifecycle()
                val saekSemester by viewModel.saekSemesterFilter.collectAsStateWithLifecycle()
                val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
                val pharmaCat by viewModel.pharmaCategoryFilter.collectAsStateWithLifecycle()
                val pharmaSearch by viewModel.pharmaSearch.collectAsStateWithLifecycle()
                val selectedSeminarId by viewModel.selectedSeminarId.collectAsStateWithLifecycle()
                val lawSearch by viewModel.lawSearch.collectAsStateWithLifecycle()

                // Flashcard states (Room DB)
                val flashcards by viewModel.filteredFlashcards.collectAsStateWithLifecycle()
                val activeFlashcardIndex by viewModel.activeFlashcardIndex.collectAsStateWithLifecycle()
                val isCardFlipped by viewModel.isCardFlipped.collectAsStateWithLifecycle()
                val flashcardCategoryFilter by viewModel.flashcardCategoryFilter.collectAsStateWithLifecycle()
                val flashcardSearch by viewModel.flashcardSearch.collectAsStateWithLifecycle()
                val flashcardsMode by viewModel.flashcardsMode.collectAsStateWithLifecycle()

                // Quiz states
                val quizQuestions by viewModel.quizQuestions.collectAsStateWithLifecycle()
                val currentQuizIndex by viewModel.currentQuizIndex.collectAsStateWithLifecycle()
                val isQuizFinished by viewModel.quizFinished.collectAsStateWithLifecycle()

                // MENTOR OS states
                val mentorSubModule by viewModel.mentorSubModule.collectAsStateWithLifecycle()
                val mentorFaculty by viewModel.mentorFaculty.collectAsStateWithLifecycle()
                val mentorMissions by viewModel.mentorMissions.collectAsStateWithLifecycle()
                val mentorOrgans by viewModel.mentorOrgans.collectAsStateWithLifecycle()
                val mentorEcg by viewModel.mentorEcg.collectAsStateWithLifecycle()
                val mentorMci by viewModel.mentorMci.collectAsStateWithLifecycle()
                val mentorEquipment by viewModel.mentorEquipment.collectAsStateWithLifecycle()
                val mentorSources by viewModel.mentorSources.collectAsStateWithLifecycle()
                val mentorAudit by viewModel.mentorAudit.collectAsStateWithLifecycle()

                // Intercept back button for smooth sub-navigation
                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        EkabTopBar(
                            currentScreen = currentScreen,
                            onNavigateBack = { viewModel.navigateBack() },
                            onOpenContacts = { viewModel.navigateTo(AppScreen.CONTACTS) },
                            onOpenLaw = { viewModel.navigateTo(AppScreen.LAW) },
                            onOpenPractical = { viewModel.navigateTo(AppScreen.PRACTICAL) },
                            onOpenFlashcards = { viewModel.navigateTo(AppScreen.FLASHCARDS) }
                        )
                    },
                    bottomBar = {
                        // Only show bottom navigation on main tabs
                        if (currentScreen != AppScreen.QUIZ) {
                            EkabBottomNav(
                                currentScreen = currentScreen,
                                onTabSelected = { targetScreen ->
                                    viewModel.navigateTo(targetScreen)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.HOME -> HomeScreen(
                                progress = progress,
                                totalQuestions = allQuestions.size,
                                totalCourses = allCourses.size,
                                totalMeds = allMedications.size,
                                onNavigate = { viewModel.navigateTo(it) },
                                onStartQuickQuiz = { viewModel.startQuiz(count = 10) }
                            )

                            AppScreen.EOPPEP -> EoppepScreen(
                                questions = questions,
                                progress = progress,
                                currentFilter = eoppepFilter,
                                searchQuery = eoppepSearch,
                                onFilterChange = { viewModel.setEoppepFilter(it) },
                                onSearchChange = { viewModel.setEoppepSearch(it) },
                                onToggleBookmark = { viewModel.toggleQuestionBookmark(it) },
                                onToggleCompleted = { viewModel.toggleQuestionCompleted(it) },
                                onStartQuiz = { viewModel.startQuiz(count = 10) }
                            )

                            AppScreen.FLASHCARDS -> FlashcardsScreen(
                                flashcards = flashcards,
                                activeCardIndex = activeFlashcardIndex,
                                isFlipped = isCardFlipped,
                                currentCategory = flashcardCategoryFilter,
                                searchQuery = flashcardSearch,
                                mode = flashcardsMode,
                                onCategoryChange = { viewModel.setFlashcardCategoryFilter(it) },
                                onSearchChange = { viewModel.setFlashcardSearch(it) },
                                onModeChange = { viewModel.setFlashcardsMode(it) },
                                onFlipCard = { viewModel.flipCard() },
                                onNextCard = { viewModel.nextFlashcard() },
                                onPrevCard = { viewModel.prevFlashcard() },
                                onRecordReview = { id, mastered -> viewModel.recordFlashcardReview(id, mastered) },
                                onToggleStar = { viewModel.toggleFlashcardStar(it) },
                                onDeleteCard = { viewModel.deleteFlashcard(it) },
                                onCreateCard = { term, def, cat, hint -> viewModel.createFlashcard(term, def, cat, hint) }
                            )

                            AppScreen.QUIZ -> QuizScreen(
                                questions = quizQuestions,
                                currentIndex = currentQuizIndex,
                                isFinished = isQuizFinished,
                                onSelectOption = { viewModel.answerQuizQuestion(it) },
                                onNext = { viewModel.nextQuizQuestion() },
                                onPrev = { viewModel.prevQuizQuestion() },
                                onFinish = { viewModel.finishQuiz() },
                                onRestart = { viewModel.startQuiz(count = 10) },
                                onExit = { viewModel.navigateTo(AppScreen.EOPPEP) }
                            )

                            AppScreen.SAEK -> SaekScreen(
                                courses = courses,
                                progress = progress,
                                currentSemester = saekSemester,
                                onSemesterSelect = { viewModel.setSaekSemesterFilter(it) },
                                onCourseClick = { viewModel.selectCourse(it) },
                                onToggleCompleted = { viewModel.toggleCourseCompleted(it) }
                            )

                            AppScreen.COURSE_DETAIL -> selectedCourse?.let { course ->
                                CourseDetailScreen(
                                    course = course,
                                    isCompleted = progress.completedCourses.contains(course.id),
                                    onToggleCompleted = { viewModel.toggleCourseCompleted(course.id) }
                                )
                            } ?: Box(modifier = Modifier.fillMaxSize())

                            AppScreen.PHARMA -> PharmaScreen(
                                medications = medications,
                                progress = progress,
                                currentCategory = pharmaCat,
                                searchQuery = pharmaSearch,
                                onCategorySelect = { viewModel.setPharmaCategoryFilter(it) },
                                onSearchChange = { viewModel.setPharmaSearch(it) },
                                onToggleFavorite = { viewModel.toggleMedFavorite(it) }
                            )

                            AppScreen.SEMINARS -> SeminarsScreen(
                                seminars = seminars,
                                selectedTrackId = selectedSeminarId,
                                onSelectTrack = { viewModel.selectSeminar(it) },
                                onStartPractice = { viewModel.startQuiz(count = 10) }
                            )

                            AppScreen.PRACTICAL -> PracticalScreen(
                                sections = practicalSections
                            )

                            AppScreen.LAW -> LawScreen(
                                acts = professionActs,
                                searchQuery = lawSearch,
                                onSearchChange = { viewModel.setLawSearch(it) }
                            )

                            AppScreen.CONTACTS -> ContactsScreen(
                                categories = contacts
                            )

                            AppScreen.MENTOR -> {
                                when (mentorSubModule) {
                                    MentorSubModule.HUB -> MentorHubScreen(
                                        onNavigateSubModule = { subModule ->
                                            viewModel.setMentorSubModule(subModule)
                                        }
                                    )
                                    MentorSubModule.TUTOR -> MentorTutorScreen(
                                        faculty = mentorFaculty
                                    )
                                    MentorSubModule.MISSIONS -> MentorMissionsScreen(
                                        missions = mentorMissions
                                    )
                                    MentorSubModule.ANATOMY_3D -> MentorAnatomyScreen(
                                        organs = mentorOrgans
                                    )
                                    MentorSubModule.ECG_LAB -> MentorEcgScreen(
                                        rhythms = mentorEcg
                                    )
                                    MentorSubModule.TRIAGE_MCI -> MentorTriageScreen(
                                        incidents = mentorMci
                                    )
                                    MentorSubModule.EQUIPMENT_LAB -> MentorEquipmentScreen(
                                        equipmentList = mentorEquipment
                                    )
                                    MentorSubModule.LIBRARY -> MentorLibraryScreen(
                                        sources = mentorSources
                                    )
                                    MentorSubModule.AUDIT_REPORT -> MentorAuditReportScreen(
                                        auditItems = mentorAudit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
