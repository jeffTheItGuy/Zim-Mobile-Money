<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="brand">
        <span class="flag">🇿🇼</span>
        <h1>Zim MoMo</h1>
      </div>
      <nav>
        <router-link to="/">Dashboard</router-link>
        <router-link to="/wallet">Wallet</router-link>
        <router-link to="/transfer">Transfer</router-link>
        <router-link v-if="auth.isAgent || auth.isAdmin" to="/agent">Agent</router-link>
        <router-link to="/transactions">History</router-link>
      </nav>
      <div class="user">
        <div class="meta">
          <div class="phone">{{ auth.user?.phone || 'User' }}</div>
          <div class="role">{{ auth.user?.role }}</div>
        </div>
        <button class="btn btn-secondary" style="width:100%" @click="auth.logout">Logout</button>
      </div>
    </aside>
    <main class="content">
      <slot />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const auth = useAuthStore()
</script>

<style scoped>
.layout { display: flex; min-height: 100vh; }
.sidebar {
  width: 240px; background: #111827; color: #f3f4f6;
  display: flex; flex-direction: column; padding: 1.5rem;
}
.brand { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 2rem; }
.brand .flag { font-size: 1.5rem; }
.brand h1 { font-size: 1.25rem; font-weight: 800; letter-spacing: -0.02em; }
nav { display: flex; flex-direction: column; gap: 0.25rem; flex: 1; }
nav a {
  color: #d1d5db; text-decoration: none; padding: 0.6rem 0.75rem; border-radius: 6px;
  font-weight: 500; font-size: 0.95rem;
}
nav a:hover, nav a.router-link-active { background: #1f2937; color: #fff; }
.user { margin-top: auto; }
.meta { margin-bottom: 0.75rem; }
.phone { font-weight: 700; font-size: 0.95rem; }
.role { font-size: 0.8rem; color: #9ca3af; text-transform: capitalize; }
.content { flex: 1; padding: 2rem; overflow-y: auto; }
@media (max-width: 768px) {
  .layout { flex-direction: column; }
  .sidebar { width: 100%; }
}
</style>
