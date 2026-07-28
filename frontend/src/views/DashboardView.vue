<template>
  <div>
    <h2 style="margin-bottom:1.25rem">Dashboard</h2>
    <div class="grid">
      <div class="card stat" v-for="s in stats" :key="s.label">
        <div class="stat-label">{{ s.label }}</div>
        <div class="stat-value">{{ s.value }}</div>
      </div>
    </div>
    <div class="card" style="margin-top:1.5rem">
      <h3 style="margin-bottom:0.75rem">Quick Actions</h3>
      <div class="actions">
        <router-link to="/transfer" class="btn btn-primary">Send Money</router-link>
        <router-link to="/wallet" class="btn btn-secondary">View Wallet</router-link>
        <router-link v-if="auth.isAgent" to="/agent" class="btn btn-primary">Agent Desk</router-link>
        <router-link to="/transactions" class="btn btn-secondary">History</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'
import { fmtCurrency } from '../utils/formatters'

const auth = useAuthStore()
const stats = ref([
  { label: 'Currency', value: 'USD / ZIG' },
  { label: 'KYC Level', value: '1' },
  { label: 'Status', value: 'Active' },
])

onMounted(async () => {
  try {
    const userRes = await api.getUser(auth.user.userId)
    const user = userRes.data.data
    stats.value = [
      { label: 'Name', value: `${user.firstName} ${user.lastName}` },
      { label: 'KYC Level', value: user.kycLevel },
      { label: 'Status', value: user.status },
    ]
  } catch (e) {}
})
</script>

<style scoped>
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem; }
.stat-label { font-size: 0.8rem; color: #6b7280; text-transform: uppercase; letter-spacing: 0.05em; font-weight: 600; }
.stat-value { font-size: 1.35rem; font-weight: 800; margin-top: 0.25rem; color: #111827; }
.actions { display: flex; flex-wrap: wrap; gap: 0.75rem; }
</style>
