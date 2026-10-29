package com.example.mentor.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mentor.models.LibrarySource
import com.example.ui.theme.*

@Composable
fun MentorLibraryScreen(
    sources: List<LibrarySource>
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("Όλες") } // "Όλες", "Εγκεκριμένα (7)", "Καταχωρισμένες (8)"

    val filteredSources = remember(sources, searchQuery, filterMode) {
        val qTrim = searchQuery.trim().lowercase()
        sources.filter { s ->
            val matchesFilter = when (filterMode) {
                "Εγκεκριμένα (7)" -> s.verifiedStatus.contains("Εγκεκριμένο")
                "Καταχωρισμένες (8)" -> !s.verifiedStatus.contains("Εγκεκριμένο")
                else -> true
            }
            val matchesQuery = if (qTrim.isEmpty()) true else {
                s.title.lowercase().contains(qTrim) ||
                        s.publisher.lowercase().contains(qTrim) ||
                        s.coverage.any { it.lowercase().contains(qTrim) }
            }
            matchesFilter && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mentor_library_screen")
    ) {
        // Search & Filter Header
        Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Ιατρική Βιβλιοθήκη MENTOR OS (PR #83)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "15 Επιστημονικές Πηγές · 7 Εισαγμένα Εγχειρίδια Πλήρους Κειμένου · ~1.811.039 λέξεις",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Αναζήτηση πηγής, εκδότη, θεματολογίας...") },
                    leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Όλες", "Εγκεκριμένα (7)", "Καταχωρισμένες (8)").forEach { opt ->
                        val isSelected = filterMode == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { filterMode = opt },
                            label = { Text(opt, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        // Sources List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredSources, key = { it.id }) { src ->
                val isVerified = src.verifiedStatus.contains("Εγκεκριμένο")

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = src.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isVerified) EkabGreenContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (isVerified) "Εγκεκριμένο Πλήρες" else "Καταχωρισμένο",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isVerified) EkabOnGreenContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${src.publisher} (${src.year}) · Άδεια: ${src.license}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Επιστημονική Κάλυψη: ${src.coverage.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        if (src.url.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(src.url))
                                    context.startActivity(intent)
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Επίσημη Πηγή / Online Έκδοση ↗", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
