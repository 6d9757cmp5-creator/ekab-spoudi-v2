# ΕΚΑΒ Σπουδή V2 — ανεξάρτητη λειτουργική εφαρμογή

## Safari / iPhone — ενημέρωση 29 Οκτωβρίου 2026

**Δημοσιευμένη διεύθυνση:** https://6d9757cmp5-creator.github.io/ekab-spoudi-v2/

- Προστέθηκαν iOS web-app meta tags, Apple touch icon, safe-area spacing και βελτιώσεις ιστορικού πλοήγησης για Safari.
- Εισήχθησαν από το αρχικό ZIP τα αρχεία `contacts.json`, `mentor_mci.json`, `mentor_equipment.json`, `mentor_faculty.json`, `mentor_library_catalog.json` στη web έκδοση (`extra-data.js`).
- Υλοποιήθηκαν λειτουργικές web οθόνες για **Επαφές ΕΚΑΒ**, **Πρακτικό ΕΟΠΠΕΠ**, **MCI Triage**, **Ιατρικό Εξοπλισμό**, **Ομάδα Εκπαιδευτών** και **Ιατρική Βιβλιοθήκη**, διατηρώντας εκπαιδευτικές επισημάνσεις.
- Έλεγχος σε mobile viewport 390×844 με Chromium σε iPhone-like διαμόρφωση: home, six nav tabs, MENTOR, triage, equipment, tutor, contacts, practical. Δεν έγινε φυσική δοκιμή σε Safari/WebKit.
- Μετάβαση από Safari: **Κοινή χρήση → Προσθήκη στην οθόνη αφετηρίας → Άνοιγμα ως εφαρμογή → Προσθήκη**.

**Όρια πιστότητας:** Η web έκδοση παραμένει μεταφορά του native Android (Compose), όχι κωδικομεταγλώττιση ή ακριβές 1:1. Η πραγματική συνομιλία AI, πλήρης 3D/WebGL ανατομία και όλες οι native αλληλεπιδράσεις δεν έχουν μεταφερθεί. Το GitHub APK είναι Android-only. Δεν έχει παραχθεί iOS IPA — απαιτείται native iOS project, Xcode, υπογραφή/Apple provisioning και δοκιμή σε iPhone.

**Διαχωρισμός:** Το `6d9757cmp5-creator/ekab-spoudi` δεν τροποποιήθηκε. Η πρόοδος της web V2 αποθηκεύεται τοπικά, ανεξάρτητα από την native Android εφαρμογή.


Αυτός ο φάκελος είναι **web/PWA** έκδοση από τα εκπαιδευτικά JSON δεδομένα του πρωτότυπου ZIP Android (29/10/2026). Δεν είναι Kotlin->web μεταγλώττιση και η διεπαφή δεν αντιγράφει κατά γράμμα το Android UI.

## Εμφάνιση
Το σκοτεινό μπλε θέμα είναι προεπιλεγμένο, όπως στο βίντεο του πρωτότυπου Android project. Παλέτα χρωμάτων από τα Android `Color.kt`/`Theme.kt` (background `#0E1A22`, surface `#142430`, surface-variant `#1B3140`, primary `#64B5F6`, secondary `#4DD0E1`). Η web υλοποίηση παραμένει ανεξάρτητη και δεν είναι κατά γράμμα native UI.

## Αμέσως: άνοιγμα
Ανοίξτε το `index.html` με υπολογιστή. Εναλλακτικά το `EKAB-Spoudi-V2-Open.html` είναι εντελώς αυτοτελές (χωρίς άλλα αρχεία) και ιδανικό για δοκιμή. Για εγκατάσταση ως εφαρμογή στο iPhone χρειάζεται HTTPS φιλοξενία (PWA), όχι τοπικό `file://`.

## Δυνατότητες
- ΣΑΕΚ 20 μαθήματα με κείμενα/ενότητες, αναζήτηση εξαμήνων και πρόοδο.
- ΕΟΠΠΕΠ 314 ερωτήσεις/ενδεικτικές απαντήσεις, αναζήτηση, τυχαία εξέταση 10 ερωτήσεων και επανάληψη λαθών.
- Φαρμακολογία 70 εκπαιδευτικές καρτέλες.
- Σεμινάρια BLS/AED, PBLS, ILS, PHTLS, ALS (από τα δεδομένα του ZIP).
- Επάγγελμα: 27 καταχωρίσεις.
- MENTOR OS: δύο αποστολές και έξι εκπαιδευτικά ΗΚΓ. Άλλες δυνατότητες επισημαίνονται ως υπό ανάπτυξη.

## Ανεξαρτησία
- Δεν υπάρχει Firebase config, auth/login, API key, backend ή επικοινωνία με το πρώτο EKAB Spoudi.
- Το localStorage της web έκδοσης αποθηκεύεται στον browser με αποκλειστικό κλειδί `ekab-spoudi-v2-preview-local-2026`.
- `sw.js` παρέχει βασικό offline cache μόνο στον ανεξάρτητο HTTPS origin όπου φιλοξενείται.
- Δεν έχει γίνει ιατρική πιστοποίηση των εκπαιδευτικών δεδομένων.

## Δημοσίευση GitHub Pages — επιτυχής

Η ανεξάρτητη **web προεπισκόπηση** είναι δημοσιευμένη στο:
https://6d9757cmp5-creator.github.io/ekab-spoudi-v2/

Το [workflow δημοσίευσης](.github/workflows/deploy-pages.yml) έχει ολοκληρωθεί επιτυχώς. Στο iPhone: Safari → Κοινή χρήση → Προσθήκη στην οθόνη αφετηρίας.

## Αυθεντική Android V2 — επιτυχές APK build

Το [πρωτότυπο Kotlin/Jetpack Compose project](native-android/source/) και το [επαληθευμένο ZIP](native-android/EKAB-Spoudi-V2-Exact-Android-Source.zip) περιλαμβάνουν 86 αμετάβλητα αρχεία του εξαγόμενου Android project. Η ανεξάρτητη διαδικασία [Android APK build](.github/workflows/native-android-apk.yml) **ολοκληρώθηκε επιτυχώς**:
[Κατέβασε το δοκιμαστικό APK από τα Artifacts](https://github.com/6d9757cmp5-creator/ekab-spoudi-v2/actions/runs/40794001465).

Το Android build αλλάζει προσωρινά μόνο το `applicationId` σε `com.ekabspoudi.v2` και τον εμφανιζόμενο τίτλο ώστε να είναι ξεχωριστό. Δεν έγινε πλήρης δοκιμή σε πραγματική Android συσκευή. Ο MENTOR AI με Firebase απαιτεί δικό του ανεξάρτητο backend για ζωντανές κλήσεις.

**Πιστότητα:** Το native Android χρησιμοποιεί τον αρχικό κώδικα Compose και τις αρχικές οθόνες. Η ιστοσελίδα αποτελεί ξεχωριστή **web ανακατασκευή** και δεν αποδίδει ακόμη όλες τις οθόνες και λειτουργίες του βίντεο 1:1. Για χρήση σε iPhone χρειάζεται επιπλέον υλοποίηση/μεταφορά. Δεν υπάρχει υπογεγραμμένο iOS IPA.

Δεν τροποποιούμε το αρχικό repository `ekab-spoudi` και δεν μεταφέρουμε παλαιά Firebase config, μυστικά ή προσωπικά δεδομένα.

## Πιστότητα με το πρωτότυπο (29/10/2026)

- Ο [αυθεντικός Android κώδικας](native-android/EKAB-Spoudi-V2-Exact-Android-Source.zip) περιέχει 86 αρχεία byte-for-byte με αναλυτικό αρχείο SHA256· εξαιρέθηκαν μόνον 2 αρχεία του παλιού Firebase και ένα προσωρινό εργαλείο.
- Το [native Android APK workflow](.github/workflows/native-android-apk.yml) δημιουργεί δοκιμαστικό APK από το πρωτότυπο Kotlin/Jetpack Compose.
- Η web έκδοση ανασχεδιάστηκε βάσει των πρωτότυπων `HomeScreen.kt`, `MentorHubScreen.kt` και `EkabNavigation.kt` με 6 καρτέλες, αλλά **δεν είναι πιστή 1:1 μεταφορά** όλων των λειτουργιών και απαιτείται περαιτέρω εργασία.
- Το αρχικό `ekab-spoudi` παραμένει εντελώς ανεπηρέαστο.
