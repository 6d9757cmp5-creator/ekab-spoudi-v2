package com.example.mentor.service

import android.util.Log
import com.example.mentor.models.TutorMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout

private const val TAG = "MentorAiService"

class MentorAiService {

    // Lazy initialization as mandated by firebase-ai-logic-android
    private val model by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    suspend fun generateClinicalResponse(
        query: String,
        facultyName: String,
        facultySpecialty: String,
        onChunk: (String) -> Unit
    ): TutorMessage {
        val prompt = buildString {
            appendLine("Είσαι ο/η $facultyName, $facultySpecialty στο εκπαιδευτικό σύστημα MENTOR OS του ΕΚΑΒ (ΣΑΕΚ & ΕΟΠΠΕΠ).")
            appendLine("Απάντησε στα Ελληνικά με απόλυτη επιστημονική ακρίβεια, αυστηρή συμμόρφωση με τις κατευθυντήριες οδηγίες ERC 2025 (European Resuscitation Council), τις προδιαγραφές PHTLS, και το νομικό πλαίσιο καθηκόντων διασωστών (Π.Δ. 348/96 & 62/07).")
            appendLine("Ερώτηση μαθητή: $query")
            appendLine("Δομή απάντησης:")
            appendLine("1. Σύντομη, σαφής κλινική σύνοψη (σε bullet points).")
            appendLine("2. Αλγόριθμος αντιμετώπισης κατά <C>ABCDE ή ERC 2025.")
            appendLine("3. Σημεία SOS για τις εξετάσεις πιστοποίησης ΕΟΠΠΕΠ.")
            appendLine("4. Επιστημονική τεκμηρίωση (π.χ. WisTech EMR 2025, ERC 2025, WHO-ICRC, Open RN).")
        }

        return try {
            val accumulated = StringBuilder()
            withTimeout(60_000L) {
                model.generateContentStream(prompt).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    accumulated.append(text)
                    onChunk(accumulated.toString())
                }
            }

            val finalText = accumulated.toString()
            if (finalText.isNotBlank()) {
                TutorMessage(
                    sender = "mentor",
                    text = finalText,
                    citations = listOf(
                        "Gemini AI (Firebase AI Logic) — $facultyName",
                        "ERC Guidelines 2025 & Π.Δ. 348/96",
                        "Ιατρική Βιβλιοθήκη MENTOR OS (PR #83)"
                    )
                )
            } else {
                fallbackLocalResponse(query)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini inference failed, falling back to clinical rules", e)
            fallbackLocalResponse(query)
        }
    }

    private fun fallbackLocalResponse(query: String): TutorMessage {
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
}
