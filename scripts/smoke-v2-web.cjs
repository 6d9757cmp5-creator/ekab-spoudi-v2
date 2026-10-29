'use strict';
// Fail publication if a linked boot script is absent or the application does not render.
const fs=require('node:fs'), path=require('node:path'), vm=require('node:vm');
const site=process.argv[2]||'.';
const html=fs.readFileSync(path.join(site,'index.html'),'utf8');
const scripts=[...html.matchAll(/<script\s+src=["']([^"']+)["']/g)].map(m=>m[1].split(/[?#]/)[0]);
for(const required of ['data.js','extra-data.js','v1-content/v1-bridge.js','main.js']) if(!scripts.includes(required))throw Error('Missing script tag: '+required);
for(const file of scripts){
 const target=path.resolve(site,file);
 if(!target.startsWith(path.resolve(site)+path.sep)||!fs.existsSync(target))throw Error('Published script missing: '+file);
}
const elements={'#app':{innerHTML:'Φόρτωση εφαρμογής…'},'#nav':{innerHTML:''},'header .shell':{innerHTML:''}};
const listeners={};
const document={title:'',querySelector:s=>elements[s]||null,body:{addEventListener:(event,fn)=>{listeners[event]=fn;}}};
const sandbox={document,window:{},localStorage:{getItem:()=>null,setItem:()=>{}},history:{pushState:()=>{}},location:{hash:''},scrollTo:()=>{},alert:()=>{}};
// In browsers window and globalThis identify the same realm; mirror this in VM.
sandbox.window=sandbox;
sandbox.addEventListener=()=>{};
vm.createContext(sandbox);
for(const file of scripts)vm.runInContext(fs.readFileSync(path.join(site,file),'utf8'),sandbox,{filename:file,timeout:10000});
if(elements['#app'].innerHTML.includes('Φόρτωση εφαρμογής')||!elements['#app'].innerHTML.includes('Εκπαιδευτικές Ενότητες'))throw Error('App did not finish booting');
if(!elements['#nav'].innerHTML.includes('goto-mentor'))throw Error('Bottom navigation did not render');
if(!sandbox.window.EKAB_V2_V1_IMPORT || sandbox.window.EKAB_V2_V1_IMPORT.chapterCount!==247) throw Error('V1 educational import did not load 247 chapters');
console.log('PASS: scripts present and EKAB Spoudi V2 booted with V1 library: '+scripts.join(', '));

// Screen-level regression: there must be one MENTOR, not a selector of five teachers.
function press(action){const element={dataset:{action},closest(){return this;}};listeners.click({target:element,preventDefault(){}});}
press('goto-mentor');press('mentor-tutor');
const tutorHtml=elements['#app'].innerHTML;
if(!tutorHtml.includes('mentor-single')||!tutorHtml.includes('Προτεινόμενο για σένα'))throw Error('Unified tutor not rendered');
if(tutorHtml.includes('choose-faculty')||tutorHtml.includes('mentor-faculty')||tutorHtml.includes('Η ομάδα των 5'))throw Error('Legacy faculty picker still visible');
if(elements['header .shell'].innerHTML.includes('MENTOR OS'))throw Error('Outdated Mentor branding');
console.log('PASS: MENTOR is one adaptive screen with no visible faculty picker');
