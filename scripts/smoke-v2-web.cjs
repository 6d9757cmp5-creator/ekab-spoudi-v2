'use strict';
// Fail publication if a linked boot script is absent or the application does not render.
const fs=require('node:fs'), path=require('node:path'), vm=require('node:vm');
const site=process.argv[2]||'.';
const html=fs.readFileSync(path.join(site,'index.html'),'utf8');
const scripts=[...html.matchAll(/<script\s+src=["']([^"']+)["']/g)].map(m=>m[1]);
for(const required of ['data.js','extra-data.js','main.js']) if(!scripts.includes(required))throw Error('Missing script tag: '+required);
for(const file of scripts){
 const target=path.resolve(site,file);
 if(!target.startsWith(path.resolve(site)+path.sep)||!fs.existsSync(target))throw Error('Published script missing: '+file);
}
const elements={'#app':{innerHTML:'Φόρτωση εφαρμογής…'},'#nav':{innerHTML:''},'header .shell':{innerHTML:''}};
const document={title:'',querySelector:s=>elements[s]||null,body:{addEventListener:()=>{}}};
const sandbox={document,localStorage:{getItem:()=>null,setItem:()=>{}},history:{pushState:()=>{}},location:{hash:''},scrollTo:()=>{},alert:()=>{}};
vm.createContext(sandbox);
for(const file of scripts)vm.runInContext(fs.readFileSync(path.join(site,file),'utf8'),sandbox,{filename:file,timeout:10000});
if(elements['#app'].innerHTML.includes('Φόρτωση εφαρμογής')||!elements['#app'].innerHTML.includes('Εκπαιδευτικές Ενότητες'))throw Error('App did not finish booting');
if(!elements['#nav'].innerHTML.includes('goto-mentor'))throw Error('Bottom navigation did not render');
console.log('PASS: scripts present and EKAB Spoudi V2 booted: '+scripts.join(', '));
