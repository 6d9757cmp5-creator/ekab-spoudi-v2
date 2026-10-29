package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.theme.EkabNavy
import com.example.ui.theme.EkabRed

data class BottomNavItem(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EkabTopBar(
    currentScreen: AppScreen,
    onNavigateBack: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenLaw: () -> Unit,
    onOpenPractical: () -> Unit,
    onOpenFlashcards: () -> Unit
) {
    val title = when (currentScreen) {
        AppScreen.HOME -> "ΕΚΑΒ Σπουδή"
        AppScreen.EOPPEP -> "Πιστοποίηση ΕΟΠΠΕΠ"
        AppScreen.FLASHCARDS -> "Κάρτες Flashcards"
        AppScreen.SAEK -> "Κατάρτιση ΣΑΕΚ ΕΚΑΒ"
        AppScreen.PHARMA -> "Επείγουσα Φαρμακολογία"
        AppScreen.SEMINARS -> "Προετοιμασία Σεμιναρίων"
        AppScreen.PRACTICAL -> "Πρακτικό Μέρος ΕΟΠΠΕΠ"
        AppScreen.LAW -> "Επάγγελμα & Νομοθεσία"
        AppScreen.CONTACTS -> "Τηλέφωνα & Επαφές ΕΚΑΒ"
        AppScreen.QUIZ -> "Quiz Εξάσκησης"
        AppScreen.COURSE_DETAIL -> "Αναλυτικό Πρόγραμμα"
        AppScreen.MENTOR -> "MENTOR OS"
    }

    val subtitle = when (currentScreen) {
        AppScreen.HOME -> "Διασώστης – Πλήρωμα Ασθενοφόρου"
        AppScreen.EOPPEP -> "314 Ερωτήσεις Θεωρίας & SOS"
        AppScreen.FLASHCARDS -> "Εξάσκηση Ορολογίας & Φαρμάκων"
        AppScreen.SAEK -> "4 Εξάμηνα · 20 Μαθήματα"
        AppScreen.PHARMA -> "70 Φαρμακευτικές Καρτέλες"
        AppScreen.SEMINARS -> "BLS, PBLS, ILS, PHTLS, ALS"
        AppScreen.PRACTICAL -> "12 Ικανότητες & 6 Σενάρια"
        AppScreen.LAW -> "Π.Δ. 348/96 & Π.Δ. 62/07"
        AppScreen.CONTACTS -> "166 · 112 · Περιφερειακά Κέντρα"
        AppScreen.QUIZ -> "Προσομοίωση Εξετάσεων"
        AppScreen.COURSE_DETAIL -> "Κεφάλαια & Κλινική Σύνδεση"
        AppScreen.MENTOR -> "Προσωπικός AI Καθηγητής & Κλινικά Εργαστήρια"
    }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            if (currentScreen != AppScreen.HOME) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Επιστροφή"
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EkabNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✚",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        actions = {
            if (currentScreen == AppScreen.HOME) {
                IconButton(
                    onClick = onOpenFlashcards,
                    modifier = Modifier.testTag("top_bar_flashcards_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Style,
                        contentDescription = "Κάρτες Flashcards",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onOpenLaw,
                    modifier = Modifier.testTag("top_bar_law_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Gavel,
                        contentDescription = "Επάγγελμα και Νομοθεσία",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = onOpenPractical,
                    modifier = Modifier.testTag("top_bar_practical_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MedicalServices,
                        contentDescription = "Πρακτικό Μέρος ΕΟΠΠΕΠ",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(
                    onClick = onOpenContacts,
                    modifier = Modifier.testTag("top_bar_contacts_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = "Επείγοντα τηλέφωνα ΕΚΑΒ",
                        tint = EkabRed
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun EkabBottomNav(
    currentScreen: AppScreen,
    onTabSelected: (AppScreen) -> Unit
) {
    val navItems = listOf(
        BottomNavItem(AppScreen.HOME, "Αρχική", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(AppScreen.MENTOR, "Mentor", Icons.Filled.Psychology, Icons.Outlined.Psychology),
        BottomNavItem(AppScreen.EOPPEP, "ΕΟΠΠΕΠ", Icons.Filled.School, Icons.Outlined.School),
        BottomNavItem(AppScreen.FLASHCARDS, "Κάρτες", Icons.Filled.Style, Icons.Outlined.Style),
        BottomNavItem(AppScreen.SAEK, "ΣΑΕΚ", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
        BottomNavItem(AppScreen.PHARMA, "Φάρμακα", Icons.Filled.Medication, Icons.Outlined.Medication)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("bottom_nav_bar")
    ) {
        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen ||
                    (item.screen == AppScreen.SAEK && currentScreen == AppScreen.COURSE_DETAIL) ||
                    (item.screen == AppScreen.EOPPEP && currentScreen == AppScreen.QUIZ)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("tab_${item.screen.name.lowercase()}")
            )
        }
    }
}
