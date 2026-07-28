<template>
  <div>
    <h2 style="margin-bottom:1.25rem">Send Money</h2>
    <div class="card" style="max-width:520px">
      <form @submit.prevent="submit">
        <div class="form-group">
          <label>Recipient Phone</label>
          <input v-model="form.recipientPhoneNumber" placeholder="0772123456" required maxlength="15" />
        </div>
        <div class="form-group">
          <label>Amount</label>
          <input v-model.number="form.amount" type="number" step="0.01" min="0.01" required />
        </div>
        <div class="form-group">
          <label>Currency</label>
          <select v-model="form.currencyCode" required>
            <option value="USD">USD</option>
            <option value="ZIG">ZIG</option>
          </select>
        </div>
        <div class="form-group">
          <label>Description</label>
          <input v-model="form.description" placeholder="Optional" maxlength="255" />
        </div>
        <div class="form-group">
          <label>Idempotency Key</label>
          <input v-model="form.idempotencyKey" required maxlength="100" />
          <button type="button" class="btn btn-secondary" style="margin-top:0.5rem;font-size:0.8rem;padding:0.4rem 0.6rem" @click="generateKey">Generate Key</button>
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Processing...' : 'Send Transfer' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../services/api'

const loading = ref(false)
const form = ref({
  recipientPhoneNumber: '',
  amount: '',
  currencyCode: 'USD',
  description: '',
  idempotencyKey: ''
})

function generateKey() {
  form.value.idempotencyKey = crypto.randomUUID()
}

async function submit() {
  loading.value = true
  try {
    const res = await api.transfer(form.value)
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: `Transfer completed. Ref: ${res.data.data.referenceNumber}`, type: 'success' } }))
    form.value = { recipientPhoneNumber: '', amount: '', currencyCode: 'USD', description: '', idempotencyKey: '' }
  } catch (e) {}
  finally { loading.value = false }
}
</script>
