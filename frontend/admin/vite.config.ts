import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // Load env file based on `mode` in the current working directory.
  const env = loadEnv(mode, process.cwd(), '')
  
  // Get API base URL from environment or use default
  const apiBaseUrl = env.VITE_API_BASE_URL || 'http://localhost:8080'
  
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      },
      extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },
    server: {
      port: 3001,
      proxy: {
        // Proxy pour les routes d'authentification directement vers identity-service (contourne le gateway)
        '/api/identity/auth': {
          target: 'http://localhost:8081',
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path
        },
        // Proxy pour toutes les autres routes API via le gateway
        '/api': {
          target: apiBaseUrl,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path
        }
      }
    },
    build: {
      outDir: 'dist',
      sourcemap: true
    },
    optimizeDeps: {
      include: ['vue', 'vue-router', 'pinia', 'axios']
    },
    // Expose env variables to the client
    define: {
      __API_BASE_URL__: JSON.stringify(apiBaseUrl)
    }
  }
})
