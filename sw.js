// Independent ΕΚΑΒ Σπουδή V2 service worker: network-first for boot scripts.
const CACHE='ekab-spoudi-v2-offline-v6-mentor-one-auto-content';
const ASSETS=['./','./index.html','./main.js','./mentor-adaptive.js','./data.js','./extra-data.js','./auto-update.js','./v1-content/v1-bridge.js','./v1-content/build-version.json','./manifest.webmanifest','./assets/icon-192.png','./assets/icon-512.png','./assets/ic_launcher_ekab.jpg','./assets/ekab_hero_banner.jpg'];
self.addEventListener('install',event=>{
  event.waitUntil(caches.open(CACHE).then(cache=>cache.addAll(ASSETS)).then(()=>self.skipWaiting()));
});
self.addEventListener('activate',event=>{
  event.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(key=>key.startsWith('ekab-spoudi-v2-')&&key!==CACHE).map(key=>caches.delete(key)))).then(()=>self.clients.claim()));
});
self.addEventListener('fetch',event=>{
  const request=event.request;
  if(request.method!=='GET'||new URL(request.url).origin!==self.location.origin)return;
  const url=new URL(request.url);
  if(request.mode==='navigate'||request.destination==='script'||url.pathname.endsWith('.js')||url.pathname.endsWith('/build-version.json')){
    event.respondWith(fetch(request).then(response=>{
      if(response.ok){const copy=response.clone();event.waitUntil(caches.open(CACHE).then(cache=>cache.put(request,copy)));}
      return response;
    }).catch(()=>caches.match(request,{ignoreSearch:true}).then(cached=>cached||(request.mode==='navigate'?caches.match('./index.html'):Response.error()))));
    return;
  }
  event.respondWith(caches.match(request).then(cached=>cached||fetch(request)));
});