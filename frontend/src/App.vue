<template>
  <div id="app">
    <Toast />
    <Layout v-if="auth.token">
      <router-view />
    </Layout>
    <router-view v-else />
  </div>
</template>

<script setup>
import { useAuthStore } from './stores/auth'
import Layout from './components/Layout.vue'
import Toast from './components/Toast.vue'
const auth = useAuthStore()
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background: #f3f4f6;
  color: #111827;
}
.card {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.btn {
  display: inline-flex; align-items: center; justify-content: center;
  padding: 0.6rem 1.2rem; border: none; border-radius: 6px;
  font-weight: 600; cursor: pointer; font-size: 0.95rem;
  transition: opacity 0.15s;
}
.btn:hover { opacity: 0.92; }
.btn:disabled { opacity: 0.55; cursor: not-allowed; }
.btn-primary { background: #059669; color: #fff; }
.btn-secondary { background: #e5e7eb; color: #111827; }
.btn-danger { background: #dc2626; color: #fff; }
input, select {
  width: 100%; padding: 0.6rem 0.75rem; border: 1px solid #d1d5db;
  border-radius: 6px; font-size: 0.95rem;
}
input:focus, select:focus { outline: none; border-color: #059669; }
label { display: block; font-size: 0.85rem; font-weight: 600; margin-bottom: 0.35rem; color: #374151; }
.form-group { margin-bottom: 1rem; }
.error { color: #dc2626; font-size: 0.85rem; margin-top: 0.25rem; }
table { width: 100%; border-collapse: collapse; }
th, td { text-align: left; padding: 0.75rem; border-bottom: 1px solid #e5e7eb; font-size: 0.9rem; }
th { font-weight: 600; color: #6b7280; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.05em; }
tr:hover { background: #f9fafb; }
.badge {
  display: inline-block; padding: 0.2rem 0.5rem; border-radius: 999px; font-size: 0.75rem; font-weight: 600;
}
.badge-green { background: #d1fae5; color: #065f46; }
.badge-yellow { background: #fef3c7; color: #92400e; }
.badge-red { background: #fee2e2; color: #991b1b; }
.badge-blue { background: #dbeafe; color: #1e40af; }
</style>
