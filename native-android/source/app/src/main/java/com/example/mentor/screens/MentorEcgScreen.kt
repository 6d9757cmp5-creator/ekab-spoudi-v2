package com.example.mentor.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mentor.models.EcgRhythm
import com.example.ui.theme.*

@Composable
fun MentorEcgScreen(
    rhythms: List<EcgRhythm>
) {
    var selectedRhythm by remember { mutableStateOf(rhythms.firstOrNull()) }
    var quizMode by remember { mutableStateOf(false) }
    var quizAnswerResult by remember { mutableStateOf<String?>(null) }

    // Animated Sweep Phase for live monitor simulation
    val infiniteTransition = rememberInfiniteTransition(label = "ecgSweep")
    val sweepPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mentor_ecg_screen")
    ) {
        // Top Rhythm Selection Tabs
        Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ECG Intelligence Lab",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    FilterChip(
                        selected = quizMode,
                        onClick = {
                            quizMode = !quizMode
                            quizAnswerResult = null
                        },
                        label = { Text("Λειτουργία Quiz ⚡", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(rhythms) { rh ->
                        val isSelected = selectedRhythm?.id == rh.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedRhythm = rh
                                quizAnswerResult = null
                            },
                            label = {
                                Text(
                                    text = rh.name.split("(").first().trim(),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.5.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        val active = selectedRhythm ?: return

        // Live ECG Oscilloscope Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color(0xFF071410)) // Monitor Deep Dark Green
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                // Draw ECG Millimeter Grid (1mm and 5mm blocks)
                val gridSmall = Color(0xFF0F3B25)
                val gridLarge = Color(0xFF1B643F)

                val step = 14f
                var xPos = 0f
                var count = 0
                while (xPos < w) {
                    val isMajor = count % 5 == 0
                    drawLine(
                        color = if (isMajor) gridLarge else gridSmall,
                        start = Offset(xPos, 0f),
                        end = Offset(xPos, h),
                        strokeWidth = if (isMajor) 1.2f else 0.6f
                    )
                    xPos += step
                    count++
                }

                var yPos = 0f
                var yCount = 0
                while (yPos < h) {
                    val isMajor = yCount % 5 == 0
                    drawLine(
                        color = if (isMajor) gridLarge else gridSmall,
                        start = Offset(0f, yPos),
                        end = Offset(w, yPos),
                        strokeWidth = if (isMajor) 1.2f else 0.6f
                    )
                    yPos += step
                    yCount++
                }

                // Draw Rhythm Waveform
                val points = active.points
                if (points.isNotEmpty()) {
                    val path = Path()
                    val totalCycles = 4
                    val cycleWidth = w / totalCycles.toFloat()

                    var first = true
                    for (c in 0 until totalCycles) {
                        val cycleStart = c * cycleWidth
                        for (i in points.indices) {
                            val px = cycleStart + (i.toFloat() / points.size.toFloat()) * cycleWidth
                            val py = midY - (points[i] * 65f)
                            if (first) {
                                path.moveTo(px, py)
                                first = false
                            } else {
                                path.lineTo(px, py)
                            }
                        }
                    }

                    // Neon Green ECG line
                    drawPath(
                        path = path,
                        color = Color(0xFF00E676),
                        style = Stroke(width = 3.5f)
                    )

                    // Live Sweep Bar line
                    val sweepX = sweepPhase * w
                    drawLine(
                        color = Color(0xFFB9F6CA),
                        start = Offset(sweepX, 0f),
                        end = Offset(sweepX, h),
                        strokeWidth = 3f
                    )
                }
            }

            // Top Monitor Metrics Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00E676)))
                    Text(
                        text = if (quizMode) "ΑΓΝΩΣΤΟΣ ΡΥΘΜΟΣ ???" else active.name,
                        color = Color(0xFFB9F6CA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = "HR: ${active.rate.split("-").first().trim().filter { it.isDigit() }.ifEmpty { "0" }} bpm",
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Rhythm Clinical Breakdown / Quiz view
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (quizMode) {
                // Quiz Question View
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ερώτηση Αναγνώρισης Ρυθμού:",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Παρατηρήστε την κυματομορφή στο μόνιτορ. Ποιος ρυθμός απεικονίζεται;",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            rhythms.forEach { rh ->
                                val isAnswered = quizAnswerResult != null
                                val isCorrectOption = rh.id == active.id

                                Button(
                                    onClick = {
                                        quizAnswerResult = if (isCorrectOption) "Σωστό!" else "Λάθος!"
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAnswered && isCorrectOption) EkabGreen else MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = if (isAnswered && isCorrectOption) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(rh.name)
                                }
                            }

                            quizAnswerResult?.let { res ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "$res ${active.clinicalSummary}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.startsWith("Σωστό")) EkabGreen else EkabRed
                                )
                            }
                        }
                    }
                }
            } else {
                // Detailed Scientific Rhythm Analysis Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = active.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.weight(1f)
                                )

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (active.shockable) EkabRedContainer else EkabBlueContainer
                                ) {
                                    Text(
                                        text = if (active.shockable) "⚡ ΑΠΙΝΙΔΩΣΙΜΟΣ" else "ΜΗ ΑΠΙΝΙΔΩΣΙΜΟΣ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (active.shockable) EkabOnRedContainer else EkabOnBlueContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Parameters Grid
                            EcgMetricRow(label = "Συχνότητα", value = active.rate)
                            EcgMetricRow(label = "Κανονικότητα", value = active.regularity)
                            EcgMetricRow(label = "Κύμα P", value = active.pWave)
                            EcgMetricRow(label = "Διάστημα PR", value = active.prInterval)
                            EcgMetricRow(label = "Σύμπλεγμα QRS", value = active.qrsDuration)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Clinical Guidance
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (active.shockable) EkabRedContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Κλινική Αντιμετώπιση (ERC 2025):",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (active.shockable) EkabRed else MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = active.clinicalSummary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            lineHeight = 18.sp,
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
}

@Composable
fun EcgMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }
}
