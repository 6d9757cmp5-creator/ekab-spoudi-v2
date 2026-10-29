package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FlashcardEntity::class], version = 1, exportSchema = false)
abstract class EkabDatabase : RoomDatabase() {

    abstract fun flashcardDao(): FlashcardDao

    companion object {
        @Volatile
        private var INSTANCE: EkabDatabase? = null

        fun getInstance(context: Context): EkabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EkabDatabase::class.java,
                    "ekab_study.db"
                ).addCallback(DatabaseCallback()).build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateFlashcards(database.flashcardDao())
                    }
                }
            }
        }

        suspend fun prepopulateFlashcards(dao: FlashcardDao) {
            val starterCards = listOf(
                FlashcardEntity(
                    term = "Αδρεναλίνη — Καρδιακή Ανακοπή",
                    definition = "1 mg IV/IO κάθε 3-5 λεπτά. Στο ALS: άμεσα σε μη απινιδώσιμους ρυθμούς (PEA / Ασυστολία) και μετά την 3η απινίδωση σε VF / pVT.",
                    category = "Φαρμακολογία",
                    hint = "Δόση ALS για ανακοπή"
                ),
                FlashcardEntity(
                    term = "Αδρεναλίνη — Αναφυλαξία",
                    definition = "0.5 mg IM (ενδομυϊκά 1:1.000) στην πρόσθια-πλάγια επιφάνεια του μέσου μηρού. Επανάληψη σε 5 λεπτά επί εμμονής συμπτωμάτων.",
                    category = "Φαρμακολογία",
                    hint = "Οδός IM και θέση"
                ),
                FlashcardEntity(
                    term = "Αμιωδαρόνη — ALS",
                    definition = "Αντιαρρυθμικό κατηγορίας III. 300 mg IV/IO bolus μετά την 3η απινίδωση σε εμμένουσα VF/pVT. Επιπλέον δόση 150 mg μετά την 5η απινίδωση.",
                    category = "Φαρμακολογία",
                    hint = "3η και 5η απινίδωση"
                ),
                FlashcardEntity(
                    term = "Ατροπίνη",
                    definition = "Παρασυμπαθολυτικό. 500 mcg (0.5 mg) IV σε συμπτωματική βραδυκαρδία με σημεία κινδύνου (υπόταση, συγκοπή, ισχαιμία). Μέγιστη δόση 3 mg.",
                    category = "Φαρμακολογία",
                    hint = "Συμπτωματική βραδυκαρδία"
                ),
                FlashcardEntity(
                    term = "Ναλοξόνη (Narcan)",
                    definition = "Ανταγωνιστής οπιοειδών. 400 mcg IV/IM ή ενδορινικά (IN). Τιτλοποίηση για αποκατάσταση επαρκούς αυτόματης αναπνοής και όχι απαραίτητα πλήρους εγρήγορσης.",
                    category = "Φαρμακολογία",
                    hint = "Υπερδοσολογία οπιοειδών"
                ),
                FlashcardEntity(
                    term = "Σαλβουταμόλη (Aerolin)",
                    definition = "β2-αδρενεργικός διεγέρτης. 5 mg νεφελοποίηση με ροή O₂ 6-8 L/min σε οξύ βρογχόσπασμο (άσθμα, ΧΑΠ). Συνδυασμός με Ιπρατρόπιο (Atrovent 0.5 mg).",
                    category = "Φαρμακολογία",
                    hint = "Νεφελοποίηση σε βρογχόσπασμο"
                ),
                FlashcardEntity(
                    term = "Κλίμακα Γλασκόβης (GCS)",
                    definition = "Βαθμολόγηση επιπέδου συνείδησης (3-15):\n• Μάτια (1-4)\n• Ομιλία (1-5)\n• Κινητικότητα (1-6)\nGCS ≤ 8 ορίζει σοβαρή εγκεφαλική βλάβη και ένδειξη εξασφάλισης αεραγωγού.",
                    category = "Ιατρική Ορολογία",
                    hint = "Μ-Ο-Κ (4-5-6)"
                ),
                FlashcardEntity(
                    term = "Πνευμοθώρακας υπό Τάση",
                    definition = "Μονόδρομη βαλβίδα αέρα στον υπεζωκότα. Σημεία: αναπνευστική δυσχέρεια, ετερόπλευρη απουσία αναπνευστικού ψιθυρίσματος, υπόταση, απόκλιση τραχείας. Άμεση αποσυμπίεση με βελόνα.",
                    category = "Τραύμα & ALS",
                    hint = "Αποσυμπίεση βελόνας"
                ),
                FlashcardEntity(
                    term = "Τριάδα του Beck (Καρδιακός Επιπωματισμός)",
                    definition = "1. Αρτηριακή υπόταση\n2. Διάταση σφαγιτίδων φλεβών τραχήλου\n3. Υπόκωφοι / απόμακροι καρδιακοί τόνοι.",
                    category = "Τραύμα & ALS",
                    hint = "Υπόταση, σφαγίτιδες, τόνοι"
                ),
                FlashcardEntity(
                    term = "FAST (Αγγειακό Εγκεφαλικό Επεισόδιο)",
                    definition = "F (Face): Πτώση γωνίας στόματος\nA (Arms): Αδυναμία έκτασης χεριού\nS (Speech): Δυσαρθρία ή αφασία\nT (Time): Χρόνος έναρξης και άμεση διακομιδή σε κέντρο εγκεφαλικών.",
                    category = "Ιατρική Ορολογία",
                    hint = "Face, Arms, Speech, Time"
                ),
                FlashcardEntity(
                    term = "Κοιλιακή Μαρμαρυγή (VF) & Άσφυγμη VT",
                    definition = "Απινιδώσιμοι ρυθμοί ανακοπής. Άμεση απινίδωση (150-200 J διφασικά), συνεχής ΚΑΡΠΑ 30:2 με ελάχιστες διακοπές (<5 sec) πριν και μετά την εκφόρτιση.",
                    category = "Τραύμα & ALS",
                    hint = "Απινιδώσιμοι ρυθμοί"
                ),
                FlashcardEntity(
                    term = "Υποογκαιμικό Shock (Στάδια)",
                    definition = "Στάδιο 1: απώλεια <15%\nΣτάδιο 2: 15-30% (ταχυκαρδία, πίεση παλμού ↓)\nΣτάδιο 3: 30-40% (υπόταση, ολιγουρία)\nΣτάδιο 4: >40% (λήθαργος, απειλητική καταπληξία).",
                    category = "Τραύμα & ALS",
                    hint = "Απώλεια όγκου αίματος"
                ),
                FlashcardEntity(
                    term = "Χειρισμός Sellick",
                    definition = "Πίεση στον κρικοειδή χόνδρο προς τα πίσω κατά τη διασωλήνωση για απόφραξη του οισοφάγου και πρόληψη εισρόφησης γαστρικού περιεχομένου.",
                    category = "Ιατρική Ορολογία",
                    hint = "Κρικοειδής πίεση"
                ),
                FlashcardEntity(
                    term = "Αντιστρέψιμα Αίτια Ανακοπής (4H & 4T)",
                    definition = "4H: Υποξία (Hypoxia), Υποογκαιμία (Hypovolemia), Υπερ/Υποκαλιαιμία & μεταβολικά (Hypo/Hyperkalemia), Υποθερμία (Hypothermia).\n4T: Πνευμοθώρακας υπό τάση (Tension pneumothorax), Επιπωματισμός (Tamponade), Τοξίνες (Toxins), Θρόμβωση (Thrombosis - PE/MI).",
                    category = "Τραύμα & ALS",
                    hint = "4H και 4T"
                )
            )
            dao.insertAll(starterCards)
        }
    }
}
