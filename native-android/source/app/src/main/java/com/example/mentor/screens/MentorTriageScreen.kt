package com.example.mentor.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mentor.models.MciIncident
import com.example.ui.theme.*

@Composable
fun MentorTriageScreen(
    incidents: List<MciIncident>
) {
    var selectedIncident by remember { mutableStateOf(incidents.firstOrNull()) }
    var assignedTags by remember { mutableStateOf(mutableMapOf<String, String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mentor_triage_screen")
    ) {
        // Incident Selector
        Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Διαλογή Πολλαπλών Τραυματιών (START Triage)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    incidents.forEach { inc ->
                        val isSelected = selectedIncident?.id == inc.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedIncident = inc
                                assignedTags.clear()
                            },
                            label = { Text(inc.title.take(20) + "…", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        val active = selectedIncident ?: return

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Incident Context
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = RoundedCornerShape(6.dp), color = EkabAmber) {
                                Text(
                                    text = "MCI Incident",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(text = active.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = active.description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }
            }

            // Triage Legend Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TriageTagPill(color = EkabRed, text = "P1 Άμεση", modifier = Modifier.weight(1f))
                    TriageTagPill(color = EkabAmber, text = "P2 Επείγουσα", modifier = Modifier.weight(1f))
                    TriageTagPill(color = EkabGreen, text = "P3 Καθυστερημένη", modifier = Modifier.weight(1f))
                    TriageTagPill(color = Color(0xFF263238), text = "P4 Αποβιώσας", modifier = Modifier.weight(1f))
                }
            }

            // Patients List
            itemsIndexed(active.patients) { pIdx, pat ->
                val assigned = assignedTags[pat.id]
                val isCorrect = assigned != null && pat.triageTag.startsWith(assigned.take(3))

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = pat.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = pat.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Επιλογή Κάρτας Διαλογής (Triage Tag):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Triage Tag Buttons Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val tags = listOf("Κόκκινο" to EkabRed, "Κίτρινο" to EkabAmber, "Πράσινο" to EkabGreen, "Μαύρο" to Color(0xFF263238))
                            tags.forEach { (tagLabel, tagColor) ->
                                val isSelected = assigned == tagLabel
                                OutlinedButton(
                                    onClick = {
                                        assignedTags = (assignedTags + (pat.id to tagLabel)).toMutableMap()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) tagColor else Color.Transparent,
                                        contentColor = if (isSelected) Color.White else tagColor
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(tagLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Clinical Feedback
                        AnimatedVisibility(visible = assigned != null) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isCorrect) EkabGreenContainer else EkabRedContainer
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "✔ ΟΡΘΗ ΔΙΑΛΟΓΗ (${pat.triageTag})" else "✖ ΕΠΑΝΕΞΕΤΑΣΗ: Η σωστή διαλογή είναι ${pat.triageTag}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isCorrect) EkabOnGreenContainer else EkabOnRedContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = pat.rationale,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TriageTagPill(color: Color, text: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
