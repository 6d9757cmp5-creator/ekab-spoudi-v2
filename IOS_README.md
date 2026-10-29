# ΕΚΑΒ Σπουδή V2 — iPhone (.ipa)

Η web εφαρμογή [ΕΚΑΒ Σπουδή V2](https://6d9757cmp5-creator.github.io/ekab-spoudi-v2/) παραμένει το βασικό έργο. Αυτός ο **ξεχωριστός** iOS κώδικας δημιουργεί native κέλυφος WKWebView γύρω από την ίδια δημόσια σελίδα, όχι μεταγλώττιση του αρχικού Android Kotlin / Jetpack Compose. Περιλαμβάνει το web περιεχόμενο στο bundle ως εναλλακτικό αντίγραφο όταν η ιστοσελίδα δεν φορτώνει. Η offline λειτουργία πρέπει να δοκιμαστεί σε συσκευή και μπορεί να αποθηκεύει την πρόοδο χωριστά από τη σύνδεση online.

## Αυτοματοποιημένο unsigned IPA

Εκτέλεσε GitHub Actions → **Build EKAB Spoudi V2 iOS IPA (unsigned)**. Mac runner + XcodeGen δημιουργούν iOS project, μεταγλωττίζουν το `.app` για πραγματική συσκευή iPhone και δημιουργούν το ZIP-format `Payload/EKABSpoudiV2.app` με επέκταση `.ipa`. Το αρχείο λαμβάνεται από τα **Artifacts** της επιτυχημένης εκτέλεσης.

**Σημαντικό:** unsigned `.ipa` **δεν εγκαθίσταται απευθείας** με απλό πάτημα στο iPhone. Απαιτείται να υπογραφεί με έγκυρο Apple provisioning profile και πιστοποιητικό. Μπορεί να επαναϋπογραφεί με εργαλείο sideloading όπως Sideloadly ή SideStore, όπου υποστηρίζεται. Για διανομή μέσω TestFlight / App Store απαιτείται αντίστοιχη διαδικασία Apple Developer και υπογεγραμμένο build. Δεν έχει διαπιστωθεί πρόσβαση στα κλειδιά/πιστοποιητικά χρήστη και το workflow ΔΕΝ χρησιμοποιεί secrets ούτε υπογράφει εφαρμογές.

## Διαδικασία με Xcode

Σε Mac με Xcode και XcodeGen: αντέγραψε τα web αρχεία στον `ios/WebAssets` (μαζί με `assets`), δημιούργησε `ios/Assets.xcassets/AppIcon.appiconset`, `xcodegen generate --spec project.yml`, άνοιξε `EKABSpoudiV2.xcodeproj`. Ρύθμισε δικό σου Apple Development Team και κανονικό signing για συσκευή.

## Εγγυήσεις απομόνωσης

Δεν υπάρχουν αναφορές στο αποθετήριο `ekab-spoudi`, σε Firebase, σε προσωπικούς λογαριασμούς ή σε κοινή βάση προόδου. Το Bundle ID είναι `gr.ekabspoudi.v2`. Αν η αρχική σελίδα του GitHub Pages αλλάξει, θα εμφανιστούν οι αλλαγές και στο online περιβάλλον του iOS shell. Το iOS προγραμματάκι δεν αντιγράφει από μόνο του τις εγγενείς Android οθόνες.
## Αυτόματες web ενημερώσεις και διαστάσεις οθόνης (IPA 1.0.2)

Το ήδη εγκατεστημένο iOS shell φορτώνει τη **ζωντανή ιστοσελίδα V2**, συνεπώς αλλαγές σε HTML/CSS/JavaScript και εκπαιδευτικό περιεχόμενο εμφανίζονται χωρίς επανεγκατάσταση όταν το iPhone είναι συνδεδεμένο. Ο έλεγχος νέας έκδοσης γίνεται όταν επιστρέφεις στην εφαρμογή και ανά 60 δευτερόλεπτα όσο παραμένει ενεργή. Η αρχική οθόνη ανανεώνεται μόνη της· μέσα σε μάθημα εμφανίζεται **«Ενημέρωση τώρα»** ώστε να μη χαθεί η τρέχουσα εργασία.

**Σημαντικό:** κανένα unsigned IPA δεν μπορεί να εγκαταστήσει μόνο του νέο native εκτελέσιμο. Για αλλαγές στον Swift κώδικα, native permissions ή στην offline ενσωματωμένη έκδοση χρειάζεται νέα υπογραφή και εγκατάσταση / ενημέρωση μέσω κατάλληλου καναλιού (π.χ. TestFlight). Το GitHub Actions αναδημιουργεί πλέον **unsigned IPA σε κάθε push της V2** και ανανεώνει το downloadable release asset· δεν κάνει αυτόματο sideload στο iPhone.

Για οθόνες με Dynamic Island και home indicator, το WKWebView χρησιμοποιεί **`contentInsetAdjustmentBehavior = .never`**, ενώ το HTML έχει `viewport-fit=cover` και CSS `safe-area-inset-*`. Αυτό αποφεύγει τη διπλή εφαρμογή των περιθωρίων ασφαλείας. Η offline fallback συμπεριλαμβάνει πλέον τις απαραίτητες βιβλιοθήκες JS και το read-only V1 import.

Τα αυτοματοποιημένα tests/compile δεν αποδεικνύουν πλήρη οπτική συμβατότητα σε πραγματικό iPhone. Χρειάζεται επιπλέον πραγματική δοκιμή σε portrait/landscape, εμφάνιση πληκτρολογίου, Dynamic Island και home indicator. Δεν αλλάζουν τα επίσημα δεδομένα προόδου.
