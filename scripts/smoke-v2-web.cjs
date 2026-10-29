'use strict';
// Fail publication if a linked boot script is absent or the application does not render.
const fs=require('node:fs'), path=require('node:path'), vm=require('node:vm');
const site=process.argv[2]||'.';
const html=fs.readFileSync(path.join(site,'index.html'),'utf8');
const scripts=[...html.matchAll(/<script\s+src=["']([^"']+)["']/g)].map(m=>m[1].split(/[?#]/)[0]);
for(const required of ['data.js','extra-data.js','v1-content/v1-bridge.js','v1-study-library.js','main.js']) if(!scripts.includes(required))throw Error('Missing script tag: '+required);
for(const file of scripts){
 const target=path.resolve(site,file);
 if(!target.startsWith(path.resolve(site)+path.sep)||!fs.existsSync(target))throw Error('Published script missing: '+file);
}
const elements={'#app':{innerHTML:'Φόρτωση εφαρμογής…'},'#nav':{innerHTML:''},'header .shell':{innerHTML:''}};
const listeners={};
const document={title:'',querySelector:s=>elements[s]||null,body:{addEventListener:(event,fn)=>{listeners[event]=fn;}}};
const sandbox={document,window:{addEventListener:()=>{}},localStorage:{getItem:()=>null,setItem:()=>{}},history:{pushState:()=>{}},location:{hash:''},scrollTo:()=>{},alert:()=>{}};
// Simulated window/globalThis share the same realm as in Safari.
sandbox.window=sandbox;
sandbox.addEventListener=()=>{};
vm.createContext(sandbox);
for(const file of scripts)vm.runInContext(fs.readFileSync(path.join(site,file),'utf8'),sandbox,{filename:file,timeout:10000});
if(elements['#app'].innerHTML.includes('Φόρτωση εφαρμογής')||!elements['#app'].innerHTML.includes('Εκπαιδευτικές Ενότητες'))throw Error('App did not finish booting');
if(!elements['#nav'].innerHTML.includes('goto-mentor'))throw Error('Bottom navigation did not render');
if(!sandbox.window.EKAB_V2_V1_IMPORT || sandbox.window.EKAB_V2_V1_IMPORT.chapterCount!==247) throw Error('V1 educational import did not load 247 chapters');
console.log('PASS: scripts present and EKAB Spoudi V2 booted with V1 library: '+scripts.join(', '));

// Verify all three study collections, archive index and reachable V2 screen.
const library=sandbox.EKAB_V2_LIBRARY;
if(!library || library.sos.length!==314 || library.terms.length!==203 || library.checks.length!==247 || library.files.length<350)
  throw Error('Public V1 library incomplete: '+JSON.stringify({sos:library?.sos?.length,terms:library?.terms?.length,checks:library?.checks?.length,files:library?.files?.length}));
if(!sandbox.V1StudyLibrary || sandbox.V1StudyLibrary.stats().length!==4)
  throw Error('New study library module missing');
// Screen-level regression: there must be one MENTOR, not a selector of five teachers.
function press(action){const element={dataset:{action},closest(){return this;}};listeners.click({target:element,preventDefault(){}});}
press('goto-mentor');press('mentor-tutor');
const tutorHtml=elements['#app'].innerHTML;
if(!tutorHtml.includes('mentor-single')||!tutorHtml.includes('Προτεινόμενο για σένα'))throw Error('Unified tutor not rendered');
if(tutorHtml.includes('choose-faculty')||tutorHtml.includes('mentor-faculty')||tutorHtml.includes('Η ομάδα των 5'))throw Error('Legacy faculty picker still visible');
if(elements['header .shell'].innerHTML.includes('MENTOR OS'))throw Error('Outdated Mentor branding');
console.log('PASS: MENTOR is one adaptive screen with no visible faculty picker');

// Open V1 module through the same existing V2 navigation, without affecting MENTOR.
press('goto-v1lib');
if(!elements['#app'].innerHTML.includes('Βιβλιοθήκη ΕΚΑΒ 1') || !elements['#app'].innerHTML.includes('SOS ΕΟΠΠΕΠ'))throw Error('V1 browser not reachable');
console.log('PASS: V1 study collections, index and extra screen available without replacing V2 UI');
