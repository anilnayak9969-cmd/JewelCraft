import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// In development the React app calls /api and /uploads on its own origin and Vite
// forwards them to Spring Boot. The browser's Origin header is dropped on the way, so the
// backend sees a same-origin call and CORS never blocks it – whichever port Vite ends up on.
// In production set VITE_API_URL to the backend URL instead (see .env.example).
const backend = {
  // Override with VITE_PROXY_TARGET=http://localhost:8081 to point the dev server at another backend.
  target: process.env.VITE_PROXY_TARGET || 'http://localhost:8080',
  configure: (proxy) => proxy.on('proxyReq', (proxyReq) => proxyReq.removeHeader('origin')),
}

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': backend,
      '/uploads': backend,
    },
  },
})
