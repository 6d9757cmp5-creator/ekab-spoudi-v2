// ΕΚΑΒ Σπουδή V2 — scope isolated to this standalone site.
const CACHE='ekab-spoudi-v2-offline-v3-safari';
const ASSETS=['./','./index.html','./main.js','./data.js','./extra-data.js','./manifest.webmanifest','./assets/icon-192.png','./assets/icon-512.png','./assets/ic_launcher_ekab.jpg','./assets/ekab_hero_banner.jpg'];
self.addEventListener('install',e=>{e.waitUntil(caches.open(CACHE).then(c=>c.addAll(ASSETS)).then(()=>self.skipWaiting()));});
self.addEventListener('activate',e=>{e.waitUntil(caches.keys().then(keys=>Promise.all(keys.filter(k=>k.startsWith('ekab-spoudi-v2-')&&k!==CACHE).map(k=>caches.delete(k)))).then(()=>self.clients.claim()));});
self.addEventListener('fetch',e=>{if(e.request.method!=='GET'||new URL(e.request.url).origin!==self.location.origin)return;e.respondWith((e.request.mode==='navigate'?fetch(e.request).catch(()=>caches.match('./index.html')):caches.match(e.request).then(cached=>cached||fetch(e.request))));});