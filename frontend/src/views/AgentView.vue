<template>
  <div>
    <h2 style="margin-bottom:1.25rem">Agent Desk</h2>

    <div class="card" style="margin-bottom:1.5rem" v-if="agent">
      <div class="agent-header">
        <div>
          <div class="agent-code">{{ agent.agentCode }}</div>
          <div class="business">{{ agent.businessName }}</div>
        </div>
        <div class="float">
          <div class="float-label">Float Balance</div>
          <div class="float-value">{{ fmtCurrency(agent.floatBalance) }}</div>
        </div>
      </div>
    </div>

    <div class="tabs">
      <button :class="['tab', tab==='cashin' && 'active']" @click="tab='cashin'">Cash In</button>
      <button :class="['tab', tab==='cashout' && 'active']" @click="tab='cashout'">Cash Out</button>
      <button v-if="auth.isAdmin" :class="['tab', tab==='topup' && 'active']" @click="tab='topup'">Top-up Float</button>
    </div>

    <div class="card" style="max-width:520px; margin-top:1rem">
      <form v-if="tab==='cashin'" @submit.prevent="doCashIn">
        <div class="form-group"><label>Customer Phone</label><input v-model="cashInForm.customerPhoneNumber" required maxlength="15" /></div>
        <div class="form-group"><label>Amount</label><input v-model.number="cashInForm.amount" type="number" step="0.01" min="0.01" required /></div>
        <div class="form-group">
          <label>Currency</label>
          <select v-model="cashInForm.currencyCode" required><option value="USD">USD</option><option value="ZIG">ZIG</option></select>
        </div>
        <div class="form-group"><label>Description</label><input v-model="cashInForm.description" maxlength="255" /></div>
        <div class="form-group">
          <label>Idempotency Key</label>
          <input v-model="cashInForm.idempotencyKey" required />
          <button type="button" class="btn btn-secondary" style="margin-top:0.5rem;font-size:0.8rem;padding:0.4rem 0.6rem" @click="cashInForm.idempotencyKey = crypto.randomUUID()">Generate</button>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">{{ loading ? 'Processing...' : 'Process Cash In' }}</button>
      </form>

      <form v-if="tab==='cashout'" @submit.prevent="doCashOut">
        <div class="form-group"><label>Customer Phone</label><input v-model="cashOutForm.customerPhoneNumber" required maxlength="15" /></div>
        <div class="form-group"><label>Amount</label><input v-model.number="cashOutForm.amount" type="number" step="0.01" min="0.01" required /></div>
        <div class="form-group">
          <label>Currency</label>
          <select v-model="cashOutForm.currencyCode" required><option value="USD">USD</option><option value="ZIG">ZIG</option></select>
        </div>
        <div class="form-group"><label>Description</label><input v-model="cashOutForm.description" maxlength="255" /></div>
        <div class="form-group">
          <label>Idempotency Key</label>
          <input v-model="cashOutForm.idempotencyKey" required />
          <button type="button" class="btn btn-secondary" style="margin-top:0.5rem;font-size:0.8rem;padding:0.4rem 0.6rem" @click="cashOutForm.idempotencyKey = crypto.randomUUID()">Generate</button>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">{{ loading ? 'Processing...' : 'Process Cash Out' }}</button>
      </form>

      <form v-if="tab==='topup'" @submit.prevent="doTopUp">
        <div class="form-group"><label>Agent Code</label><input v-model="topUpForm.agentCode" required maxlength="20" /></div>
        <div class="form-group"><label>Amount</label><input v-model.number="topUpForm.amount" type="number" step="0.01" min="0.01" required /></div>
        <div class="form-group">
          <label>Currency</label>
          <select v-model="topUpForm.currencyCode" required><option value="USD">USD</option><option value="ZIG">ZIG</option></select>
        </div>
        <div class="form-group">
          <label>Idempotency Key</label>
          <input v-model="topUpForm.idempotencyKey" required />
          <button type="button" class="btn btn-secondary" style="margin-top:0.5rem;font-size:0.8rem;padding:0.4rem 0.6rem" @click="topUpForm.idempotencyKey = crypto.randomUUID()">Generate</button>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">{{ loading ? 'Processing...' : 'Top Up Float' }}</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'
import { fmtCurrency } from '../utils/formatters'

const auth = useAuthStore()
const tab = ref('cashin')
const loading = ref(false)
const agent = ref(null)

const cashInForm = ref({ customerPhoneNumber: '', amount: '', currencyCode: 'USD', description: '', idempotencyKey: '' })
const cashOutForm = ref({ customerPhoneNumber: '', amount: '', currencyCode: 'USD', description: '', idempotencyKey: '' })
const topUpForm = ref({ agentCode: '', amount: '', currencyCode: 'USD', idempotencyKey: '' })

onMounted(async () => {
  if (auth.isAgent) {
    try { agent.value = (await api.getAgent()).data.data } catch (e) {}
  }
})

async function doCashIn() {
  loading.value = true
  try {
    const res = await api.cashIn(cashInForm.value)
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: `Cash In done. Ref: ${res.data.data.referenceNumber}`, type: 'success' } }))
    cashInForm.value = { customerPhoneNumber: '', amount: '', currencyCode: 'USD', description: '', idempotencyKey: '' }
    if (auth.isAgent) agent.value = (await api.getAgent()).data.data
  } catch (e) {}
  finally { loading.value = false }
}

async function doCashOut() {
  loading.value = true
  try {
    const res = await api.cashOut(cashOutForm.value)
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: `Cash Out done. Ref: ${res.data.data.referenceNumber}`, type: 'success' } }))
    cashOutForm.value = { customerPhoneNumber: '', amount: '', currencyCode: 'USD', description: '', idempotencyKey: '' }
    if (auth.isAgent) agent.value = (await api.getAgent()).data.data
  } catch (e) {}
  finally { loading.value = false }
}

async function doTopUp() {
  loading.value = true
  try {
    await api.topUpFloat(topUpForm.value)
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: 'Float topped up successfully', type: 'success' } }))
    topUpForm.value = { agentCode: '', amount: '', currencyCode: 'USD', idempotencyKey: '' }
  } catch (e) {}
  finally { loading.value = false }
}
</script>

<style scoped>
.agent-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; }
.agent-code { font-size: 1.1rem; font-weight: 800; color: #059669; }
.business { color: #4b5563; font-size: 0.95rem; }
.float-label { font-size: 0.8rem; color: #6b7280; text-transform: uppercase; font-weight: 600; }
.float-value { font-size: 1.4rem; font-weight: 800; }
.tabs { display: flex; gap: 0.5rem; flex-wrap: wrap; }
.tab { padding: 0.5rem 1rem; border: 1px solid #d1d5db; background: #fff; border-radius: 6px; cursor: pointer; font-weight: 600; color: #374151; }
.tab.active { background: #059669; color: #fff; border-color: #059669; }
</style>
