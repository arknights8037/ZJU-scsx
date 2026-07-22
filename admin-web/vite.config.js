import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    allowedHosts: true,
    cors: true,
    proxy: {
      // 开发环境把接口和后端静态资源都代理到 Spring Boot，前端代码保持同源访问。
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/admin-uploads': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/community-products': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/mall-uploads': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
})
