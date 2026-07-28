<template>
  <div>
    <h2 style="margin-bottom:1.25rem">My Wallets</h2>
    <div class="grid">
      <div class="card wallet" v-for="w in wallets" :key="w.walletId">
        <div class="wallet-header">
          <span class="currency">{{ w.currencyCode }}</span>
          <span :class="['badge', w.isActive ? 'badge-green' : 'badge-red']">{{ w.isActive ? 'Active' : 'Inactive' }}</span>
        </div>
        <div class="balance">{{ fmtCurrency(w.balance, w.currencyCode) }}</div>
        <div class="limits">
          <div>Daily: {{ fmtCurrency(w.dailyLimit, w.currencyCode) }}</div>
          <div>Monthly: {{ fmtCurrency(w.monthlyLimit, w.currencyCode) }}</div>
        </div>
      </div>
    </div>
    <p v-if="wallets.length === 0" style="color:#6b7280;margin-top:1rem">No wallets found.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'
import { fmtCurrency } from '../utils/formatters'

const wallets = ref([])

onMounted(async () => {
  try {
    const res = await api.getWallets()
    wallets.value = res.data.data || []
  } catch (e) {}
})
</script>

<style scoped>
.grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem; }
.wallet-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem; }
.currency { font-weight: 800; font-size: 1.1rem; color: #059669; }
.balance { font-size: 1.75rem; font-weight: 800; margin-bottom: 0.75rem; }
.limits { display: flex; gap: 1rem; font-size: 0.85rem; color: #4b5563; }
</style>
