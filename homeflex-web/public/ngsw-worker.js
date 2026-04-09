self.addEventListener('install', () => {
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    (async () => {
      const cacheKeys = await caches.keys();
      await Promise.all(cacheKeys.map((key) => caches.delete(key)));

      await self.clients.claim();
      await self.registration.unregister();

      const clients = await self.clients.matchAll({
        type: 'window',
        includeUncontrolled: true,
      });

      await Promise.all(clients.map((client) => client.navigate(client.url)));
    })(),
  );
});

self.addEventListener('fetch', () => {
  // Intentionally empty. This worker exists only to evict stale Angular PWA caches.
});
