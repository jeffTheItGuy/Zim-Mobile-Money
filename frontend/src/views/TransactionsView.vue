<template>
  <div>
    <h2 style="margin-bottom:1.25rem">Transaction History</h2>
    <div class="card">
      <div class="form-group" style="max-width:320px">
        <label>Wallet</label>
        <select v-model="selectedWallet" @change="loadTransactions">
          <option v-for="w in wallets" :key="w.walletId" :value="w.walletId">{{ w.currencyCode }} — {{ fmtCurrency(w.balance, w.currencyCode) }}</option>
        </select>
      </div>
      <table v-if="transactions.length">
        <thead>
          <tr><th>Ref</th><th>Type</th><th>Amount</th><th>Fee</th><th>Status</th><th>Date</th></tr>
        </thead>
        <tbody>
          <tr v-for="t in transactions" :key="t.transactionId">
            <td class="mono">{{ t.referenceNumber }}</td>
            <td>{{ txTypeLabel(t.transactionType) }}</td>
            <td>{{ fmtCurrency(t.amount, t.currencyCode) }}</td>
            <td>{{ fmtCurrency(t.feeAmount, t.currencyCode) }}</td>
            <td><span :class="['badge', txStatusClass(t.status)]">{{ t.status }}</span></td>
            <td>{{ fmtDate(t.createdAt) }}</td>
          </tr>
        </tbody>
      </table>
      <p v-else style="color:#6b7280;margin-top:1rem">No transactions found.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'
import { fmtCurrency, fmtDate, txTypeLabel, txStatusClass } from '../utils/formatters'

const wallets = ref([])
const selectedWallet = ref('')
const transactions = ref([])

onMounted(async () => {
  try {
    const res = await api.getWallets()
    wallets.value = res.data.data || []
    if (wallets.value.length) {
      selectedWallet.value = wallets.value[0].walletId
      await loadTransactions()
    }
  } catch (e) {}
})

async function loadTransactions() {
  if (!selectedWallet.value) return
  try {
    const res = await api.getTransactions(selectedWallet.value)
    transactions.value = res.data.data?.content || []
  } catch (e) {}
}
</script>

<style scoped>
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; font-size: 0.85rem; }
</style>
