/* MENTOR/V2 web content updates for Safari and the installed WKWebView.
 * No native IPA self-installation. Never clears official progress or Mentor memory.
 * Active study is never reloaded without user action.
 */
(function(){
  'use strict';
  if(typeof navigator==='undefined'||typeof fetch!=='function'||typeof document==='undefined'||!/^https?:$/.test(location.protocol))return;
  const KEY='ekab-v2-installed-content-version-v1';
  const PENDING='ekab-v2-update-pending-v1';
  let checking=false;
  let notifiedVersion=null;
  function activeStudy(){
    const hash=String(location.hash||'');
    const active=document.activeElement;
    return (hash && hash!=='#home')||
      (active&&/INPUT|TEXTAREA|SELECT/.test(active.tagName||''));
  }
  function apply(version){
    try{
      localStorage.setItem(KEY,version);
      localStorage.removeItem(PENDING);
      const token='ekab-v2-reloaded-'+version;
      // If repeated checks race while the page is closing, reload only once.
      if(sessionStorage.getItem(token))return;
      sessionStorage.setItem(token,'1');
    }catch(_){}
    location.reload();
  }
  function showAvailable(version){
    if(notifiedVersion===version&&document.getElementById('ekab-v2-update-banner'))return;
    notifiedVersion=version;
    const banner=document.getElementById('ekab-v2-update-banner')||document.createElement('aside');
    banner.id='ekab-v2-update-banner';
    banner.setAttribute('role','status');
    banner.setAttribute('aria-live','polite');
    banner.style.cssText='position:fixed;z-index:9999;left:12px;right:12px;bottom:calc(86px + env(safe-area-inset-bottom));max-width:580px;margin:0 auto;padding:12px;border-radius:14px;background:#123d54;color:#fff;border:1px solid #6dc7f2;box-shadow:0 7px 30px #0008;display:flex;align-items:center;flex-wrap:wrap;gap:12px;font:inherit;';
    banner.replaceChildren();
    const message=document.createElement('span');
    message.textContent='Υπάρχει νέα έκδοση της ΕΚΑΒ Σπουδή V2. Η τρέχουσα μελέτη σου δεν θα διακοπεί.';
    message.style.cssText='flex:1 1 220px;font-size:13px;line-height:1.4;';
    const button=document.createElement('button');
    button.type='button';
    button.textContent='Ενημέρωση τώρα';
    button.style.cssText='padding:11px 14px;border-radius:9px;background:#e1f5ff;color:#07374e;border:0;font-weight:800;min-height:44px;';
    button.addEventListener('click',()=>apply(version),{once:true});
    banner.append(message,button);
    document.body.append(banner);
  }
  async function check(){
    if(checking||document.visibilityState==='hidden')return;
    checking=true;
    try{
      if('serviceWorker' in navigator){
        try{const reg=await navigator.serviceWorker.getRegistration();if(reg)await reg.update();}catch(_){}
      }
      const response=await fetch('./v1-content/build-version.json?check='+Date.now(),{cache:'no-store'});
      if(!response.ok)return;
      const info=await response.json();
      if(!info||!info.sourceCommit||!info.v2Commit)return;
      const next=String(info.v2Commit)+'/'+String(info.sourceCommit);
      const previous=localStorage.getItem(KEY);
      if(!previous){localStorage.setItem(KEY,next);return;}
      if(previous===next){
        localStorage.removeItem(PENDING);
        document.getElementById('ekab-v2-update-banner')?.remove();
        notifiedVersion=null;
        return;
      }
      localStorage.setItem(PENDING,next);
      if(activeStudy()){showAvailable(next);return;}
      apply(next);
    }catch(_){
      // No network or disallowed storage: keep the existing page and its data.
    }finally{checking=false;}
  }
  window.addEventListener('pageshow',()=>{void check();});
  document.addEventListener('visibilitychange',()=>{if(document.visibilityState==='visible')void check();});
  window.addEventListener('focus',()=>{void check();});
  window.addEventListener('hashchange',()=>{void check();});
  // App stays open for long sessions: check for deployed updates without user action.
  setInterval(()=>{void check();},60_000);
})();
