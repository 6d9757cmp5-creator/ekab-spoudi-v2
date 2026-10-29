package com.example.mentor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class MentorSubModule {
    HUB,
    TUTOR,
    MISSIONS,
    ANATOMY_3D,
    ECG_LAB,
    TRIAGE_MCI,
    EQUIPMENT_LAB,
    LIBRARY,
    AUDIT_REPORT
}

data class MentorFeatureTile(
    val module: MentorSubModule,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun MentorHubScreen(
    onNavigateSubModule: (MentorSubModule) -> Unit
) {
    val features = listOf(
        MentorFeatureTile(
            module = MentorSubModule.TUTOR,
            title = "Προσωπικός AI Καθηγητής",
            subtitle = "5 εξειδικευμένοι κλινικοί εκπαιδευτές & τεκμηριωμένη διδασκαλία",
            badge = "Ιατρική Ομάδα AI",
            icon = Icons.Filled.Psychology,
            color = EkabNavy
        ),
        MentorFeatureTile(
            module = MentorSubModule.MISSIONS,
            title = "ΕΚΑΒ Live — Αποστολές",
            subtitle = "Διαδραστικά περιστατικά: Κλήση, <C>ABCDE, SBAR παράδοση",
            badge = "Προσομοιωτής",
            icon = Icons.Filled.Emergency,
            color = EkabRed
        ),
        MentorFeatureTile(
            module = MentorSubModule.ANATOMY_3D,
            title = "Ζωντανή 3D Ανατομία",
            subtitle = "Ανδρικό/Γυναικείο μοντέλο, 5 συστήματα, περιστροφή 360° & τραύμα",
            badge = "3D Εργαστήριο",
            icon = Icons.Filled.AccessibilityNew,
            color = EkabBlue
        ),
        MentorFeatureTile(
            module = MentorSubModule.ECG_LAB,
            title = "ECG Intelligence Lab",
            subtitle = "ΗΚΓ κυματομορφή, αναγνώριση ρυθμών & αλγόριθμοι ERC 2025",
            badge = "Ηλεκτροκαρδιογράφημα",
            icon = Icons.AutoMirrored.Filled.ShowChart,
            color = Color(0xFF00897B)
        ),
        MentorFeatureTile(
            module = MentorSubModule.TRIAGE_MCI,
            title = "Πολλαπλοί Τραυματίες (MCI)",
            subtitle = "Διαλογή START / Triage Sieve σε τροχαία & μαζικά συμβάντα",
            badge = "Διαλογή Triage",
            icon = Icons.Filled.Warning,
            color = EkabAmber
        ),
        MentorFeatureTile(
            module = MentorSubModule.EQUIPMENT_LAB,
            title = "Ιατρικός Εξοπλισμός",
            subtitle = "AED, Ασκός BVM AMBU, Μόνιτορ & πρωτόκολλα ασφαλείας",
            badge = "Εξοπλισμός",
            icon = Icons.Filled.MedicalServices,
            color = Color(0xFF5E35B1)
        ),
        MentorFeatureTile(
            module = MentorSubModule.LIBRARY,
            title = "Ιατρική Βιβλιοθήκη (PR #83)",
            subtitle = "15 επιστημονικές πηγές, WisTech, WHO-ICRC, Open RN & ERC 2025",
            badge = "1.8M Λέξεις",
            icon = Icons.Filled.LocalLibrary,
            color = Color(0xFF3949AB)
        ),
        MentorFeatureTile(
            module = MentorSubModule.AUDIT_REPORT,
            title = "Πίνακας Επαλήθευσης & Audit",
            subtitle = "Αναφορά συμμόρφωσης, PR #83, #85, #6, #39 & κατάσταση εγκρίσεων",
            badge = "Έλεγχος Ποιότητας",
            icon = Icons.AutoMirrored.Filled.FactCheck,
            color = EkabGreen
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mentor_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EkabNavy,
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "⚡",
                                    fontSize = 28.sp
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "MENTOR OS",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EkabAmberContainer
                                ) {
                                    Text(
                                        text = "Clinical AI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EkabOnAmberContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Επιστημονικό σύστημα εκπαίδευσης & προσομοίωσης ΕΚΑΒ",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ολοκληρωμένη κλινική ακαδημία για σπουδαστές ΣΑΕΚ και υποψηφίους ΕΟΠΠΕΠ. Περιλαμβάνει 3D ανατομία, προσομοιωτή αποστολών, εργαστήριο ECG, διαλογή μαζικών συμβάντων και τεκμηριωμένη βιβλιοθήκη 15 πηγών.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Εκπαιδευτικά Εργαστήρια & Υποσυστήματα",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Feature Tiles List
        items(features.size) { idx ->
            val feat = features[idx]
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mentor_tile_${feat.module.name.lowercase()}")
                    .clickable { onNavigateSubModule(feat.module) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(feat.color),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = feat.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = feat.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = feat.subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            ),
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = feat.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Μετάβαση",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Disclaimer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Εκπαιδευτική προσομοίωση MENTOR OS. Δεν αποτελεί πραγματική ιατρική διάγνωση και δεν υποκαθιστά την ανθρώπινη κλινική εκπαίδευση στο πεδίο.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}
