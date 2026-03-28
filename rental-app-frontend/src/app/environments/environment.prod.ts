export const environment = {
  production: true,
  // Use same-origin paths so nginx can proxy to backend in Docker/local deployments.
  apiUrl: '/api/v1',
  wsUrl: '/ws',
  googleClientId: '',
  firebaseConfig: {
    apiKey: '',
    authDomain: '',
    projectId: '',
    storageBucket: '',
    messagingSenderId: '',
    appId: '',
  },
};
