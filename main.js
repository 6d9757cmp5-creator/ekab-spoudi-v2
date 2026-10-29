(()=>{'use strict';
const D=EKAB_V2_DATA, X=EKAB_V2_EXTRA, storeKey='ekab-spoudi-v2-preview-local-2026', S={page:'home',detail:null,semester:'Όλα',group:'Όλα',search:'',medSearch:'',medCategory:'Όλα',quizIndex:0,reveal:false,quizList:null,missionIndex:0,missionOption:null,mentorTab:'menu',seminarId:null,flashIndex:0,flashReveal:false,triageCase:0,triageAnswers:{},mentorGuidance:null,mentorNotice:''};
let progress={courses:[],questions:{},notes:{}};try{let d=JSON.parse(localStorage.getItem(storeKey)||'{}');if(d && typeof d==='object'){progress.courses=Array.isArray(d.courses)?d.courses:[];progress.questions=d.questions&&typeof d.questions==='object'?d.questions:{};progress.notes=d.notes&&typeof d.notes==='object'?d.notes:{}}}catch(e){};
const mentorStoreKey='ekab-spoudi-v2-mentor-private-v1';
let mentorMemory=MentorAdaptive.newMemory();
try{mentorMemory=MentorAdaptive.safeMemory(JSON.parse(localStorage.getItem(mentorStoreKey)||'null'));}catch(_){}
const saveMentor=()=>{try{localStorage.setItem(mentorStoreKey,JSON.stringify(mentorMemory));return true;}catch(_){return false;}};
const $=s=>document.querySelector(s), e=s=>String(s??'').replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;').replaceAll("'",'&#39;');
const save=()=>{try{localStorage.setItem(storeKey,JSON.stringify(progress))}catch(ex){}};
const btn=(txt,action,extra='')=>`<button type="button" class="btn ${extra}" data-action="${e(action)}">${e(txt)}</button>`;
const sectionTitle=(title,subtitle='')=>`<div class="bar"><h1>${e(title)}</h1>${subtitle?`<small>${e(subtitle)}</small>`:''}</div>`;
const back=()=>`<div class="detailbar"><button class="back" data-action="back">← Πίσω</button></div>`;
const disclaimer=()=>`<div class="notice">Εκπαιδευτικό περιεχόμενο από το αρχείο V2, χωρίς ανεξάρτητη επιστημονική πιστοποίηση στη web έκδοση. Δεν υποκαθιστά επικαιροποιημένες επίσημες οδηγίες ή κλινική εκπαίδευση.</div>`;
const tabs=(items,selected,act)=>`<div class="tabs" role="group">${items.map(item=>`<button class="pill ${selected===item?'on':''}" data-action="${e(act)}" data-value="${e(item)}">${e(item)}</button>`).join('')}</div>`;
function nav(page,fromHistory=false){S.page=page;S.detail=null;S.search='';S.reveal=false;S.quizList=null;S.mentorTab='menu';try{if(!fromHistory&&location.hash!=='#'+page)history.pushState({page},'','#'+page)}catch(_){}render();if(typeof scrollTo==='function')scrollTo(0,0)}
function finish(){const qs=Object.values(progress.questions), right=qs.filter(x=>x==='right').length;return {right,answered:qs.length}}
const coursePercent=()=>Math.round(progress.courses.length/D.courses.length*100);
function home(){const f=finish();const pct=coursePercent();const modules=[
['psychology','MENTOR','AI Καθηγητής · 3D Ανατομία · ECG Lab · Προσομοίωση Ασθενοφόρου · MCI Triage · 15 Πηγές PR #83','Εργαστήρια & υπό ανάπτυξη','mentor','#0288D1','V2 DEMO'],
['book','Κατάρτιση ΣΑΕΚ ΕΚΑΒ','Το πρώτο βήμα · Εξάμηνα, 20 μαθήματα και αναλυτικά κεφάλαια','4 Εξάμηνα','saek','#153C54',''],
['school','Πιστοποίηση ΕΟΠΠΕΠ','314 Ερωτήσεις Θεωρίας · Ομάδα Α & Β · SOS · Εξάσκηση',`${f.answered}/314 Μελετήθηκαν`,'eoppep','#0288D1',''],
['medication','Επείγουσα Φαρμακολογία','70 φάρμακα, δόσεις, ενδείξεις & κατευθυντήριες οδηγίες ERC 2025',`${D.medicines.length} Φάρμακα`,'pharma','#FFA000',''],
['heart','Προετοιμασία Σεμιναρίων','BLS/AED · PBLS · ILS · PHTLS · ALS αναλυτικά βήματα','5 Σεμινάρια','seminars','#D32F2F',''],
['cards','Κάρτες Μελέτης (Flashcards)','Επανάληψη ιατρικής ορολογίας, φαρμάκων και κλινικών στοιχείων','Διαδραστικές Κάρτες','flashcards','#2E7D32','']];
return `<section class="native-hero"><div class="native-hero-cover"><span class="native-hero-overline">ΕΚΑΒ Σπουδή</span><h1>ΕΚΑΒ Σπουδή</h1><p>Ολοκληρωμένος οδηγός Εκπαίδευσης &amp; Πιστοποίησης Διασωστών</p></div><div class="native-hero-progress"><strong>Συνολική Πρόοδος Μελέτης</strong><div class="progress"><div style="width:${pct}%"></div></div></div></section>
<div class="native-stats">${[[`${f.answered}/314`,'Ερωτήσεις'],[`${progress.courses.length}/20`,'Μαθήματα'],[`${f.right}`,'Απαντήσεις'],[`${pct}%`,'Μελέτη']].map(([n,t])=>`<div class="native-stat"><b>${n}</b><span>${t}</span></div>`).join('')}</div>
<h2 class="sectiontitle">Εκπαιδευτικές Ενότητες</h2><div class="native-modules">${modules.map(([ico,t,desc,badge,path,color,tag])=>`<button class="native-module ${path==='mentor'?'mentor-priority':''}" data-action="goto-${path}"><div class="native-icon" style="--module-color:${color}">${({psychology:'✺',book:'▣',school:'🎓',medication:'✚',heart:'♥',cards:'▤'})[ico]}</div><div class="native-module-copy"><div class="native-module-title">${e(t)} ${tag?`<span class="native-tag">${e(tag)}</span>`:''}</div><small>${e(desc)}</small><span class="native-module-badge">${e(badge)}</span></div><span class="native-chevron">›</span></button>`).join('')}</div>
<h2 class="sectiontitle">Εργαλεία &amp; Άμεση Πρόσβαση</h2><div class="native-quick">${[['⚖','Επάγγελμα & Νομοθεσία','law'],['🚑','Πρακτικό Μέρος ΕΟΠΠΕΠ','practical'],['☎','Επαφές ΕΚΑΒ','contacts'],['📝','Γρήγορο Quiz','eoppep']].map(([ic,t,p])=>`<button class="native-quick-tile" data-action="goto-${p}"><b>${ic}</b><span>${e(t)}</span></button>`).join('')}</div><div class="safari-setup"><div><strong>📱 Εγκατάσταση στο iPhone</strong><p>Άνοιξε τη V2 στο Safari → Κοινή χρήση → Προσθήκη στην οθόνη αφετηρίας. Θα ανοίγει από δικό της εικονίδιο.</p></div></div>${disclaimer()}`;}
function saek(){if(S.detail?.kind==='course'){let c=D.courses.find(c=>c.id===S.detail.id);if(!c)return back();let done=progress.courses.includes(c.id);return `${back()}${sectionTitle(c.title,c.semester)}<div class="card"><span class="chip">${e(c.semester)}</span><div class="action-row">${btn(done?'✓ Ολοκληρώθηκε — αναίρεση':'✓ Ολοκλήρωσα την ανάγνωση','toggle-course',done?'ok':'alt')}</div></div><h2 class="sectiontitle">Κεφάλαια (${c.sections.length})</h2><div class="card">${c.sections.map((ch,i)=>`<article class="chapter"><h3>${i+1}. ${e(ch.title)}</h3><p>${e(ch.content)}</p></article>`).join('')}</div>${disclaimer()}`}
 const courses=D.courses.filter(c=>S.semester==='Όλα'||c.semester.startsWith(S.semester));return `${sectionTitle('Κατάρτιση ΣΑΕΚ ΕΚΑΒ','4 εξάμηνα · 20 μαθήματα')}${tabs(['Όλα',"Α'","Β'","Γ'","Δ'"],S.semester,'semester')}<div class="list">${courses.map(c=>`<button class="card cardbtn" data-action="open-course" data-id="${e(c.id)}"><span class="arrow">›</span><span class="chip ${progress.courses.includes(c.id)?'good':''}">${e(c.semester)} ${progress.courses.includes(c.id)?'· Διαβάστηκε':''}</span><h3>${e(c.title)}</h3><p>${c.sections.length} κεφάλαια</p></button>`).join('')}</div>`}
function questionsFiltered(){return D.questions.filter(q=>(S.group==='Όλα'||q.group===S.group)&&(!S.search||`${q.question} ${q.topic} ${q.answer}`.toLocaleLowerCase('el').includes(S.search.toLocaleLowerCase('el'))))}
function eoppep(){if(S.quizList){let ids=S.quizList, total=ids.length, i=Math.min(S.quizIndex,total-1),q=D.questions.find(q=>q.id===ids[i]);if(!q)return `${back()}<p>Δεν βρέθηκε η ερώτηση.</p>`;const grade=progress.questions[q.id];return `${back()}${sectionTitle('Εξάσκηση ΕΟΠΠΕΠ',`Ερώτηση ${i+1} / ${total}`)}<div class="progress"><div style="width:${(i+1)/total*100}%"></div></div><div class="card"><span class="chip">Ομάδα ${e(q.group)} · ${e(q.topic)}</span><div class="quiz-question">${e(q.question)}</div>${!S.reveal?`<p class="muted">Σκέψου ή πες την απάντηση με δικά σου λόγια και μετά άνοιξε τη λύση.</p>${btn('Δείξε τη σωστή απάντηση','reveal','alt')}`:`<div class="answer"><strong>Ενδεικτική απάντηση</strong><p>${e(q.answer)}</p></div><p>Πώς απάντησες;</p><div class="answers">${btn('✓ Σωστά','grade-right','ok')}${btn('✕ Χρειάζομαι επανάληψη','grade-wrong','wrong')}</div>${grade?`<p><span class="chip ${grade==='right'?'good':'warn'}">${grade==='right'?'Καταχωρίστηκε σωστό':'Για επανάληψη'}</span></p>`:''}`}</div><div class="action-row">${i>0?btn('← Προηγούμενη','prev','alt'):''}${btn(i+1===total?'Τέλος εξέτασης':'Επόμενη →','next')}</div>${disclaimer()}`}
 if(S.detail?.kind==='question'){let q=D.questions.find(q=>q.id===S.detail.id);return `${back()}${sectionTitle('Ερώτηση '+q.number,'Ομάδα '+q.group)}<div class="card"><span class="chip">${e(q.topic)}</span><h2>${e(q.question)}</h2><details><summary>Εμφάνιση ενδεικτικής απάντησης</summary><p>${e(q.answer)}</p></details>${btn('Ξεκίνησε εξάσκηση','start-single')}</div>${disclaimer()}`}
 let qs=questionsFiltered();return `${sectionTitle('Πιστοποίηση ΕΟΠΠΕΠ','314 ερωτήσεις θεωρίας')}${tabs(['Όλα','A','B'],S.group,'group')}<input class="input" id="question-search" aria-label="Αναζήτηση ερωτήσεων" placeholder="Αναζήτησε λέξεις σε ερωτήσεις ή απαντήσεις…" value="${e(S.search)}"><div class="bar"><small>${qs.length} αποτελέσματα</small><div class="answers">${btn('Μικτή εξέταση 10 ερωτήσεων','start-mixed','small')}${btn('Επανάληψη λαθών','start-wrong','alt small')}</div></div><div class="list">${qs.slice(0,60).map(q=>`<button class="card cardbtn" data-action="open-question" data-id="${e(q.id)}"><span class="arrow">›</span><span class="chip">${e(q.id)} · ${e(q.topic)}</span><h3>${e(q.question)}</h3></button>`).join('')}</div>${qs.length>60?'<p class="muted">Εμφανίζονται οι πρώτες 60. Χρησιμοποίησε την αναζήτηση για όλες.</p>':''}${qs.length===0?'<p class="empty">Δεν βρέθηκαν ερωτήσεις.</p>':''}`}
function pharma(){if(S.detail?.kind==='medicine'){let med=D.medicines.find(x=>String(x.id)===S.detail.id);return `${back()}${sectionTitle(med.name,med.category)}<div class="card"><span class="chip">${e(med.english||'')}</span>${[['Μηχανισμός',med.mechanism],['Ενδείξεις',med.indication],['Προφυλάξεις',med.caution],['Παρακολούθηση',med.monitor],['Σημειώσεις',med.notes]].map(([h,v])=>`<div class="chapter"><h3>${e(h)}</h3><p>${e(v)}</p></div>`).join('')}</div>${disclaimer()}`}
 const categories=['Όλα',...new Set(D.medicines.map(x=>x.category))],ms=D.medicines.filter(x=>(S.medCategory==='Όλα'||x.category===S.medCategory)&&(!S.medSearch||`${x.name} ${x.category} ${x.english}`.toLocaleLowerCase('el').includes(S.medSearch.toLocaleLowerCase('el'))));return `${sectionTitle('Επείγουσα Φαρμακολογία',`${D.medicines.length} εκπαιδευτικές καρτέλες`)}${tabs(categories,S.medCategory,'med-category')}<input class="input" id="med-search" placeholder="Αναζήτηση φαρμάκων…" value="${e(S.medSearch)}"><div class="list">${ms.map(m=>`<button class="card cardbtn" data-action="open-med" data-id="${e(m.id)}"><span class="arrow">›</span><span class="chip">${e(m.category)}</span><h3>${e(m.name)}</h3><p>${e(m.english||'')}</p></button>`).join('')}</div>${disclaimer()}`}
function seminars(){if(S.detail?.kind==='seminar'){let sem=D.seminars.find(x=>x.id===S.detail.id);return `${back()}${sectionTitle(sem.name,sem.subtitle)}<div class="card">${sem.units.map((u,i)=>`<div class="chapter"><h3>${i+1}. ${e(u[0])}</h3>${u.slice(1).filter(Boolean).map(x=>`<p>${e(x)}</p>`).join('')}</div>`).join('')}</div>${disclaimer()}`}
 return `${sectionTitle('Προετοιμασία Σεμιναρίων','5 εκπαιδευτικά προγράμματα')}<div class="grid">${D.seminars.map(s=>`<button class="card cardbtn" data-action="open-seminar" data-id="${e(s.id)}"><span class="arrow">›</span><div class="mark">🚑</div><h3>${e(s.name)}</h3><p>${e(s.subtitle)} · ${s.units.length} ενότητες</p></button>`).join('')}</div>${disclaimer()}`}
function law(){if(S.detail?.kind==='law'){let x=D.profession.find(q=>q.id===S.detail.id);return `${back()}${sectionTitle(x.title,x.article||'')}<div class="card"><p>${e(x.summary)}</p><div class="notice">${e(x.limit)}</div><span class="chip">${e(x.status)}</span></div>${disclaimer()}`};return `${sectionTitle('Επάγγελμα & Νομοθεσία',D.profession.length+' ενότητες')}<div class="list">${D.profession.map(x=>`<button class="card cardbtn" data-action="open-law" data-id="${e(x.id)}"><span class="arrow">›</span><h3>${e(x.title)}</h3><p>${e(x.summary)}</p></button>`).join('')}</div>${disclaimer()}`}
function contacts(){return `${sectionTitle('Τηλέφωνα & Επαφές ΕΚΑΒ','166 · 112 · Περιφερειακά Κέντρα')}<p class="native-subtitle">Από τον πρωτότυπο κατάλογο επαφών της εφαρμογής Android. Επαλήθευσε πριν από μη επείγουσα χρήση.</p>${X.contacts.map(c=>`<section><h2 class="sectiontitle">${e(c.category)}</h2><div class="list">${c.entries.map(x=>`<div class="card"><span class="chip">${e(x.badge||'Επικοινωνία')}</span><h3>${e(x.title)}</h3><p>${e(x.desc||'')}</p>${x.phone?`<p><a class="btn small" style="display:inline-block;text-decoration:none;margin-top:9px" href="tel:${e(x.phone.replace(/[^+0-9]/g,''))}">☎ ${e(x.phone)}</a></p>`:''}</div>`).join('')}</div></section>`).join('')}${disclaimer()}`}
function practical(){return `${sectionTitle('Πρακτικό Μέρος ΕΟΠΠΕΠ','Ενότητες από το πρωτότυπο ZIP')}<div class="list">${D.practical.map((x,i)=>`<article class="card"><span class="chip">Ενότητα ${i+1}</span><h3>${e(x.title)}</h3>${(x.paragraphs||[]).map(p=>`<p style="white-space:pre-line;margin-top:9px">${e(p)}</p>`).join('')}${x.items?.length?`<details><summary>Δες τα σημεία μελέτης (${x.items.length})</summary>${x.items.map(t=>`<p>${e(typeof t==='string'?t:JSON.stringify(t))}</p>`).join('')}</details>`:''}</article>`).join('')}</div>${disclaimer()}`}
function mentorTutor(){
const suggestion=MentorAdaptive.recommend(mentorMemory,progress);
const current=S.mentorGuidance||MentorAdaptive.guide(mentorMemory,'',progress);
const topic=MentorAdaptive.TOPICS[current.id];
const visited=Object.values(mentorMemory.topics).filter(x=>x.visits>0).length;
const due=Object.values(mentorMemory.topics).filter(x=>x.due&&x.due<=MentorAdaptive.todayString()).length;
const styleOptions=[['auto','Αυτόματη προσαρμογή'],['simple','Πιο απλά'],['detailed','Αναλυτικά'],['exam','Εξάσκηση εξέτασης']];
const goalOptions=[['learning','ΣΑΕΚ / γενική μελέτη'],['certification','ΕΟΠΠΕΠ'],['trauma','Προνοσοκομειακή εξάσκηση'],['pharmacology','Φαρμακολογία']];
return back()+sectionTitle('MENTOR','Ένας προσωπικός καθηγητής · όλες οι εκπαιδευτικές θεματικές')+
'<section class="mentor-single" aria-label="Ενιαίος προσωπικός καθηγητής">'+
'<div class="mentor-person"><span class="mentor-orb" aria-hidden="true">✺</span><div><h2>MENTOR</h2><p>Σε ακολουθώ στον τρόπο που μαθαίνεις.</p></div></div>'+
'<p class="mentor-description">Δεν χρειάζεται να επιλέξεις διαφορετικό εκπαιδευτή. Πες τι θέλεις να μελετήσεις και ο MENTOR επιλέγει τον κατάλληλο τομέα, ύφος και επόμενη εξάσκηση.</p>'+
'<div class="mentor-learning-summary"><div><strong>'+visited+'</strong><small>Θεματικές εξάσκησης</small></div><div><strong>'+due+'</strong><small>Επαναλήψεις που εκκρεμούν</small></div></div>'+
'<label class="mentor-label" for="mentor-request">Τι θέλεις να μάθεις ή να επαναλάβεις;</label>'+
'<div class="mentor-request-row"><input id="mentor-request" type="text" maxlength="180" autocomplete="off" placeholder="Π.χ. ανατομία, ΚΑΡΠΑ, φάρμακα, ΕΟΠΠΕΠ" aria-describedby="mentor-request-hint"><button class="btn" type="button" data-action="mentor-study">Συνέχισε</button></div>'+
'<p id="mentor-request-hint" class="mentor-fine">Η διατύπωσή σου δεν αποθηκεύεται. Η επιλογή θεματικής και οι προσωπικές σημειώσεις επανάληψης παραμένουν μόνο σε αυτή τη συσκευή.</p>'+
'<div class="mentor-suggestion"><span class="chip">Προτεινόμενο για σένα</span><p>'+e(MentorAdaptive.TOPICS[suggestion.id].label)+'</p><small>'+e(suggestion.reason)+'</small> '+btn('Ξεκίνα πρόταση','mentor-suggest','alt')+'</div>'+
'<div class="mentor-guidance" role="status" aria-live="polite"><h3>Η καθοδήγησή σου · '+e(topic.label)+'</h3><p>'+e(current.message)+'</p><small>'+e(current.reason)+'</small><div class="action-row">'+btn('Άνοιγμα σχετικού εργαλείου','mentor-open')+'</div>'+
'<div class="mentor-review"><p>Πώς πήγε η εξάσκηση; (δική σου εκτίμηση)</p><div class="action-row">'+btn('Χρειάζομαι επανάληψη','mentor-review-repeat','alt')+btn('Το κατάλαβα','mentor-review-ok','alt')+btn('Μου ήταν εύκολο','mentor-review-easy','alt')+'</div></div></div>'+
(S.mentorNotice?'<p class="mentor-fine" role="status">'+e(S.mentorNotice)+'</p>':'')+
'<details class="mentor-settings"><summary>Προσαρμογή ύφους και στόχου</summary><label for="mentor-tone">Ύφος διδασκαλίας</label><select id="mentor-tone">'+styleOptions.map(([v,t])=>'<option value="'+v+'"'+(mentorMemory.style===v?' selected':'')+'>'+e(t)+'</option>').join('')+'</select><label for="mentor-goal">Προτεραιότητα μελέτης</label><select id="mentor-goal">'+goalOptions.map(([v,t])=>'<option value="'+v+'"'+(mentorMemory.goal===v?' selected':'')+'>'+e(t)+'</option>').join('')+'</select><p class="mentor-fine">Η αυτόματη προσαρμογή βασίζεται σε θέματα που επέλεξες και στην αυτοαξιολόγησή σου, όχι σε κλινική πιστοποίηση.</p>'+btn('Διαγραφή μόνο της μνήμης MENTOR','mentor-forget','alt')+'</details>'+
'<p class="mentor-fine">Προσωρινή έκδοση καθοδήγησης βάσει κανόνων: χωρίς συνδεδεμένο AI ή αξιολόγηση ελεύθερων απαντήσεων. Δεν παρέχει οδηγίες για πραγματικούς ασθενείς.</p>'+
'</section>'+disclaimer();
}
function mentorEquipment(){return `${back()}${sectionTitle('Ιατρικός Εξοπλισμός',`${X.equipment.length} πρωτότυπες εκπαιδευτικές καρτέλες`)}<div class="list">${X.equipment.map((x,i)=>`<article class="card"><span class="chip">${e(x.category)}</span><h3>${e(x.name)}</h3><p>${e(x.description)}</p><details><summary>Εξαρτήματα · ${x.components?.length||0}</summary>${(x.components||[]).map(a=>`<p>• ${e(a)}</p>`).join('')}</details><details><summary>Σημεία ασφαλείας</summary><p>${e(x.safetyChecks||'')}</p></details></article>`).join('')}</div>${disclaimer()}`}
function mentorLibrary(){const sources=X.library.sources||[];return `${back()}${sectionTitle('Ιατρική Βιβλιοθήκη',`${sources.length} βιβλιογραφικές πηγές από το πρωτότυπο`)}<div class="no-backend">Πρόκειται για κατάλογο πηγών του ZIP. Ο κατάλογος δεν αποτελεί απόδειξη ότι οι πλήρεις πηγές έχουν εγκριθεί επιστημονικά ή ότι λειτουργεί ανάκτησή τους από AI.</div><div class="list">${sources.map(x=>`<article class="card"><h3>${e(x.title)}</h3><p>${e(x.publisher||'')} ${e(x.year||'')}</p><span class="chip">${e(x.status||'Έλεγχος εκκρεμεί')}</span>${/^https:\/\//.test(x.url||'')?`<p><a href="${e(x.url)}" target="_blank" rel="noopener noreferrer">Επίσημη πηγή</a></p>`:''}</article>`).join('')}</div>`}
function mentorTriage(){let caseData=X.triage[S.triageCase%X.triage.length],answers=S.triageAnswers;return `${back()}${sectionTitle('Πολλαπλοί Τραυματίες — MCI',`Εκπαιδευτικό περιστατικό ${S.triageCase+1} από ${X.triage.length}`)}<div class="card"><h3>${e(caseData.title)}</h3><p>${e(caseData.description)}</p><p>${e(caseData.sceneSafetyPrompt||'')}</p></div>${caseData.patients.map((p,i)=>{let k=caseData.id+'-'+p.id,selected=answers[k];return `<article class="training-patient"><h3>${e(p.name)}</h3><p>${e(p.description)}</p><div class="clinical-choices">${['ΚΟΚΚΙΝΟ','ΚΙΤΡΙΝΟ','ΠΡΑΣΙΝΟ','ΜΑΥΡΟ'].map(c=>`<button data-action="choose-triage" data-id="${e(k)}" data-value="${c}" aria-pressed="${selected===c}">${c}</button>`).join('')}</div>${selected?`<div class="training-feedback"><strong>Πρωτότυπη εκπαιδευτική κατηγορία:</strong> ${e(p.triageTag)}<p>${e(p.rationale)}</p><span class="chip ${p.triageTag.startsWith(selected)?'good':'warn'}">${p.triageTag.startsWith(selected)?'Συμφωνεί':'Χρειάζεται επανάληψη'}</span></div>`:''}</article>`}).join('')}<div class="action-row">${btn('Επόμενο εκπαιδευτικό περιστατικό','next-triage','alt')}</div>${disclaimer()}`}
function mentorAnatomy(){return `${back()}${sectionTitle('Ζωντανή 3D Ανατομία','Υποσύστημα από τον αρχικό κώδικα Kotlin')}<div class="card"><h3>Ανατομία 3D</h3><p>Το πρωτότυπο Android project περιέχει τη συγκεκριμένη οθόνη σε Jetpack Compose, αλλά δεν περιέχει μεταφέρσιμο 3D μοντέλο για Safari. Δεν θα εμφανίσω ψεύτικο 3D. Απαιτείται ξεχωριστή μηχανή WebGL και τα κατάλληλα μοντέλα.</p></div>`}
function mentorAudit(){return `${back()}${sectionTitle('Έλεγχος & Audit','Πρωτότυπη ενότητα MENTOR')}<div class="card"><h3>Έλεγχος ποιότητας</h3><p>Οι καταγραφές του αρχικού ZIP δεν επιβεβαιώνουν αυτομάτως ιατρική έγκριση. Η πλήρης αναφορά ποιότητας του native Android app παραμένει διαθέσιμη στον αρχικό κώδικα και χρειάζεται ανεξάρτητη επαλήθευση πριν παρουσιαστεί ως εγκεκριμένη.</p></div>`}
function ecgSvg(points){if(!Array.isArray(points)||!points.length)return '';let coords=points.map((y,i)=>`${(i/(points.length-1)*760+20).toFixed(1)},${(85-y*48).toFixed(1)}`).join(' ');return `<div class="ecg"><svg viewBox="0 0 800 150" role="img" aria-label="Ενδεικτική κυματομορφή ΗΚΓ">${Array.from({length:41},(_,i)=>`<line x1="${i*20}" x2="${i*20}" y1="0" y2="150"/>`).join('')}${Array.from({length:9},(_,i)=>`<line x1="0" x2="800" y1="${i*20}" y2="${i*20}"/>`).join('')}<polyline points="${coords}"/></svg></div>`}
function mentor(){
if(S.mentorTab==='ecg'){return `${back()}${sectionTitle('ECG Intelligence Lab','6 εκπαιδευτικά παραδείγματα')}${D.ecgs.map(x=>`<div class="card" style="margin-bottom:12px"><h3>${e(x.name)}</h3>${ecgSvg(x.points)}<p><strong>Συχνότητα:</strong> ${e(x.rate)} · <strong>Ρυθμός:</strong> ${e(x.regularity)}</p><p>${e(x.clinicalSummary)}</p><span class="chip ${x.shockable?'warn':'good'}">${x.shockable?'Απινιδώσιμος':'Μη απινιδώσιμος'}</span></div>`).join('')}${disclaimer()}`}
if(S.mentorTab==='tutor')return mentorTutor();
if(S.mentorTab==='triage')return mentorTriage();
if(S.mentorTab==='equipment')return mentorEquipment();
if(S.mentorTab==='library')return mentorLibrary();
if(S.mentorTab==='anatomy')return mentorAnatomy();
if(S.mentorTab==='audit')return mentorAudit();
if(S.mentorTab==='missions'){let m=D.missions[S.missionIndex]||D.missions[0],opts=m.sceneSafety.options;return `${back()}${sectionTitle('ΕΚΑΒ Live — Εκπαιδευτική αποστολή',`${S.missionIndex+1} / ${D.missions.length}`)}<div class="card"><span class="chip">${e(m.category)} · ${e(m.difficulty)}</span><h2>${e(m.title)}</h2><p>${e(m.callIntake.chiefComplaint)}</p><p class="muted">${e(m.callIntake.location)}</p><h3>${e(m.sceneSafety.prompt)}</h3><div class="list">${opts.map((o,i)=>`<button class="card cardbtn" data-action="mission-option" data-index="${i}">${e(o.text)}</button>`).join('')}</div>${S.missionOption!==null?`<div class="${opts[S.missionOption].isCorrect?'success':'notice'}">${e(opts[S.missionOption].feedback)}</div>`:''}<div class="action-row">${btn('Άλλη αποστολή','next-mission','alt')}</div></div>${disclaimer()}`}
const features=[
['✺','Προσωπικός καθηγητής','Ένας MENTOR · προσωπική μελέτη, μνήμη και προτάσεις','Προσωπική καθοδήγηση','#153C54','tutor'],
['✚','ΕΚΑΒ Live — Αποστολές','Διαδραστικά περιστατικά: Κλήση, C-ABCDE, SBAR παράδοση','Προσομοιωτής','#D32F2F','missions'],
['♙','Ζωντανή 3D Ανατομία','Ανδρικό/Γυναικείο μοντέλο, 5 συστήματα, περιστροφή 360° & τραύμα','3D Εργαστήριο','#0288D1','anatomy'],
['▥','ECG Intelligence Lab','ΗΚΓ κυματομορφή, αναγνώριση ρυθμών & αλγόριθμοι ERC 2025','Ηλεκτροκαρδιογράφημα','#00897B','ecg'],
['⚠','Πολλαπλοί Τραυματίες (MCI)','Διαλογή START / Triage Sieve σε τροχαία & μαζικά συμβάντα','Διαλογή Triage','#FFA000','triage'],
['✚','Ιατρικός Εξοπλισμός','AED, Ασκός BVM AMBU, Μόνιτορ & πρωτόκολλα ασφαλείας','Εξοπλισμός','#5E35B1','equipment'],
['▤','Ιατρική Βιβλιοθήκη (PR #83)','Κατάλογος 15 βιβλιογραφικών πηγών, με εκκρεμή έλεγχο','Κατάλογος πηγών','#3949AB','library'],
['✓','Πίνακας Επαλήθευσης & Audit','Αναφορά συμμόρφωσης, PR #83, #85, #6, #39 & κατάσταση εγκρίσεων','Έλεγχος Ποιότητας','#2E7D32','audit']];
return `<section class="mentor-hero"><div class="native-mentor-intro"><div class="native-icon" style="--module-color:#153C54">⚡</div><div><h2>MENTOR <span class="clinical-badge">Εκπαιδευτικό πρωτότυπο</span></h2><p>Εξατομικευμένη εκπαιδευτική εξάσκηση · χωρίς AI προς το παρόν</p></div></div><p>Ολοκληρωμένη κλινική ακαδημία για σπουδαστές ΣΑΕΚ και υποψηφίους ΕΟΠΠΕΠ. Περιλαμβάνει 3D ανατομία, προσομοιωτή αποστολών, εργαστήριο ECG, διαλογή μαζικών συμβάντων και τεκμηριωμένη βιβλιοθήκη 15 πηγών.</p></section><h2 class="sectiontitle">Εκπαιδευτικά Εργαστήρια &amp; Υποσυστήματα</h2><div class="native-modules">${features.map(([ic,t,sub,badge,c,code])=>`<button class="native-module" data-action="mentor-${code}"><div class="native-icon" style="--module-color:${c}">${ic}</div><div class="native-module-copy"><div class="native-module-title">${e(t)}</div><small>${e(sub)}</small><span class="native-module-badge">${e(badge)}</span></div><span class="native-chevron">›</span></button>`).join('')}</div>${disclaimer()}`}
function flashcards(){const arr=D.questions.slice(0,314);const i=S.flashIndex%arr.length,q=arr[i];return `${sectionTitle('Κάρτες Flashcards','Εξάσκηση Ορολογίας & Φαρμάκων')}<div class="card flashcard-native"><span class="chip">${i+1}/${arr.length} · Ομάδα ${e(q.group)}</span><h2>${e(q.question)}</h2>${S.flashReveal?`<div class="answer">${e(q.answer)}</div>`:'<p>Σκέψου την απάντηση και πάτησε για αποκάλυψη.</p>'}<div class="action-row">${btn(S.flashReveal?'Απόκρυψη απάντησης':'Δείξε απάντηση','flash-reveal','alt')}${btn('Επόμενη κάρτα →','flash-next')}</div></div>${disclaimer()}`}
function render(){const meta={home:['ΕΚΑΒ Σπουδή','Διασώστης – Πλήρωμα Ασθενοφόρου'],saek:['Κατάρτιση ΣΑΕΚ ΕΚΑΒ','4 Εξάμηνα · 20 Μαθήματα'],eoppep:['Πιστοποίηση ΕΟΠΠΕΠ','314 Ερωτήσεις Θεωρίας & SOS'],flashcards:['Κάρτες Flashcards','Εξάσκηση Ορολογίας & Φαρμάκων'],pharma:['Επείγουσα Φαρμακολογία','70 Φαρμακευτικές Καρτέλες'],mentor:['MENTOR','Προσωπικός καθηγητής & Κλινικά Εργαστήρια'],seminars:['Προετοιμασία Σεμιναρίων','BLS, PBLS, ILS, PHTLS, ALS'],contacts:['Τηλέφωνα & Επαφές ΕΚΑΒ','166 · 112 · Περιφερειακά Κέντρα'],practical:['Πρακτικό Μέρος ΕΟΠΠΕΠ','Πρακτικές ενότητες'],law:['Επάγγελμα & Νομοθεσία','Π.Δ. 348/96 & Π.Δ. 62/07']}[S.page]||['ΕΚΑΒ Σπουδή V2','Διασώστης'];document.title='ΕΚΑΒ Σπουδή V2 — '+meta[0];
const h=document.querySelector('header .shell');if(h)h.innerHTML=`<button class="top-icon" data-action="${S.page==='home'?'goto-home':'back'}" aria-label="${S.page==='home'?'Αρχική':'Πίσω'}">${S.page==='home'?'✚':'←'}</button><div class="header-title"><strong>${meta[0]}</strong><small>${meta[1]}</small></div><div class="header-actions">${S.page==='home'?'<button data-action="goto-flashcards" title="Κάρτες">▤</button><button data-action="goto-law" title="Νομοθεσία">⚖</button><button data-action="goto-practical" title="Πρακτικό Μέρος">✚</button><button data-action="goto-contacts" title="Επαφές">☎</button>':''}</div>`;
$('#app').innerHTML=`<div class="contentwidth">${({home,saek,eoppep,flashcards,pharma,seminars,mentor,law,contacts,practical}[S.page]||home)()}<div class="footer">ΕΚΑΒ Σπουδή V2 · ανεξάρτητη web μεταφορά του πρωτότυπου Kotlin · χωρίς διασύνδεση με το παλιό project</div></div>`;
$('#nav').innerHTML=`<div>${[['home','⌂','Αρχική'],['mentor','✺','Mentor'],['eoppep','▱','ΕΟΠΠΕΠ'],['flashcards','▤','Κάρτες'],['saek','▣','ΣΑΕΚ'],['pharma','✚','Φάρμακα']].map(([p,ic,t])=>`<button data-action="goto-${p}" class="${S.page===p?'on':''}"><strong>${ic}</strong><span>${t}</span></button>`).join('')}</div>`;}
function quizStart(ids){if(!ids||!ids.length){alert('Δεν υπάρχουν διαθέσιμες ερωτήσεις στο επιλεγμένο φίλτρο.');return}S.quizList=ids;S.quizIndex=0;S.reveal=false;render();scrollTo(0,0)}
function click(a,el){if(a.startsWith('goto-'))return nav(a.slice(5));if(a==='back'){if(S.page==='mentor'&&S.mentorTab!=='menu'){S.mentorTab='menu';return render()}if(S.page==='eoppep'&&S.quizList){S.quizList=null;S.reveal=false;return render()}if(S.detail){S.detail=null;return render()}return nav('home')}
 if(a==='semester'){S.semester=el.dataset.value;return render()};if(a==='group'){S.group=el.dataset.value;return render()};if(a==='med-category'){S.medCategory=el.dataset.value;return render()};
 if(a==='open-course'){S.detail={kind:'course',id:el.dataset.id};render();return scrollTo(0,0)};
 if(a==='toggle-course'){let id=S.detail.id;progress.courses=progress.courses.includes(id)?progress.courses.filter(x=>x!==id):[...progress.courses,id];save();return render()};
 if(a==='open-question'){S.detail={kind:'question',id:el.dataset.id};render();return scrollTo(0,0)};
 if(a==='open-med'){S.detail={kind:'medicine',id:el.dataset.id};render();return scrollTo(0,0)};
 if(a==='open-seminar'){S.detail={kind:'seminar',id:el.dataset.id};render();return scrollTo(0,0)};
 if(a==='open-law'){S.detail={kind:'law',id:el.dataset.id};render();return scrollTo(0,0)};
 if(a==='start-mixed'){let ids=questionsFiltered().map(x=>x.id);for(let i=ids.length-1;i>0;i--){let j=Math.floor(Math.random()*(i+1));[ids[j],ids[i]]=[ids[i],ids[j]]}return quizStart(ids.slice(0,10))}
 if(a==='start-wrong'){let ids=D.questions.filter(q=>progress.questions[q.id]==='wrong').map(q=>q.id);if(ids.length===0){alert('Δεν υπάρχουν ακόμη λανθασμένες απαντήσεις για επανάληψη.');return}return quizStart(ids)}
 if(a==='start-single')return quizStart([S.detail.id]);
 if(a==='reveal'){S.reveal=true;return render()};
 if(a.startsWith('grade-')){let id=S.quizList[S.quizIndex];progress.questions[id]=a==='grade-right'?'right':'wrong';save();return render()};
 if(a==='next'){if(S.quizIndex<S.quizList.length-1){S.quizIndex++;S.reveal=false;return render()}let count=S.quizList.filter(id=>progress.questions[id]==='right').length;alert(`Τέλος εξάσκησης! ${count} / ${S.quizList.length} ερωτήσεις αξιολογήθηκαν ως σωστές (με αυτοαξιολόγηση).`);S.quizList=null;return render()}
 if(a==='prev'){S.quizIndex=Math.max(0,S.quizIndex-1);S.reveal=false;return render()}
 if(a==='mentor-ecg'){S.mentorTab='ecg';render();return scrollTo(0,0)}
 if(a==='mentor-missions'){S.mentorTab='missions';S.missionOption=null;render();return scrollTo(0,0)}
 if(a==='mentor-tutor'){S.mentorTab='tutor';return render()}
 if(a==='mentor-triage'){S.mentorTab='triage';return render()}
 if(a==='mentor-equipment'){S.mentorTab='equipment';return render()}
 if(a==='mentor-library'){S.mentorTab='library';return render()}
 if(a==='mentor-anatomy'){S.mentorTab='anatomy';return render()}
 if(a==='mentor-audit'){S.mentorTab='audit';return render()}
 if(a==='mentor-study'){
   const input=$('#mentor-request')?.value.trim()||'';
   if(!input){S.mentorNotice='Γράψε ένα θέμα για να συνεχίσουμε.';return render();}
   const guided=MentorAdaptive.guide(mentorMemory,input,progress);
   mentorMemory=MentorAdaptive.recordVisit(mentorMemory,guided.id);
   S.mentorGuidance=MentorAdaptive.guide(mentorMemory,input,progress);
   S.mentorNotice=saveMentor()?'Η εξάσκηση καταγράφηκε τοπικά.':'Η αποθήκευση δεν είναι διαθέσιμη σε αυτή τη συσκευή.';
   return render();
 }
 if(a==='mentor-suggest'){
   mentorMemory=MentorAdaptive.recordVisit(mentorMemory,MentorAdaptive.recommend(mentorMemory,progress).id);
   S.mentorGuidance=MentorAdaptive.guide(mentorMemory,'',progress);
   S.mentorNotice=saveMentor()?'Πρόταση ενεργή.':'Η αποθήκευση δεν είναι διαθέσιμη.';
   return render();
 }
 if(a==='mentor-open'){
   const route=S.mentorGuidance?.route||MentorAdaptive.guide(mentorMemory,'',progress).route;
   if(['mentor-anatomy','mentor-ecg','mentor-missions','goto-pharma','goto-eoppep','mentor-triage','mentor-equipment','goto-saek'].includes(route))return click(route,el);
   return;
 }
 if(a.startsWith('mentor-review-')){
   const quality=a.slice('mentor-review-'.length);
   if(!['repeat','ok','easy'].includes(quality))return;
   const current=S.mentorGuidance||MentorAdaptive.guide(mentorMemory,'',progress);
   mentorMemory=MentorAdaptive.recordReview(mentorMemory,current.id,quality);
   const date=mentorMemory.topics[current.id]?.due||'';
   S.mentorNotice=saveMentor()?'Η προσωπική επανάληψη προγραμματίστηκε για '+date+'. Δεν άλλαξαν βαθμοί ΣΑΕΚ/ΕΟΠΠΕΠ.':'Η αποθήκευση δεν είναι διαθέσιμη.';
   S.mentorGuidance=MentorAdaptive.guide(mentorMemory,'',progress);
   return render();
 }
 if(a==='mentor-forget'){
   if(!confirm('Να διαγραφούν μόνο οι προσωπικές ρυθμίσεις και οι επαναλήψεις του MENTOR;'))return;
   mentorMemory=MentorAdaptive.newMemory();try{localStorage.removeItem(mentorStoreKey);}catch(_){}
   S.mentorGuidance=null;S.mentorNotice='Η μνήμη του MENTOR μηδενίστηκε. Οι βαθμοί των άλλων ενοτήτων παρέμειναν ως είχαν.';
   return render();
 }
 if(a==='choose-triage'){S.triageAnswers[el.dataset.id]=el.dataset.value;return render()}
 if(a==='next-triage'){S.triageCase=(S.triageCase+1)%X.triage.length;S.triageAnswers={};return render()}
 if(a==='mentor-none'){alert('Η αρχική εφαρμογή Android περιλαμβάνει αυτή την οθόνη. Η πιστή μεταφορά της σε web δεν έχει ολοκληρωθεί.');return}
 if(a==='flash-reveal'){S.flashReveal=!S.flashReveal;return render()}
 if(a==='flash-next'){S.flashIndex++;S.flashReveal=false;return render()}
 if(a==='mission-option'){S.missionOption=Number(el.dataset.index);return render()};if(a==='next-mission'){S.missionIndex=(S.missionIndex+1)%D.missions.length;S.missionOption=null;return render()}
}
document.body.addEventListener('click',ev=>{let el=ev.target.closest('[data-action]');if(el){ev.preventDefault();click(el.dataset.action,el)}});
document.body.addEventListener('change',ev=>{
 if(ev.target.id==='mentor-tone' && MentorAdaptive.STYLES.includes(ev.target.value)){
   mentorMemory=MentorAdaptive.safeMemory({...mentorMemory,style:ev.target.value});
   S.mentorGuidance=null;S.mentorNotice=saveMentor()?'Η προτίμηση ύφους αποθηκεύτηκε.':'Η αποθήκευση δεν είναι διαθέσιμη.';render();
 }
 if(ev.target.id==='mentor-goal' && MentorAdaptive.TOPICS[ev.target.value]){
   mentorMemory=MentorAdaptive.safeMemory({...mentorMemory,goal:ev.target.value});
   S.mentorGuidance=null;S.mentorNotice=saveMentor()?'Η προτεραιότητα μελέτης αποθηκεύτηκε.':'Η αποθήκευση δεν είναι διαθέσιμη.';render();
 }
});
document.body.addEventListener('keydown',ev=>{
 if(ev.key==='Enter' && ev.target.id==='mentor-request'){ev.preventDefault();click('mentor-study',ev.target);}
});
document.body.addEventListener('input',ev=>{if(ev.target.id==='question-search'){let v=ev.target.value,pos=ev.target.selectionStart;S.search=v;render();let field=$('#question-search');field.focus();field.setSelectionRange(pos,pos)}if(ev.target.id==='med-search'){let v=ev.target.value,pos=ev.target.selectionStart;S.medSearch=v;render();let field=$('#med-search');field.focus();field.setSelectionRange(pos,pos)}});
let hash=location.hash.slice(1);if(['home','saek','eoppep','flashcards','pharma','seminars','law','mentor','contacts','practical'].includes(hash))S.page=hash;render();
if(typeof window!=='undefined')window.addEventListener('popstate',()=>{let p=location.hash.slice(1);if(['home','saek','eoppep','flashcards','pharma','seminars','law','mentor','contacts','practical'].includes(p))nav(p,true);else nav('home',true)});
})();