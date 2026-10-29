package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.ui.theme.*

@Composable
fun QuizScreen(
    questions: List<QuizQuestion>,
    currentIndex: Int,
    isFinished: Boolean,
    onSelectOption: (Int) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onFinish: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (isFinished) {
        QuizResultsView(
            questions = questions,
            onRestart = onRestart,
            onExit = onExit
        )
        return
    }

    val currentQ = questions.getOrNull(currentIndex) ?: return
    val total = questions.size
    val progress = (currentIndex + 1).toFloat() / total.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_screen")
    ) {
        // Progress Header
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ερώτηση ${currentIndex + 1} από $total",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    IconButton(
                        onClick = onExit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Έξοδος από το Quiz")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Question Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EkabNavy
                            ) {
                                Text(
                                    text = "Ομάδα ${currentQ.question.group}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            if (currentQ.question.isSos) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EkabAmberContainer
                                ) {
                                    Text(
                                        text = "⭐ SOS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EkabOnAmberContainer,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentQ.question.question,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 24.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Options List
            item {
                Text(
                    text = "Επίλεξε τη σωστή διατύπωση:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(currentQ.options.size) { optIdx ->
                val optionText = currentQ.options[optIdx]
                val isSelected = currentQ.selectedIndex == optIdx
                val isCorrect = optIdx == currentQ.correctIndex
                val hasAnswered = currentQ.selectedIndex != null

                val optionLetter = when (optIdx) {
                    0 -> "Α"
                    1 -> "Β"
                    2 -> "Γ"
                    else -> "Δ"
                }

                val borderColor = when {
                    !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    isCorrect -> EkabGreen
                    isSelected -> EkabRed
                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                }

                val bgColor = when {
                    !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    isCorrect -> EkabGreenContainer
                    isSelected -> EkabRedContainer
                    else -> MaterialTheme.colorScheme.surface
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = BorderStroke(1.5.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_option_${optIdx}")
                        .clickable(enabled = !hasAnswered) {
                            onSelectOption(optIdx)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        !hasAnswered -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        isCorrect -> EkabGreen
                                        isSelected -> EkabRed
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optionLetter,
                                color = if (isSelected || (hasAnswered && isCorrect)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (hasAnswered) {
                            if (isCorrect) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Σωστό",
                                    tint = EkabGreen
                                )
                            } else if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Cancel,
                                    contentDescription = "Λάθος",
                                    tint = EkabRed
                                )
                            }
                        }
                    }
                }
            }

            // Explanation Section when Answered
            item {
                AnimatedVisibility(visible = currentQ.selectedIndex != null) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = EkabAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Επίσημη Κλινική Επεξήγηση:",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentQ.question.answer,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar for Quiz
        Surface(
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Προηγούμενη")
                }

                if (currentIndex < total - 1) {
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("quiz_next_btn")
                    ) {
                        Text("Επόμενη")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = onFinish,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EkabGreen),
                        modifier = Modifier.testTag("quiz_finish_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ολοκλήρωση Quiz")
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResultsView(
    questions: List<QuizQuestion>,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val total = questions.size
    val correct = questions.count { it.selectedIndex == it.correctIndex }
    val percentage = if (total > 0) (correct * 100) / total else 0

    val (gradeTitle, gradeColor) = when {
        percentage >= 85 -> "Άριστη Επίδοση!" to EkabGreen
        percentage >= 60 -> "Επιτυχής Εξάσκηση!" to MaterialTheme.colorScheme.primary
        else -> "Χρειάζεται Επανάληψη" to EkabAmber
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quiz_results_screen"),
        contentPadding = PaddingValues(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(gradeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = gradeColor,
                    modifier = Modifier.size(54.dp)
                )
            }
        }

        item {
            Text(
                text = gradeTitle,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = gradeColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Αποτέλεσμα προσομοίωσης εξετάσεων ΕΟΠΠΕΠ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentage%",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = gradeColor
                            )
                        )
                        Text(
                            text = "Ποσοστό",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(50.dp)
                            .width(1.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$correct / $total",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Σωστές Απαντήσεις",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRestart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quiz_restart_btn")
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Νέο Quiz")
                }

                OutlinedButton(
                    onClick = onExit,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quiz_exit_btn")
                ) {
                    Text("Επιστροφή")
                }
            }
        }
    }
}
