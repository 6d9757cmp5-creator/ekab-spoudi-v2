package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.FlashcardEntity
import com.example.ui.theme.*

@Composable
fun FlashcardsScreen(
    flashcards: List<FlashcardEntity>,
    activeCardIndex: Int,
    isFlipped: Boolean,
    currentCategory: String,
    searchQuery: String,
    mode: String, // "review" or "manage"
    onCategoryChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onModeChange: (String) -> Unit,
    onFlipCard: () -> Unit,
    onNextCard: () -> Unit,
    onPrevCard: () -> Unit,
    onRecordReview: (Long, Boolean) -> Unit,
    onToggleStar: (Long) -> Unit,
    onDeleteCard: (Long) -> Unit,
    onCreateCard: (String, String, String, String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcards_screen")
    ) {
        // Top Toolbar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Mode Toggle & Add Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.weight(1f)
                    ) {
                        SegmentedButton(
                            selected = mode == "review",
                            onClick = { onModeChange("review") },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Icon(imageVector = Icons.Filled.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Μελέτη (Cards)", fontSize = 12.sp)
                        }
                        SegmentedButton(
                            selected = mode == "manage",
                            onClick = { onModeChange("manage") },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Icon(imageVector = Icons.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Όλες (${flashcards.size})", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledTonalIconButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("add_flashcard_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Προσθήκη Κάρτας")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar (active in manage mode or when query entered)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("flashcards_search_input"),
                    placeholder = { Text("Αναζήτηση σε όρους, φάρμακα, ορισμούς...") },
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
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Filter Chips
                val categories = listOf("Όλα", "Φαρμακολογία", "Ιατρική Ορολογία", "Τραύμα & ALS", "Αγαπημένα ⭐")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = currentCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategoryChange(cat) },
                            label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("flashcard_chip_${cat.take(4)}")
                        )
                    }
                }
            }
        }

        // Body Content
        if (flashcards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Style,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Δεν βρέθηκαν κάρτες",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Προσθέστε μία νέα κάρτα με το κουμπί (+) ή αλλάξτε φίλτρο.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Δημιουργία Νέας Κάρτας")
                    }
                }
            }
        } else if (mode == "review") {
            // Interactive 3D Card Review Mode
            val safeIndex = activeCardIndex.coerceIn(0, flashcards.size - 1)
            val currentCard = flashcards[safeIndex]

            FlashcardReviewView(
                card = currentCard,
                index = safeIndex,
                total = flashcards.size,
                isFlipped = isFlipped,
                onFlip = onFlipCard,
                onNext = onNextCard,
                onPrev = onPrevCard,
                onRecordReview = { mastered -> onRecordReview(currentCard.id, mastered) },
                onToggleStar = { onToggleStar(currentCard.id) }
            )
        } else {
            // Manage / List Mode
            FlashcardsListView(
                flashcards = flashcards,
                onToggleStar = onToggleStar,
                onDeleteCard = onDeleteCard
            )
        }
    }

    // Create Card Dialog
    if (showCreateDialog) {
        CreateFlashcardDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { term, definition, category, hint ->
                onCreateCard(term, definition, category, hint)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun FlashcardReviewView(
    card: FlashcardEntity,
    index: Int,
    total: Int,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onRecordReview: (Boolean) -> Unit,
    onToggleStar: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "cardFlip"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress & Star Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Κάρτα ${index + 1} από $total",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            IconButton(
                onClick = onToggleStar,
                modifier = Modifier.testTag("star_flashcard_${card.id}")
            ) {
                Icon(
                    imageVector = if (card.isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Αστέρι",
                    tint = if (card.isStarred) EkabAmber else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3D Flippable Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { onFlip() }
                .testTag("flashcard_flip_box"),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                // FRONT OF CARD: Question / Term
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Category Pill
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (card.category) {
                                "Φαρμακολογία" -> EkabAmberContainer
                                "Τραύμα & ALS" -> EkabRedContainer
                                else -> EkabBlueContainer
                            }
                        ) {
                            Text(
                                text = card.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = when (card.category) {
                                        "Φαρμακολογία" -> EkabOnAmberContainer
                                        "Τραύμα & ALS" -> EkabOnRedContainer
                                        else -> EkabOnBlueContainer
                                    }
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Term / Question
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = card.term,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 32.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (card.hint.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "💡 Υπόδειξη: ${card.hint}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // Tap to Flip Prompt
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TouchApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Πάτησε για περιστροφή & απάντηση",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                // BACK OF CARD: Definition / Clinical Answer
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = "ΟΡΙΣΜΟΣ & ΚΛΙΝΙΚΗ ΣΗΜΑΣΙΑ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Text(
                                    text = card.definition,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 15.sp,
                                        lineHeight = 24.sp,
                                        textAlign = TextAlign.Start
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Autorenew,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Επαναλήψεις: ${card.timesReviewed} · Κατάσταση: ${if (card.masteryLevel >= 2) "Κατακτήθηκε ✔️" else "Σε εξάσκηση"}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mastery Review Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { onRecordReview(false) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EkabRed),
                modifier = Modifier
                    .weight(1f)
                    .testTag("flashcard_hard_btn")
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Δυσκολεύτηκα")
            }

            Button(
                onClick = { onRecordReview(true) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EkabGreen),
                modifier = Modifier
                    .weight(1f)
                    .testTag("flashcard_easy_btn")
            ) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Το Κατέχω!")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Previous / Next Navigation Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPrev,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("flashcard_prev_btn")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Προηγούμενη")
            }

            FilledTonalButton(
                onClick = onFlip,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("flashcard_flip_btn")
            ) {
                Icon(imageVector = Icons.Filled.Flip, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Αναστροφή")
            }

            OutlinedButton(
                onClick = onNext,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("flashcard_next_btn")
            ) {
                Text("Επόμενη")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun FlashcardsListView(
    flashcards: List<FlashcardEntity>,
    onToggleStar: (Long) -> Unit,
    onDeleteCard: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcards_list_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(flashcards, key = { it.id }) { card ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (card.category) {
                                "Φαρμακολογία" -> EkabAmberContainer
                                "Τραύμα & ALS" -> EkabRedContainer
                                else -> EkabBlueContainer
                            }
                        ) {
                            Text(
                                text = card.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = when (card.category) {
                                        "Φαρμακολογία" -> EkabOnAmberContainer
                                        "Τραύμα & ALS" -> EkabOnRedContainer
                                        else -> EkabOnBlueContainer
                                    }
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleStar(card.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (card.isStarred) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "Αστέρι",
                                    tint = if (card.isStarred) EkabAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { onDeleteCard(card.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Διαγραφή",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = card.term,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = card.definition,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        ),
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
fun CreateFlashcardDialog(
    onDismiss: () -> Unit,
    onConfirm: (term: String, definition: String, category: String, hint: String) -> Unit
) {
    var term by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Φαρμακολογία") }
    val categories = listOf("Φαρμακολογία", "Ιατρική Ορολογία", "Τραύμα & ALS", "Ανατομία", "ΕΟΠΠΕΠ")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Νέα Κάρτα Μελέτης",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = term,
                    onValueChange = { term = it },
                    label = { Text("Όρος ή Φάρμακο / Ερώτηση") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_term_input")
                )

                OutlinedTextField(
                    value = definition,
                    onValueChange = { definition = it },
                    label = { Text("Ορισμός / Δοσολογία / Απάντηση") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_def_input")
                )

                OutlinedTextField(
                    value = hint,
                    onValueChange = { hint = it },
                    label = { Text("Υπόδειξη (Προαιρετικό)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Κατηγορία:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (term.isNotBlank() && definition.isNotBlank()) {
                        onConfirm(term, definition, selectedCategory, hint)
                    }
                },
                enabled = term.isNotBlank() && definition.isNotBlank(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dialog_save_btn")
            ) {
                Text("Αποθήκευση")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ακύρωση")
            }
        }
    )
}
