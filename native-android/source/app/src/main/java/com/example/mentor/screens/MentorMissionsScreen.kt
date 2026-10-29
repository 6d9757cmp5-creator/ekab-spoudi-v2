package com.example.mentor.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.example.mentor.models.LiveMission
import com.example.ui.theme.*

@Composable
fun MentorMissionsScreen(
    missions: List<LiveMission>
) {
    var selectedMission by remember { mutableStateOf<LiveMission?>(null) }
    var currentPhase by remember { mutableStateOf(0) } // 0: Dispatch, 1: Scene Safety, 2: ABCDE, 3: Decisions, 4: Handover
    var decisionAnswers by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var sceneAnswer by remember { mutableStateOf<Int?>(null) }

    if (selectedMission == null) {
        // Mission Selection Screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mentor_missions_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EkabRed,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Emergency,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ΕΚΑΒ Live — Αποστολές",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Διαδραστικός Προσομοιωτής Περιστατικών Ασθενοφόρου",
                                    style = MaterialTheme.typography.bodySmall.copy(color = EkabRed)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Αναλάβετε την πλήρη διαχείριση πραγματικών προνοσοκομειακών αποστολών: Λήψη κλήσης από το 166, ασφάλεια σκηνής, εκτίμηση <C>ABCDE, κρίσιμες αποφάσεις και παράδοση SBAR.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Διαθέσιμα Εκπαιδευτικά Σενάρια",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            itemsIndexed(missions) { index, mission ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedMission = mission
                            currentPhase = 0
                            sceneAnswer = null
                            decisionAnswers.clear()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EkabNavy,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${index + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = mission.category,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (mission.difficulty == "Προχωρημένο") EkabRedContainer else EkabAmberContainer
                                ) {
                                    Text(
                                        text = mission.difficulty,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (mission.difficulty == "Προχωρημένο") EkabOnRedContainer else EkabOnAmberContainer,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mission.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mission.callIntake.chiefComplaint,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                maxLines = 2
                            )
                        }

                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Έναρξη",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        // Active Mission Simulation
        val mission = selectedMission!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mission_simulation_view")
        ) {
            // Header Bar
            Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = mission.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Φάση ${currentPhase + 1} από 5 · ${getPhaseTitle(currentPhase)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }

                    IconButton(onClick = { selectedMission = null }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Έξοδος από την αποστολή")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (currentPhase) {
                    0 -> {
                        // PHASE 1: DISPATCH CALL INTAKE (166)
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EkabRed
                                        ) {
                                            Text(
                                                text = "ΚΛΗΣΗ 166",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Text(
                                            text = "Κέντρο Επιχειρήσεων ΕΚΑΒ",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))
                                    MissionInfoRow(label = "Καλών", value = mission.callIntake.caller)
                                    MissionInfoRow(label = "Τοποθεσία", value = mission.callIntake.location)
                                    MissionInfoRow(label = "Κύρια Ενόχληση", value = mission.callIntake.chiefComplaint)
                                    MissionInfoRow(label = "Αναφερθείσα Εικόνα", value = mission.callIntake.reportedVitals)
                                }
                            }
                        }
                    }

                    1 -> {
                        // PHASE 2: SCENE SAFETY
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "1 · Εκτίμηση Ασφάλειας Σκηνής",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = mission.sceneSafety.prompt,
                                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    mission.sceneSafety.options.forEachIndexed { idx, opt ->
                                        val isSelected = sceneAnswer == idx
                                        val isAnswered = sceneAnswer != null

                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = when {
                                                    !isAnswered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                    opt.isCorrect -> EkabGreenContainer
                                                    isSelected -> EkabRedContainer
                                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                                }
                                            ),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable(enabled = !isAnswered) {
                                                    sceneAnswer = idx
                                                }
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Text(
                                                    text = opt.text,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                                )
                                                if (isAnswered && (isSelected || opt.isCorrect)) {
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Text(
                                                        text = opt.feedback,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            color = if (opt.isCorrect) EkabOnGreenContainer else EkabOnRedContainer
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

                    2 -> {
                        // PHASE 3: PRIMARY ABCDE ASSESSMENT
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "2 · Πρωτογενής Εκτίμηση (<C>ABCDE)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    AbcdeCard(letter = "A", name = "Airway (Αεραγωγός & ΑΜΣΣ)", detail = mission.initialAssessment.airway)
                                    AbcdeCard(letter = "B", name = "Breathing (Αναπνοή & SpO2)", detail = mission.initialAssessment.breathing)
                                    AbcdeCard(letter = "C", name = "Circulation (Κυκλοφορία & ΑΠ)", detail = mission.initialAssessment.circulation)
                                    AbcdeCard(letter = "D", name = "Disability (Νευρολογικό & GCS)", detail = mission.initialAssessment.disability)
                                    AbcdeCard(letter = "E", name = "Exposure (Έκθεση & Θερμοκρασία)", detail = mission.initialAssessment.exposure)
                                }
                            }
                        }
                    }

                    3 -> {
                        // PHASE 4: CLINICAL DECISIONS
                        itemsIndexed(mission.decisions) { dIdx, dec ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = dec.phase,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = dec.prompt,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val chosenOpt = decisionAnswers[dIdx]
                                    dec.options.forEachIndexed { oIdx, opt ->
                                        val isSelected = chosenOpt == oIdx
                                        val isAnswered = chosenOpt != null

                                        Card(
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = when {
                                                    !isAnswered -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                    opt.isCorrect -> EkabGreenContainer
                                                    isSelected -> EkabRedContainer
                                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                                }
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable(enabled = !isAnswered) {
                                                    decisionAnswers = (decisionAnswers + (dIdx to oIdx)).toMutableMap()
                                                }
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text(text = opt.text, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                                                if (isAnswered && (isSelected || opt.isCorrect)) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = opt.feedback,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            color = if (opt.isCorrect) EkabOnGreenContainer else EkabOnRedContainer
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

                    4 -> {
                        // PHASE 5: SBAR / ATMIST HANDOVER
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Text(
                                        text = "Δομημένη Παράδοση Περιστατικού (${mission.handover.format})",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Η παράδοση στον ιατρό του ΤΕΠ πρέπει να είναι σαφής, σύντομη και να περιλαμβάνει κατάσταση, υπόβαθρο, εκτίμηση και προτάσεις:",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                            .padding(14.dp)
                                    ) {
                                        Text(
                                            text = mission.handover.summary,
                                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Phase Navigation Footer
            Surface(tonalElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentPhase > 0) currentPhase--
                        },
                        enabled = currentPhase > 0,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Προηγούμενο")
                    }

                    if (currentPhase < 4) {
                        Button(
                            onClick = { currentPhase++ },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Επόμενη Φάση")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = { selectedMission = null },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EkabGreen)
                        ) {
                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ολοκλήρωση Αποστολής")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MissionInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
fun AbcdeCard(letter: String, name: String, detail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = EkabNavy,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = letter, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = detail, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
    }
}

fun getPhaseTitle(phase: Int): String {
    return when (phase) {
        0 -> "Λήψη Κλήσης (166)"
        1 -> "Ασφάλεια Σκηνής"
        2 -> "Εκτίμηση <C>ABCDE"
        3 -> "Κλινικές Παρεμβάσεις"
        4 -> "Παράδοση SBAR"
        else -> ""
    }
}
