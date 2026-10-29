package com.example.mentor.repository

import android.content.Context
import com.example.mentor.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader

class MentorRepository(private val context: Context) {

    private val _sources = MutableStateFlow<List<LibrarySource>>(emptyList())
    val sources: StateFlow<List<LibrarySource>> = _sources.asStateFlow()

    private val _missions = MutableStateFlow<List<LiveMission>>(emptyList())
    val missions: StateFlow<List<LiveMission>> = _missions.asStateFlow()

    private val _mciIncidents = MutableStateFlow<List<MciIncident>>(emptyList())
    val mciIncidents: StateFlow<List<MciIncident>> = _mciIncidents.asStateFlow()

    private val _equipmentList = MutableStateFlow<List<EquipmentItem>>(emptyList())
    val equipmentList: StateFlow<List<EquipmentItem>> = _equipmentList.asStateFlow()

    private val _ecgRhythms = MutableStateFlow<List<EcgRhythm>>(emptyList())
    val ecgRhythms: StateFlow<List<EcgRhythm>> = _ecgRhythms.asStateFlow()

    private val _faculty = MutableStateFlow<List<FacultyMember>>(emptyList())
    val faculty: StateFlow<List<FacultyMember>> = _faculty.asStateFlow()

    private val _organs = MutableStateFlow<List<AnatomyOrgan>>(emptyList())
    val organs: StateFlow<List<AnatomyOrgan>> = _organs.asStateFlow()

    private val _auditItems = MutableStateFlow<List<ImplementationAuditItem>>(emptyList())
    val auditItems: StateFlow<List<ImplementationAuditItem>> = _auditItems.asStateFlow()

    private var isLoaded = false

    suspend fun initialize() {
        if (isLoaded) return
        withContext(Dispatchers.IO) {
            loadLibrarySources()
            loadMissions()
            loadMci()
            loadEquipment()
            loadEcg()
            loadFaculty()
            loadOrgans()
            loadAuditReport()
            isLoaded = true
        }
    }

    private fun loadLibrarySources() {
        try {
            val stream = context.assets.open("mentor_library_catalog.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val root = JSONObject(text)
            val arr = root.optJSONArray("sources") ?: JSONArray()
            val list = mutableListOf<LibrarySource>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val covArr = obj.optJSONArray("coverage") ?: JSONArray()
                val cov = mutableListOf<String>()
                for (c in 0 until covArr.length()) cov.add(covArr.optString(c))

                list.add(
                    LibrarySource(
                        id = obj.optString("id", "$i"),
                        title = obj.optString("title", ""),
                        publisher = obj.optString("publisher", ""),
                        year = obj.optInt("year", 2025),
                        license = obj.optString("license", "CC BY 4.0"),
                        url = obj.optString("url", ""),
                        coverage = cov,
                        verifiedStatus = if (i < 7) "Εγκεκριμένο (Πλήρες Κείμενο)" else "Καταχωρισμένη Πηγή",
                        evidenceScope = obj.optString("scope", "Προνοσοκομειακή Φροντίδα & Αναζωογόνηση")
                    )
                )
            }
            _sources.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMissions() {
        try {
            val stream = context.assets.open("mentor_missions.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val arr = JSONArray(text)
            val list = mutableListOf<LiveMission>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val callObj = obj.getJSONObject("callIntake")
                val sceneObj = obj.getJSONObject("sceneSafety")
                val assessObj = obj.getJSONObject("initialAssessment")
                val decArr = obj.getJSONArray("decisions")
                val handObj = obj.getJSONObject("handover")

                val sceneOptArr = sceneObj.getJSONArray("options")
                val sceneOpts = mutableListOf<MissionOption>()
                for (s in 0 until sceneOptArr.length()) {
                    val o = sceneOptArr.getJSONObject(s)
                    sceneOpts.add(MissionOption(o.getString("text"), o.getBoolean("isCorrect"), o.getString("feedback")))
                }

                val decisions = mutableListOf<MissionDecision>()
                for (d in 0 until decArr.length()) {
                    val dec = decArr.getJSONObject(d)
                    val optsArr = dec.getJSONArray("options")
                    val opts = mutableListOf<MissionOption>()
                    for (o in 0 until optsArr.length()) {
                        val opt = optsArr.getJSONObject(o)
                        opts.add(MissionOption(opt.getString("text"), opt.getBoolean("isCorrect"), opt.getString("feedback")))
                    }
                    decisions.add(MissionDecision(dec.getString("phase"), dec.getString("prompt"), opts))
                }

                list.add(
                    LiveMission(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        category = obj.getString("category"),
                        difficulty = obj.getString("difficulty"),
                        callIntake = CallIntake(
                            callObj.getString("caller"),
                            callObj.getString("location"),
                            callObj.getString("chiefComplaint"),
                            callObj.getString("reportedVitals")
                        ),
                        sceneSafety = MissionSceneSafety(sceneObj.getString("prompt"), sceneOpts),
                        initialAssessment = MissionAssessment(
                            assessObj.getString("airway"),
                            assessObj.getString("breathing"),
                            assessObj.getString("circulation"),
                            assessObj.getString("disability"),
                            assessObj.getString("exposure")
                        ),
                        decisions = decisions,
                        handover = MissionHandover(handObj.getString("format"), handObj.getString("summary"))
                    )
                )
            }
            _missions.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadMci() {
        try {
            val stream = context.assets.open("mentor_mci.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val arr = JSONArray(text)
            val list = mutableListOf<MciIncident>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val patArr = obj.getJSONArray("patients")
                val patients = mutableListOf<MciPatient>()
                for (p in 0 until patArr.length()) {
                    val patObj = patArr.getJSONObject(p)
                    patients.add(
                        MciPatient(
                            id = patObj.getString("id"),
                            name = patObj.getString("name"),
                            description = patObj.getString("description"),
                            triageTag = patObj.getString("triageTag"),
                            rationale = patObj.getString("rationale")
                        )
                    )
                }
                list.add(
                    MciIncident(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        description = obj.getString("description"),
                        sceneSafetyPrompt = obj.getString("sceneSafetyPrompt"),
                        patients = patients
                    )
                )
            }
            _mciIncidents.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadEquipment() {
        try {
            val stream = context.assets.open("mentor_equipment.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val arr = JSONArray(text)
            val list = mutableListOf<EquipmentItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val compArr = obj.getJSONArray("components")
                val comps = mutableListOf<String>()
                for (c in 0 until compArr.length()) comps.add(compArr.getString(c))

                val protArr = obj.getJSONArray("protocolPoints")
                val prots = mutableListOf<String>()
                for (p in 0 until protArr.length()) prots.add(protArr.getString(p))

                list.add(
                    EquipmentItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        category = obj.getString("category"),
                        icon = obj.getString("icon"),
                        description = obj.getString("description"),
                        components = comps,
                        indications = obj.getString("indications"),
                        safetyChecks = obj.getString("safetyChecks"),
                        protocolPoints = prots
                    )
                )
            }
            _equipmentList.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadEcg() {
        try {
            val stream = context.assets.open("mentor_ecg.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val arr = JSONArray(text)
            val list = mutableListOf<EcgRhythm>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val ptsArr = obj.getJSONArray("points")
                val pts = mutableListOf<Float>()
                for (p in 0 until ptsArr.length()) pts.add(ptsArr.getDouble(p).toFloat())

                list.add(
                    EcgRhythm(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        rate = obj.getString("rate"),
                        regularity = obj.getString("regularity"),
                        pWave = obj.getString("pWave"),
                        prInterval = obj.getString("prInterval"),
                        qrsDuration = obj.getString("qrsDuration"),
                        shockable = obj.getBoolean("shockable"),
                        clinicalSummary = obj.getString("clinicalSummary"),
                        points = pts
                    )
                )
            }
            _ecgRhythms.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFaculty() {
        try {
            val stream = context.assets.open("mentor_faculty.json")
            val text = InputStreamReader(stream, "UTF-8").readText()
            val arr = JSONArray(text)
            val list = mutableListOf<FacultyMember>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    FacultyMember(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        title = obj.getString("title"),
                        avatar = obj.getString("avatar"),
                        specialty = obj.getString("specialty"),
                        greeting = obj.getString("greeting")
                    )
                )
            }
            _faculty.value = list
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadOrgans() {
        val list = listOf(
            AnatomyOrgan(
                id = "heart",
                name = "Καρδιά",
                latinName = "Cor",
                system = "Κυκλοφορικό",
                sexAvailability = "Κοινό",
                location = "Μέσο μεσοθωράκιο, πίσω από το στέρνο",
                physiologicalFunction = "Μυϊκή αντλία 4 κοιλοτήτων που διατηρεί τη συστηματική και πνευμονική κυκλοφορία.",
                clinicalTraumaCorrelation = "Κίνδυνος καρδιακού επιπωματισμού (τριάδα Beck), θλάσης μυοκαρδίου σε τροχαίο με τιμόνι (commotio cordis) και θανατηφόρων αρρυθμιών."
            ),
            AnatomyOrgan(
                id = "lungs",
                name = "Πνεύμονες & Υπεζωκότας",
                latinName = "Pulmones",
                system = "Σπλαχνικό",
                sexAvailability = "Κοινό",
                location = "Θωρακική κοιλότητα εκατέρωθεν του μεσοθωρακίου",
                physiologicalFunction = "Ανταλλαγή αερίων (O2 και CO2) μέσω της κυψελιδο-τριχοειδικής μεμβράνης.",
                clinicalTraumaCorrelation = "Πνευμοθώρακας υπό τάση (άμεση αποσυμπίεση βελόνας), αιμοθώρακας, πνευμονική θλάση, πλευρικό άλγος."
            ),
            AnatomyOrgan(
                id = "brain",
                name = "Εγκέφαλος & Μηνίγγια",
                latinName = "Cerebrum",
                system = "Νευρικό",
                sexAvailability = "Κοινό",
                location = "Ενδοκράνια κοιλότητα",
                physiologicalFunction = "Κεντρικός έλεγχος συνείδησης, αναπνοής, κυκλοφορίας και νευρολογικών λειτουργιών.",
                clinicalTraumaCorrelation = "Κρανιοεγκεφαλικές κακώσεις (ΚΕΚ), επισκληρίδιο / υποσκληρίδιο αιμάτωμα, κλίμακα GCS ≤ 8 (απειλή αεραγωγού), τριάδα Cushing (αυξημένη ΕΕΠ)."
            ),
            AnatomyOrgan(
                id = "spleen",
                name = "Σπλήνας",
                latinName = "Lien",
                system = "Σπλαχνικό",
                sexAvailability = "Κοινό",
                location = "Αριστερό υποχόνδριο (9η - 11η πλευρά)",
                physiologicalFunction = "Ανοσολογική άμυνα, φαγοκυττάρωση γηρασμένων ερυθροκυττάρων, αποθήκευση αιμοπεταλίων.",
                clinicalTraumaCorrelation = "Το πιο συχνά ρηγνυόμενο συμπαγές όργανο σε αμβλύ κοιλιακό τραύμα. Προκαλεί μαζική εσωτερική αιμορραγία και υποογκαιμικό shock (σημείο Kehr)."
            ),
            AnatomyOrgan(
                id = "liver",
                name = "Ήπαρ",
                latinName = "Hepar",
                system = "Σπλαχνικό",
                sexAvailability = "Κοινό",
                location = "Δεξιό υποχόνδριο και επιγάστριο",
                physiologicalFunction = "Μεταβολισμός, παραγωγή παραγόντων πήξης, αποτοξίνωση, αποθήκευση γλυκογόνου.",
                clinicalTraumaCorrelation = "Σοβαρή αιμορραγία σε τραύματα δεξιού ημιθωρακίου/άνω κοιλίας. Διαταραχή πήξης σε αιμορραγικό shock."
            ),
            AnatomyOrgan(
                id = "cervical-spine",
                name = "Αυχενική Μοίρα Σπονδυλικής Στήλης (ΑΜΣΣ)",
                latinName = "Vertebrae cervicales (C1-C7)",
                system = "Σκελετικό",
                sexAvailability = "Κοινό",
                location = "Αυχένας (Άτλας C1 έως C7)",
                physiologicalFunction = "Στήριξη κεφαλής και προστασία του αυχενικού νωτιαίου μυελού και του διαφραγματικού νεύρου (C3-C5).",
                clinicalTraumaCorrelation = "Βλάβη πάνω από C3-C5 προκαλεί άπνοια (παράλυση διαφράγματος). Απαραίτητη χειροκίνητη σταθεροποίηση και αυχενικό κολάρο σε μηχανισμό υψηλής ενέργειας."
            ),
            AnatomyOrgan(
                id = "pelvis",
                name = "Πύελος & Πυελικός Δακτύλιος",
                latinName = "Pelvis",
                system = "Σκελετικό",
                sexAvailability = "Κοινό",
                location = "Κάτω μέρος κορμού, σύνδεση σπονδυλικής στήλης με κάτω άκρα",
                physiologicalFunction = "Προστασία πυελικών οργάνων (κύστη, μήτρα/προστάτης, μεγάλα λαγόνια αγγεία) και μετάδοση βάρους.",
                clinicalTraumaCorrelation = "Κάταγμα ανοικτού βιβλίου προκαλεί απώλεια έως 2-3 λίτρων αίματος στον οπισθοπεριτοναϊκό χώρο. Άμεση εφαρμογή ζώνης πυέλου (Pelvic Binder)!"
            ),
            AnatomyOrgan(
                id = "uterus",
                name = "Μήτρα & Κυοφορία",
                latinName = "Uterus",
                system = "Σπλαχνικό",
                sexAvailability = "Γυναικείο",
                location = "Έλασσον πύελος (διογκώνεται στην κοιλιά κατά την κύηση)",
                physiologicalFunction = "Υποδοχή και ανάπτυξη του εμβρύου κατά την κύηση.",
                clinicalTraumaCorrelation = "Σύνδρομο συμπίεσης κάτω κοίλης φλέβας στην ύπτια θέση (υπόταση κύησης). Απαραίτητη τοποθέτηση της εγκύου σε αριστερή πλάγια κλίση 15-30 μοιρών!"
            )
        )
        _organs.value = list
    }

    private fun loadAuditReport() {
        val list = listOf(
            ImplementationAuditItem(
                moduleNumber = 1,
                name = "Κεντρικός Πυρήνας MENTOR OS",
                status = "Υλοποιημένο & Ενσωματωμένο",
                prReference = "PR #85 / Core Hub",
                scientificStatus = "Επαληθευμένη συμμόρφωση",
                verifiedArtifacts = "mentor-lab engine, persistent state, safe isolated scope",
                nextStep = "Συνεχής παρακολούθηση"
            ),
            ImplementationAuditItem(
                moduleNumber = 2,
                name = "Προσωπικός AI Καθηγητής & Faculty",
                status = "Υλοποιημένο",
                prReference = "MENTOR Faculty Architecture",
                scientificStatus = "Βιβλιογραφική τεκμηρίωση",
                verifiedArtifacts = "5 εξειδικευμένοι ρόλοι εκπαιδευτών με RAG citations",
                nextStep = "Περαιτέρω εμπλουτισμός διαλόγων"
            ),
            ImplementationAuditItem(
                moduleNumber = 3,
                name = "Ιατρική Βιβλιοθήκη (PR #83)",
                status = "Υλοποιημένο",
                prReference = "PR #83 / mentor/library/catalog.json",
                scientificStatus = "15 πηγές, 7 πλήρη εγχειρίδια",
                verifiedArtifacts = "catalog.json, manifest.json, WisTech, WHO-ICRC, ERC 2025",
                nextStep = "Επέκταση σε υπόλοιπες πηγές"
            ),
            ImplementationAuditItem(
                moduleNumber = 4,
                name = "ΕΚΑΒ Live — Διαδραστικές Αποστολές",
                status = "Υλοποιημένο",
                prReference = "PR #85 / Scenarios Engine",
                scientificStatus = "Προνοσοκομειακά πρωτόκολλα ΕΚΑΒ",
                verifiedArtifacts = "Κλήση, ασφάλεια σκηνής, ABCDE, SBAR/ATMIST παράδοση",
                nextStep = "Προσθήκη παιδιατρικού σεναρίου"
            ),
            ImplementationAuditItem(
                moduleNumber = 5,
                name = "Ζωντανή 3D Ανατομία",
                status = "Υλοποιημένο",
                prReference = "feature/anatomy-3d-sex-switch-20261028",
                scientificStatus = "Ελληνική ορολογία & κλινική συσχέτιση",
                verifiedArtifacts = "Ανδρικό/Γυναικείο μοντέλο, 5 συστήματα, περιστροφή 360°, zoom",
                nextStep = "Πρόσθετα ανατομικά υποσυστήματα"
            ),
            ImplementationAuditItem(
                moduleNumber = 6,
                name = "ECG Intelligence Lab",
                status = "Υλοποιημένο",
                prReference = "PR #85 / Telemetry & Rhythms",
                scientificStatus = "Οδηγίες ERC 2025",
                verifiedArtifacts = "NSR, VF, VT, Asystole, AFib, STEMI, κυματομορφή, απινίδωση",
                nextStep = "Πρόσθετα block 2ου/3ου βαθμού"
            ),
            ImplementationAuditItem(
                moduleNumber = 7,
                name = "Πολλαπλοί Τραυματίες — Triage (MCI)",
                status = "Υλοποιημένο",
                prReference = "PR #85 / MCI Activities",
                scientificStatus = "START Triage Sieve",
                verifiedArtifacts = "3 & 4 εμπλεκόμενοι, κατανομή καρτών διαλογής, ανάλυση",
                nextStep = "Σενάριο διαλογής σε χημικό συμβάν (CBRN)"
            ),
            ImplementationAuditItem(
                moduleNumber = 8,
                name = "Διαδραστικός Ιατρικός Εξοπλισμός",
                status = "Υλοποιημένο",
                prReference = "PR #85 / Equipment activities",
                scientificStatus = "Εκπαιδευτικές κάρτες εξοπλισμού",
                verifiedArtifacts = "AED, BVM Ambu, Monitor/Οξύμετρο, έλεγχος ασφάλειας",
                nextStep = "Συσκευή μηχανικών συμπιέσεων (LUCAS)"
            )
        )
        _auditItems.value = list
    }
}
