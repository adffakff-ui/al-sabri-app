const CACHE_NAME = 'quran-app-v3';
const assetsToCache = [
'/',
'/index.html',
'/manifest.json'
];
self.addEventListener('install', (event) => {
event.waitUntil(
caches.open(CACHE_NAME).then((cache) => {
return cache.addAll(assetsToCache);
})
);
self.skipWaiting();
});
self.addEventListener('activate', (event) => {
event.waitUntil(
caches.keys().then((cacheNames) => {
return Promise.all(
cacheNames.map((cacheName) => {
if (cacheName !== CACHE_NAME) {
return caches.delete(cacheName);
}
})
);
})
);
});
self.addEventListener('fetch', (event) => {
event.respondWith(
fetch(event.request).catch(() => caches.match(event.request))
);
});
