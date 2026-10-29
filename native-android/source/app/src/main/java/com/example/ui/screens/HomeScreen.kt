package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StudyProgress
import com.example.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    progress: StudyProgress,
    totalQuestions: Int,
    totalCourses: Int,
    totalMeds: Int,
    onNavigate: (AppScreen) -> Unit,
    onStartQuickQuiz: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with Image and Welcome
        item {
            HeroHeaderCard(
                progress = progress,
                totalQuestions = totalQuestions,
                totalCourses = totalCourses
            )
        }

        // Quick Stats Row
        item {
            QuickStatsRow(
                progress = progress,
                totalQuestions = totalQuestions,
                totalCourses = totalCourses,
                totalMeds = totalMeds
            )
        }

        // Section Title
        item {
            Text(
                text = "Εκπαιδευτικές Ενότητες",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        // 0. MENTOR OS Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_module_mentor")
                    .clickable { onNavigate(AppScreen.MENTOR) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(EkabNavy, EkabBlue))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = "MENTOR OS",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "MENTOR OS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EkabRed
                            ) {
                                Text(
                                    text = "AI ACADEMY",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "AI Καθηγητής · 3D Ανατομία · ECG Lab · Προσομοίωση Ασθενοφόρου · MCI Triage · 15 Πηγές PR #83",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "8 Εξειδικευμένα Εργαστήρια",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Μετάβαση",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 1. SAEK Card
        item {
            EducationalModuleCard(
                title = "Κατάρτιση ΣΑΕΚ ΕΚΑΒ",
                subtitle = "Το πρώτο βήμα · Εξάμηνα, 20 μαθήματα και αναλυτικά κεφάλαια",
                badge = "4 Εξάμηνα",
                icon = Icons.Filled.MenuBook,
                iconBg = EkabNavy,
                testTag = "card_module_saek",
                onClick = { onNavigate(AppScreen.SAEK) }
            )
        }

        // 2. EOPPEP Card
        item {
            EducationalModuleCard(
                title = "Πιστοποίηση ΕΟΠΠΕΠ",
                subtitle = "314 Ερωτήσεις Θεωρίας · Ομάδα Α & Β · SOS · Εξάσκηση",
                badge = "${progress.completedQuestions.size}/314 Μελετήθηκαν",
                icon = Icons.Filled.School,
                iconBg = EkabBlue,
                testTag = "card_module_eoppep",
                onClick = { onNavigate(AppScreen.EOPPEP) }
            )
        }

        // 3. Pharmacology Card
        item {
            EducationalModuleCard(
                title = "Επείγουσα Φαρμακολογία",
                subtitle = "70 φάρμακα, δόσεις, ενδείξεις & κατευθυντήριες οδηγίες ERC 2025",
                badge = "$totalMeds Φάρμακα",
                icon = Icons.Filled.Medication,
                iconBg = EkabAmber,
                testTag = "card_module_pharma",
                onClick = { onNavigate(AppScreen.PHARMA) }
            )
        }

        // 4. Seminars Card
        item {
            EducationalModuleCard(
                title = "Προετοιμασία Σεμιναρίων",
                subtitle = "BLS/AED · PBLS · ILS · PHTLS · ALS αναλυτικά βήματα",
                badge = "5 Σεμινάρια",
                icon = Icons.Filled.MonitorHeart,
                iconBg = EkabRed,
                testTag = "card_module_seminars",
                onClick = { onNavigate(AppScreen.SEMINARS) }
            )
        }

        // 5. Flashcards Card
        item {
            EducationalModuleCard(
                title = "Κάρτες Μελέτης (Flashcards)",
                subtitle = "Επανάληψη ιατρικής ορολογίας, δόσεων φαρμάκων & κλινικών στοιχείων (Room DB)",
                badge = "Διαδραστικές Κάρτες",
                icon = Icons.Filled.Style,
                iconBg = EkabGreen,
                testTag = "card_module_flashcards",
                onClick = { onNavigate(AppScreen.FLASHCARDS) }
            )
        }

        // Quick Tools Header
        item {
            Text(
                text = "Εργαλεία & Άμεση Πρόσβαση",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }

        // Quick Actions Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickToolButton(
                    title = "Quiz Εξάσκησης",
                    subtitle = "10 Τυχαίες Ερωτήσεις",
                    icon = Icons.Filled.Quiz,
                    iconTint = EkabBlue,
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_quiz",
                    onClick = onStartQuickQuiz
                )
                QuickToolButton(
                    title = "Πρακτικό ΕΟΠΠΕΠ",
                    subtitle = "12 Ικανότητες & 6 Σενάρια",
                    icon = Icons.Filled.HealthAndSafety,
                    iconTint = EkabGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_practical",
                    onClick = { onNavigate(AppScreen.PRACTICAL) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickToolButton(
                    title = "Επάγγελμα & Νόμοι",
                    subtitle = "Π.Δ. 348/96 & Διατάξεις",
                    icon = Icons.Filled.Gavel,
                    iconTint = EkabNavyLight,
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_law",
                    onClick = { onNavigate(AppScreen.LAW) }
                )
                QuickToolButton(
                    title = "Επείγοντα 166 / 112",
                    subtitle = "Τηλέφωνα & Κέντρα ΕΚΑΒ",
                    icon = Icons.Filled.Emergency,
                    iconTint = EkabRed,
                    modifier = Modifier.weight(1f),
                    testTag = "quick_tool_contacts",
                    onClick = { onNavigate(AppScreen.CONTACTS) }
                )
            }
        }

        // Disclaimer Note
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Ανεξάρτητο εκπαιδευτικό βοήθημα για τη μελέτη. Δεν απονέμει επίσημη πιστοποίηση και δεν υποκαθιστά την πρακτική εκπαίδευση στο πεδίο.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HeroHeaderCard(
    progress: StudyProgress,
    totalQuestions: Int,
    totalCourses: Int
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_header_card")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ekab_hero_banner),
                    contentDescription = "ΕΚΑΒ Ασθενοφόρο & Διασώστες",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    EkabNavy.copy(alpha = 0.85f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ΕΚΑΒ Σπουδή",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Ολοκληρωμένος οδηγός διασώστη & πληρώματος ασθενοφόρου",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Study Progress Summary
            val pctQuestions = if (totalQuestions > 0) {
                (progress.completedQuestions.size.toFloat() / totalQuestions.toFloat())
            } else 0f

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Συνολική Πρόοδος Μελέτης",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${(pctQuestions * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { pctQuestions },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Composable
fun QuickStatsRow(
    progress: StudyProgress,
    totalQuestions: Int,
    totalCourses: Int,
    totalMeds: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatPill(
            count = "${progress.completedQuestions.size}/$totalQuestions",
            label = "Ερωτήσεις",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            count = "${progress.completedCourses.size}/$totalCourses",
            label = "Μαθήματα",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            count = "${progress.bookmarkedQuestions.size}",
            label = "Αγαπημένα",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            count = "${progress.bestScore}%",
            label = "Καλύτερο Quiz",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatPill(
    count: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun EducationalModuleCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    iconBg: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Μετάβαση",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun QuickToolButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}
