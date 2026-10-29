package com.example.mentor.models

data class LibrarySource(
    val id: String,
    val title: String,
    val publisher: String,
    val year: Int,
    val license: String,
    val url: String,
    val coverage: List<String>,
    val verifiedStatus: String, // "Εγκεκριμένο", "Υπό κλινική αξιολόγηση"
    val evidenceScope: String
)

data class LiveMission(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val callIntake: CallIntake,
    val sceneSafety: MissionSceneSafety,
    val initialAssessment: MissionAssessment,
    val decisions: List<MissionDecision>,
    val handover: MissionHandover
)

data class CallIntake(
    val caller: String,
    val location: String,
    val chiefComplaint: String,
    val reportedVitals: String
)

data class MissionSceneSafety(
    val prompt: String,
    val options: List<MissionOption>
)

data class MissionOption(
    val text: String,
    val isCorrect: Boolean,
    val feedback: String
)

data class MissionAssessment(
    val airway: String,
    val breathing: String,
    val circulation: String,
    val disability: String,
    val exposure: String
)

data class MissionDecision(
    val phase: String,
    val prompt: String,
    val options: List<MissionOption>
)

data class MissionHandover(
    val format: String,
    val summary: String
)

data class MciIncident(
    val id: String,
    val title: String,
    val description: String,
    val sceneSafetyPrompt: String,
    val patients: List<MciPatient>
)

data class MciPatient(
    val id: String,
    val name: String,
    val description: String,
    val triageTag: String,
    val rationale: String,
    var assignedTag: String? = null
)

data class EquipmentItem(
    val id: String,
    val name: String,
    val category: String,
    val icon: String,
    val description: String,
    val components: List<String>,
    val indications: String,
    val safetyChecks: String,
    val protocolPoints: List<String>
)

data class EcgRhythm(
    val id: String,
    val name: String,
    val rate: String,
    val regularity: String,
    val pWave: String,
    val prInterval: String,
    val qrsDuration: String,
    val shockable: Boolean,
    val clinicalSummary: String,
    val points: List<Float>
)

data class FacultyMember(
    val id: String,
    val name: String,
    val title: String,
    val avatar: String,
    val specialty: String,
    val greeting: String
)

data class TutorMessage(
    val sender: String, // "user" or "mentor"
    val text: String,
    val citations: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

data class AnatomyOrgan(
    val id: String,
    val name: String,
    val latinName: String,
    val system: String, // "Σκελετικό", "Μυϊκό", "Σπλαχνικό", "Αγγειακό", "Νευρικό"
    val sexAvailability: String, // "Κοινό", "Ανδρικό", "Γυναικείο"
    val location: String,
    val physiologicalFunction: String,
    val clinicalTraumaCorrelation: String
)

data class ImplementationAuditItem(
    val moduleNumber: Int,
    val name: String,
    val status: String, // "Υλοποιημένο", "Ελεγμένο", "Υπό ανάπτυξη"
    val prReference: String,
    val scientificStatus: String,
    val verifiedArtifacts: String,
    val nextStep: String
)
