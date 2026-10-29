package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EoppepQuestion
import com.example.data.model.StudyProgress
import com.example.ui.EoppepFilter
import com.example.ui.theme.*

@Composable
fun EoppepScreen(
    questions: List<EoppepQuestion>,
    progress: StudyProgress,
    currentFilter: EoppepFilter,
    searchQuery: String,
    onFilterChange: (EoppepFilter) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleBookmark: (String) -> Unit,
    onToggleCompleted: (String) -> Unit,
    onStartQuiz: () -> Unit
) {
    var expandAll by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("eoppep_screen")
    ) {
        // Search and Quiz Action Bar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("eoppep_search_input"),
                    placeholder = { Text("Αναζήτηση σε 314 ερωτήσεις & απαντήσεις...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Αναζήτηση")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Καθαρισμός")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips
                ScrollableTabRow(
                    selectedTabIndex = currentFilter.ordinal,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {},
                    containerColor = Color.Transparent
                ) {
                    val tabs = listOf(
                        EoppepFilter.ALL to "Όλες (314)",
                        EoppepFilter.GROUP_A to "Ομάδα Α (105)",
                        EoppepFilter.GROUP_B to "Ομάδα Β (209)",
                        EoppepFilter.SOS to "SOS ⭐",
                        EoppepFilter.BOOKMARKED to "Αγαπημένα (${progress.bookmarkedQuestions.size})"
                    )

                    tabs.forEach { (filter, label) ->
                        val isSelected = currentFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { onFilterChange(filter) },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("chip_filter_${filter.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions row: Quiz CTA & Expand/Collapse Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onStartQuiz,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("start_quiz_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Έναρξη Quiz Εξάσκησης", fontSize = 13.sp)
                    }

                    TextButton(
                        onClick = { expandAll = !expandAll },
                        modifier = Modifier.testTag("toggle_expand_all_btn")
                    ) {
                        Icon(
                            imageVector = if (expandAll) Icons.Filled.UnfoldLess else Icons.Filled.UnfoldMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (expandAll) "Σύμπτυξη" else "Ανάπτυξη όλων",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Questions List
        if (questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Δεν βρέθηκαν ερωτήσεις",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Δοκίμασε διαφορετικούς όρους αναζήτησης ή άλλαξε φίλτρο.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("eoppep_questions_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = questions,
                    key = { it.id }
                ) { item ->
                    QuestionItemCard(
                        question = item,
                        isBookmarked = progress.bookmarkedQuestions.contains(item.id),
                        isCompleted = progress.completedQuestions.contains(item.id),
                        forceExpanded = expandAll,
                        onToggleBookmark = { onToggleBookmark(item.id) },
                        onToggleCompleted = { onToggleCompleted(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionItemCard(
    question: EoppepQuestion,
    isBookmarked: Boolean,
    isCompleted: Boolean,
    forceExpanded: Boolean,
    onToggleBookmark: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    var localExpanded by remember(question.id) { mutableStateOf(false) }
    val expanded = forceExpanded || localExpanded

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_card_${question.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Group/Number, SOS, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (question.group == "A") EkabNavyLight else EkabBlue
                    ) {
                        Text(
                            text = "Ομάδα ${question.group} #${question.number}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (question.isSos) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EkabAmberContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "⭐ SOS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EkabOnAmberContainer,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    if (question.page > 0) {
                        Text(
                            text = "Σελ. ${question.page}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Action Buttons: Bookmark & Completed
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_btn_${question.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Αφαίρεση από αγαπημένα" else "Προσθήκη στα αγαπημένα",
                            tint = if (isBookmarked) EkabAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onToggleCompleted,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("complete_btn_${question.id}")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                            contentDescription = if (isCompleted) "Ολοκληρώθηκε" else "Σήμανση ως μελετημένο",
                            tint = if (isCompleted) EkabGreen else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Topic Pill
            if (question.topic.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Θέμα: ${question.topic}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expand Answer Toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (expanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { localExpanded = !localExpanded }
                    .testTag("toggle_answer_${question.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) "Απόκρυψη Απάντησης" else "Εμφάνιση Επίσημης Απάντησης",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expandable Answer Section
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "ΕΚΠΑΙΔΕΥΤΙΚΗ ΑΠΑΝΤΗΣΗ ΕΟΠΠΕΠ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = question.answer,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}
