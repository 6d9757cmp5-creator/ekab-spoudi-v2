package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.PharmacologyItem
import com.example.data.model.StudyProgress
import com.example.ui.theme.*

@Composable
fun PharmaScreen(
    medications: List<PharmacologyItem>,
    progress: StudyProgress,
    currentCategory: String,
    searchQuery: String,
    onCategorySelect: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    var selectedItemForDialog by remember { mutableStateOf<PharmacologyItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("pharma_screen")
    ) {
        // Search & Filter header
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pharma_search_input"),
                    placeholder = { Text("Αναζήτηση φαρμάκου (π.χ. Αδρεναλίνη, Epinephrine)...") },
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

                // Category Chips
                val categories = listOf("Όλα", "Αναζωογόνηση", "Καρδιολογία", "Αναπνευστικό", "Αναλγησία", "Νευρολογία", "Ειδικά")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = currentCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(cat) },
                            label = { Text(cat, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("pharma_cat_$cat")
                        )
                    }
                }
            }
        }

        // Medications Count Banner
        Text(
            text = "Εμφάνιση ${medications.size} φαρμακευτικών καρτελών (ERC 2025)",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Medications List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("pharma_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(medications, key = { it.id }) { med ->
                val isFav = progress.favoriteMeds.contains(med.id)
                MedicationCard(
                    medication = med,
                    isFavorite = isFav,
                    onToggleFavorite = { onToggleFavorite(med.id) },
                    onClick = { selectedItemForDialog = med }
                )
            }
        }
    }

    // Detail Dialog
    selectedItemForDialog?.let { med ->
        AlertDialog(
            onDismissRequest = { selectedItemForDialog = null },
            title = {
                Column {
                    Text(
                        text = med.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    if (med.english.isNotBlank()) {
                        Text(
                            text = med.english,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Κατηγορία: ${med.category} · Επίπεδο: ${med.scope}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (med.indication.isNotBlank()) {
                        item {
                            ClinicalSection(title = "Ενδείξεις & Δοσολογία", content = med.indication, color = EkabNavy)
                        }
                    }

                    if (med.mechanism.isNotBlank()) {
                        item {
                            ClinicalSection(title = "Μηχανισμός Δράσης", content = med.mechanism, color = EkabBlue)
                        }
                    }

                    if (med.caution.isNotBlank()) {
                        item {
                            ClinicalSection(title = "Προφυλάξεις & Αντενδείξεις", content = med.caution, color = EkabRed)
                        }
                    }

                    if (med.monitor.isNotBlank()) {
                        item {
                            ClinicalSection(title = "Παρακολούθηση Ασθενούς", content = med.monitor, color = EkabAmber)
                        }
                    }

                    if (med.notes.isNotBlank()) {
                        item {
                            ClinicalSection(title = "Κλινικές Σημειώσεις", content = med.notes, color = EkabGreen)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedItemForDialog = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Κλείσιμο")
                }
            }
        )
    }
}

@Composable
fun MedicationCard(
    medication: PharmacologyItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("med_card_${medication.id}")
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medication.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (medication.english.isNotBlank()) {
                        Text(
                            text = medication.english,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Αγαπημένο",
                        tint = if (isFavorite) EkabAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pills row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = medication.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (medication.scope == "advanced") EkabAmberContainer else EkabBlueContainer
                ) {
                    Text(
                        text = if (medication.scope == "advanced") "Advanced ALS" else "Προνοσοκομειακό",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (medication.scope == "advanced") EkabOnAmberContainer else EkabOnBlueContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (medication.indication.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ένδειξη: ${medication.indication}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun ClinicalSection(title: String, content: String, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
