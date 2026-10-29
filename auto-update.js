/* Independent EKAB Spoudi V2 silent web-content updates.
 * Works in Safari and the native iPhone WKWebView without changing the UI.
 * Never touches the user's study-progress localStorage key.
 */
(function(){
  'use strict';
  if(typeof navigator==='undefined'||typeof fetch!=='function'||typeof document==='undefined')return;
  const KEY='ekab-v2-installed-content-version-v1';
  const PENDING='ekab-v2-update-pending-v1';
  let checking=false;
  function hasActiveTask(){
    const hash=String(location.hash||'');
    const active=document.activeElement;
    return (hash && hash!=='#home')||
      (active && /INPUT|TEXTAREA|SELECT/.test(active.tagName||''));
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
      if(previous===next){localStorage.removeItem(PENDING);return;}
      localStorage.setItem(PENDING,next);
      if(hasActiveTask())return;
      localStorage.setItem(KEY,next);
      localStorage.removeItem(PENDING);
      const token='ekab-v2-reloaded-'+next;
      if(sessionStorage.getItem(token))return;
      sessionStorage.setItem(token,'1');
      location.reload();
    }catch(_){
      // Network outage / private mode: never interrupt offline education.
    }finally{checking=false;}
  }
  window.addEventListener('pageshow',()=>{void check();});
  document.addEventListener('visibilitychange',()=>{if(document.visibilityState==='visible')void check();});
  window.addEventListener('focus',()=>{void check();});
})();