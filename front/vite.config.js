import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  server: {
    proxy: {
      '/admin': {
        target: 'http://localhost:11451',
        changeOrigin: true,
      },
      '/ws': {
        target: 'http://localhost:11451',
        ws: true,
        changeOrigin: true,
      },
    },
  },
})
