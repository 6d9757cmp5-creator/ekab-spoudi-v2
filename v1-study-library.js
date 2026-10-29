/* EKAB V2 — locally rendered, independent, read-only V1 educational library.
 * Original V2 layout/theme, no V1 account/API/code execution, no medical approval.
 */
(function(root){
'use strict';
const escape=x=>String(x==null?'':x).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const norm=x=>String(x||'').toLocaleLowerCase('el').normalize('NFD').replace(/[\u0300-\u036f]/g,'');
const tabs=Object.freeze([
 ['sos','SOS ΕΟΠΠΕΠ'],['terms','Ιατρικό λεξικό'],['checks','Αυτοέλεγχοι ΣΑΕΚ'],['sources','Όλα τα αρχεία V1'],['audit','Έλεγχος']
]);
const state={tab:'sos',query:'',page:0};
function records(){
 const source=root.EKAB_V2_LIBRARY||{};
 if(state.tab==='sos')return (source.sos||[]).map((x,i)=>({key:x.id||i,heading:x.id||('SOS '+(i+1)),
  body:(x.points||[]).join('\n'),source:x.sourceUrl,status:x.auditStatus||'Μη επικυρωμένο'}));
 if(state.tab==='terms')return (source.terms||[]).map((x,i)=>({key:i,heading:x.term||x.en||'Όρος',
  body:[x.en,x.category,x.definition,(x.aliases||[]).join(', ')].filter(Boolean).join(' — '),source:x.source,status:'Εκπαιδευτικός όρος'}));
 if(state.tab==='checks')return (source.checks||[]).map((x,i)=>({key:x.id||i,heading:x.course+' · '+(x.chapterTitle||''),
  body:[x.question,'Απάντηση: '+x.answer].filter(Boolean).join('\n'),status:'Ενδεικτική απάντηση'}));
 if(state.tab==='sources')return (source.files||[]).map((x,i)=>({key:i,heading:x.path,
  body:x.kind||x.group||'Αρχείο V1',url:x.url,status:'Αρχειακό — δεν εκτελείται'}));
 return [];
}
function stats(){
 const s=root.EKAB_V2_LIBRARY||{};
 return [
  ['ΕΟΠΠΕΠ SOS',s.sos?.length||0],['Ιατρικοί όροι',s.terms?.length||0],
  ['Αυτοέλεγχοι ΣΑΕΚ',s.checks?.length||0],['Αρχεία V1',s.files?.length||0]
 ];
}
function render(){
 const source=root.EKAB_V2_LIBRARY||{};
 const counts=stats();
 let html='<div class="detailbar"><button class="back" data-action="back">← Πίσω</button></div>';
 html+='<div class="bar"><h1>Βιβλιοθήκη ΕΚΑΒ 1</h1><small>Πλήρες δημόσιο περιεχόμενο · Ανεξάρτητη V2</small></div>';
 html+='<div class="notice">Εκπαιδευτικό περιεχόμενο προς έλεγχο. Η μεταφορά αρχείων δεν αποτελεί ιατρική επικύρωση ή ενεργοποίηση υπηρεσιών. Οι προσωπικές σημειώσεις παραμένουν στη συσκευή σου.</div>';
 html+='<div class="native-stats">'+counts.map(([n,v])=>'<div class="native-stat"><b>'+escape(v)+'</b><span>'+escape(n)+'</span></div>').join('')+'</div>';
 html+='<div class="tabs" role="group" aria-label="Περιεχόμενο ΕΚΑΒ 1">'+tabs.map(([id,label])=>'<button class="pill '+(state.tab===id?'on':'')+'" data-action="v1-tab" data-value="'+id+'">'+escape(label)+'</button>').join('')+'</div>';
 if(state.tab==='audit'){
  const q=source.qa||{};
  html+='<div class="card"><h3>Κατάσταση επιστημονικού ελέγχου</h3><p><strong>Καταγραφή πηγών:</strong> '+escape(q.sourceLinks||0)+' συνδέσεις πηγών.</p><p><strong>Στοιχεία χωρίς αναφορά πηγής:</strong> '+escape(q.withoutSource||0)+'.</p><p><strong>Δημοσιευμένος κώδικας:</strong> '+escape(source.sourceCommit||'')+'</p><p>Η πληρότητα αρχείων/πηγών και η λειτουργική δοκιμή δεν αποδεικνύουν την επιστημονική ορθότητα. Δεν έχει δοθεί ανεξάρτητη κλινική υπογραφή.</p></div>';
  html+='<div class="card"><h3>Λειτουργίες που θέλουν σύνδεση</h3><p>Λογαριασμοί, ιδιωτική πρόοδος ΕΚΑΒ 1, εξωτερικό AI, ζωντανή 3D απεικόνιση, real-time υπηρεσίες και βαθμολόγηση με διακομιστή απαιτούν ξεχωριστό backend, άδειες και δοκιμές πριν ενεργοποιηθούν.</p></div>';
  return html;
 }
 const items=records().filter(x=>!state.query||norm(x.heading+' '+x.body).includes(norm(state.query)));
 const pageSize=20,max=Math.max(0,Math.ceil(items.length/pageSize)-1);state.page=Math.min(Math.max(0,state.page),max);
 html+='<div class="card"><label for="v1-study-search"><strong>Αναζήτηση</strong></label><input id="v1-study-search" type="search" value="'+escape(state.query)+'" placeholder="Αναζήτηση στις εγγραφές" aria-label="Αναζήτηση στην παλιά βιβλιοθήκη" style="width:100%;max-width:100%;padding:10px;margin-top:8px"><p class="muted">'+items.length+' εγγραφές · σελίδα '+(state.page+1)+' / '+(max+1)+'</p></div>';
 html+='<div class="list">'+items.slice(state.page*pageSize,(state.page+1)*pageSize).map(item=>
   '<article class="card"><h3>'+escape(item.heading)+'</h3><p style="white-space:pre-wrap">'+escape(item.body)+'</p><small class="muted">'+escape(item.status||'')+'</small>'+
   (item.source&&/^https:\/\//.test(item.source)?'<p><a href="'+escape(item.source)+'" target="_blank" rel="noopener noreferrer">Πηγή</a></p>':'')+
   (item.url&&/^https:\/\/github\.com\//.test(item.url)?'<p><a href="'+escape(item.url)+'" target="_blank" rel="noopener noreferrer">Προβολή αρχείου GitHub</a></p>':'')+'</article>'
 ).join('')+'</div>';
 html+='<div class="action-row"><button class="btn alt" data-action="v1-prev" '+(state.page?'':'disabled')+'>← Προηγούμενη</button><button class="btn" data-action="v1-next" '+(state.page<max?'':'disabled')+'>Επόμενη →</button></div>';
 return html;
}
function act(action,value){
 if(action==='v1-tab' && tabs.some(([id])=>id===value)){state.tab=value;state.query='';state.page=0;return true;}
 if(action==='v1-prev'){state.page=Math.max(0,state.page-1);return true;}
 if(action==='v1-next'){state.page++;return true;}
 return false;
}
function search(value){state.query=String(value||'').slice(0,250);state.page=0;}
root.V1StudyLibrary=Object.freeze({render,act,search,stats});
})(typeof globalThis==='object'?globalThis:this);
