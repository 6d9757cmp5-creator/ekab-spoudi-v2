package com.example.mentor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.mentor.models.FacultyMember
import com.example.mentor.models.TutorMessage
import com.example.mentor.service.MentorAiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MentorTutorScreen(
    faculty: List<FacultyMember>
) {
    val aiService = remember { MentorAiService() }
    val coroutineScope = rememberCoroutineScope()
    var selectedFaculty by remember { mutableStateOf(faculty.firstOrNull()) }
    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            TutorMessage(
                sender = "mentor",
                text = "Καλώς ήρθες στον MENTOR OS! Είμαι ο προσωπικός σου AI κλινικός καθηγητής με υποστήριξη Gemini API. Μπορείς να επιλέξεις εξειδικευμένο εκπαιδευτή παραπάνω και να ρωτήσεις για ανατομία, αλγορίθμους ανακοπής, προνοσοκομειακό τραύμα ή φαρμακολογία.",
                citations = listOf("WisTech Emergency Medical Responder 2025", "ERC Guidelines 2025 (Adult ALS)")
            )
        )
    }

    LaunchedEffect(selectedFaculty) {
        selectedFaculty?.let { f ->
            messages.add(
                TutorMessage(
                    sender = "mentor",
                    text = "${f.avatar} **${f.name}** (${f.title}): ${f.greeting}",
                    citations = listOf("Επιστημονική Βιβλιοθήκη MENTOR OS (PR #83)")
                )
            )
        }
    }

    fun handleSend() {
        if (inputText.isBlank() || isGenerating) return
        val userQuery = inputText.trim()
        messages.add(TutorMessage(sender = "user", text = userQuery))
        inputText = ""
        isGenerating = true

        coroutineScope.launch {
            val placeholderIndex = messages.size
            messages.add(
                TutorMessage(
                    sender = "mentor",
                    text = "Σύνταξη επιστημονικής απάντησης...",
                    citations = listOf("Gemini 3.8 Flash · ERC Guidelines 2025")
                )
            )

            val finalResponse = aiService.generateClinicalResponse(
                query = userQuery,
                facultyName = selectedFaculty?.name ?: "Dr. Νικόλαος Καρράς",
                facultySpecialty = selectedFaculty?.specialty ?: "Επείγουσα Καρδιολογία & ALS",
                onChunk = { streamedText ->
                    if (placeholderIndex in messages.indices) {
                        messages[placeholderIndex] = messages[placeholderIndex].copy(
                            text = streamedText
                        )
                    }
                }
            )

            if (placeholderIndex in messages.indices) {
                messages[placeholderIndex] = finalResponse
            }
            isGenerating = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mentor_tutor_screen")
    ) {
        // Faculty Selector Bar
        Surface(
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Ιατρική Ομάδα AI (Faculty)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(faculty) { member ->
                        val isSelected = selectedFaculty?.id == member.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFaculty = member },
                            leadingIcon = {
                                Text(member.avatar, fontSize = 16.sp)
                            },
                            label = {
                                Text(
                                    text = member.name.take(18) + (if (member.name.length > 18) "…" else ""),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
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

        // Active Faculty Banner
        selectedFaculty?.let { fac ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = fac.avatar, fontSize = 20.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = fac.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = fac.specialty,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Chat Conversation List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            )

                            if (msg.citations.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isUser) Color.White.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📖 Επιστημονική Τεκμηρίωση:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = if (isUser) Color.White else MaterialTheme.colorScheme.primary
                                        )
                                    )
                                    msg.citations.forEach { cit ->
                                        Text(
                                            text = "• $cit",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = if (isUser) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
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

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val suggestions = listOf(
                "Ποια η δόση Αδρεναλίνης σε VF;",
                "Πώς εκτιμάται το GCS;",
                "Σημεία Πνευμοθώρακα υπό Τάση",
                "Αλγόριθμος START διαλογής"
            )
            items(suggestions) { sugg ->
                SuggestionChip(
                    onClick = {
                        inputText = sugg
                        handleSend()
                    },
                    label = { Text(sugg, fontSize = 11.sp) }
                )
            }
        }

        // Input Row
        Surface(
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                if (isGenerating) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ρωτήστε τον καθηγητή MENTOR OS...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mentor_tutor_input"),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3
                )

                IconButton(
                    onClick = { handleSend() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .testTag("mentor_tutor_send_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Αποστολή",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
}

fun generateTutorResponse(query: String, facultyId: String): TutorMessage {
    val qLower = query.lowercase()
    return when {
        qLower.contains("αδρεναλιν") || qLower.contains("ανακοπη") || qLower.contains("vf") -> {
            TutorMessage(
                sender = "mentor",
                text = "Στην καρδιακή ανακοπή ενηλίκου κατά τις κατευθυντήριες οδηγίες ERC 2025:\n\n1. **Απινιδώσιμοι ρυθμοί (VF / pVT)**: Χορηγείται **1 mg Αδρεναλίνη IV/IO** μετά την **3η απινίδωση** (μαζί με 300 mg Αμιωδαρόνη). Επαναλαμβάνεται κάθε 3-5 λεπτά (κάθε 2ο κύκλο ΚΑΡΠΑ).\n\n2. **Μη απινιδώσιμοι ρυθμοί (PEA / Ασυστολία)**: Χορηγείται **1 mg Αδρεναλίνη IV/IO ΑΜΕΣΩΣ** μόλις εξασφαλιστεί φλεβική ή ενδοοστική γραμμή.\n\n*Κλινική προσοχή:* Στην αναφυλαξία η δόση είναι 0.5 mg **ενδομυϊκά (IM)** και ποτέ IV bolus σε ασθενή με σφυγμό!",
                citations = listOf(
                    "ERC Guidelines 2025 — Adult Advanced Life Support",
                    "Open RN Nursing Pharmacology — Adrenergic Agonists",
                    "Οδηγός ΣΑΕΚ ΕΚΑΒ: Προνοσοκομειακή Χρήση Φαρμάκων"
                )
            )
        }
        qLower.contains("gcs") || qLower.contains("γλασκοβ") || qLower.contains("συνειδησ") -> {
            TutorMessage(
                sender = "mentor",
                text = "Η **Κλίμακα Γλασκόβης (GCS)** αξιολογεί τη συνείδηση από 3 έως 15 βαθμούς:\n\n• **Άνοιγμα Ματιών (Ε, 1-4)**: 4=Αυτόματο, 3=Στη φωνή, 2=Στον πόνο, 1=Καμία αντίδραση.\n• **Λεκτική Ανταπόκριση (V, 1-5)**: 5=Προσανατολισμένος, 4=Σύγχυση, 3=Ακατάλληλες λέξεις, 2=Ακατάληπτοι ήχοι, 1=Καμία.\n• **Κινητική Ανταπόκριση (M, 1-6)**: 6=Υπακούει εντολές, 5=Εντοπίζει τον πόνο, 4=Απόσυρση, 3=Παθολογική κάμψη (αποφλοίωση), 2=Έκταση (απεγκεφαλισμός), 1=Καμία.\n\n*Κανόνας ΕΚΑΒ:* **GCS ≤ 8 = Κώμα** -> Άμεση προστασία αεραγωγού και ειδοποίηση ιατρικής ομάδας.",
                citations = listOf(
                    "WisTech Emergency Medical Responder 2025 — Chapter 10: Neurological Assessment",
                    "WHO-ICRC Basic Emergency Care — Trauma & Altered Mental Status"
                )
            )
        }
        qLower.contains("πνευμοθωρακ") || qLower.contains("θωρακ") || qLower.contains("ταση") -> {
            TutorMessage(
                sender = "mentor",
                text = "Ο **Πνευμοθώρακας υπό Τάση** είναι άμεση απειλή για τη ζωή (<C>A**B**CDE):\n\n• **Μηχανισμός**: Μονόδρομη βαλβίδα αέρα στον υπεζωκότα που συμπιέζει τον πνεύμονα, παρεκτοπίζει το μεσοθωράκιο και εμποδίζει τη φλεβική επάνοδο στην καρδιά (αποφρακτικό shock).\n• **Κλινικά σημεία**: Σοβαρή δύσπνοια, υπόταση, διάταση σφαγιτίδων τραχήλου, απουσία αναπνευστικού ψιθυρίσματος ετερόπλευρα, απόκλιση τραχείας (όψιμο σημείο).\n• **Επείγουσα αντιμετώπιση**: Άμεση αποσυμπίεση με βελόνα μεγάλου διαμετρήματος (14G) στο 2ο μεσοπλεύριο διάστημα στη μεσοκλειδική γραμμή ή στο 4ο/5ο στην πρόσθια μασχαλιαία γραμμή.",
                citations = listOf(
                    "PHTLS Prehospital Trauma Life Support 10th Ed.",
                    "WisTech EMR 2025 — Thoracic Emergencies",
                    "Π.Δ. 348/1996 — Επαγγελματικές πράξεις διασωστών"
                )
            )
        }
        qLower.contains("start") || qLower.contains("διαλογ") || qLower.contains("triage") -> {
            TutorMessage(
                sender = "mentor",
                text = "Ο αλγόριθμος **START (Simple Triage and Rapid Treatment)** για μαζικά συμβάντα:\n\n1. **Περιπατητικοί**: «Όποιος μπορεί να περπατήσει να έρθει εδώ» -> **Πράσινο (P3 - Delayed)**.\n2. **Αναπνοή**:\n   • Άπνοια -> Άνοιγμα αεραγωγού -> Εάν δεν αναπνέει: **Μαύρο (P4 - Deceased)**.\n   • Εάν αρχίσει να αναπνέει ή αναπνοές > 30/min: **Κόκκινο (P1 - Immediate)**.\n3. **Κυκλοφορία**: Αναπνοές < 30/min, αλλά απών κερκιδικός σφυγμός ή τριχοειδική επαναπλήρωση > 2 sec: **Κόκκινο (P1)**.\n4. **Νευρολογικό**: Αδυνατεί να ακολουθήσει απλές εντολές: **Κόκκινο (P1)**.\n5. Εάν όλα τα παραπάνω είναι φυσιολογικά αλλά δεν μπορεί να περπατήσει: **Κίτρινο (P2 - Urgent)**.",
                citations = listOf(
                    "WHO Integrated Triage Tool 2024",
                    "PR #85 MENTOR OS Triage Activities Engine",
                    "ΣΑΕΚ ΕΚΑΒ: Διαχείριση Μαζικού Συμβάντος (Μάθημα 15)"
                )
            )
        }
        else -> {
            TutorMessage(
                sender = "mentor",
                text = "Σχετικά με το ερώτημά σου «$query»:\n\nΣτην προνοσοκομειακή φροντίδα και στην κατάρτιση του ΕΚΑΒ, ακολουθούμε πάντοτε τη δομημένη προσέγγιση **<C>ABCDE**:\n\n• **<C> Catastrophic Bleeding**: Έλεγχος μαζικής εξωτερικής αιμορραγίας (τουρνικέ, αιμοστατική γάζα).\n• **A Airway**: Εξασφάλιση αεραγωγού με σταθεροποίηση ΑΜΣΣ.\n• **B Breathing**: Εκτίμηση αναπνοών, SpO2, χορήγηση O2 εάν SpO2 < 94%.\n• **C Circulation**: Σφύξεις, ΑΠ, τριχοειδική επαναπλήρωση, έλεγχος shock.\n• **D Disability**: GCS, κόρες, μέτρηση γλυκόζης αίματος.\n• **E Exposure**: Έκθεση και προστασία από υποθερμία.\n\nΠοια παράμετρο θα ήθελες να αναλύσουμε εκτενέστερα;",
                citations = listOf(
                    "ERC Guidelines 2025 — First Aid & Basic Life Support",
                    "WisTech Emergency Medical Responder 2025",
                    "ΣΑΕΚ ΕΚΑΒ — Εισαγωγή στην Προνοσοκομειακή Εκτίμηση"
                )
            )
        }
    }
}
