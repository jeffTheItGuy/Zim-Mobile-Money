<template>
  <div class="auth-page">
    <div class="card auth-box">
      <div class="brand-center">
        <span class="flag">🇿🇼</span>
        <h2>Create Account</h2>
      </div>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Phone Number</label>
          <input v-model="form.phoneNumber" placeholder="0772123456" required maxlength="15" />
        </div>
        <div class="form-group">
          <label>PIN (4-6 digits)</label>
          <input v-model="form.pin" type="password" required minlength="4" maxlength="6" />
        </div>
        <div class="form-group">
          <label>First Name</label>
          <input v-model="form.firstName" required maxlength="100" />
        </div>
        <div class="form-group">
          <label>Last Name</label>
          <input v-model="form.lastName" required maxlength="100" />
        </div>
        <div class="form-group">
          <label>National ID</label>
          <input v-model="form.nationalId" placeholder="Optional" maxlength="50" />
        </div>
        <div class="form-group">
          <label>Account Type</label>
          <select v-model="form.userType" required>
            <option value="CUSTOMER">Customer</option>
            <option value="AGENT">Agent</option>
            <option value="MERCHANT">Merchant</option>
          </select>
        </div>
        <button type="submit" class="btn btn-primary" style="width:100%" :disabled="loading">
          {{ loading ? 'Creating...' : 'Create Account' }}
        </button>
        <p class="switch">Have an account? <router-link to="/login">Sign in</router-link></p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api'

const router = useRouter()
const loading = ref(false)
const form = ref({
  phoneNumber: '', pin: '', firstName: '', lastName: '', nationalId: '', userType: 'CUSTOMER'
})

async function handleRegister() {
  loading.value = true
  try {
    await api.register(form.value)
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: 'Account created. Please sign in.', type: 'success' } }))
    router.push('/login')
  } catch (e) {}
  finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 1rem; }
.auth-box { width: 100%; max-width: 420px; }
.brand-center { text-align: center; margin-bottom: 1.25rem; }
.brand-center .flag { font-size: 2.5rem; }
.brand-center h2 { margin-top: 0.5rem; font-size: 1.4rem; }
.switch { text-align: center; margin-top: 1rem; font-size: 0.9rem; color: #4b5563; }
.switch a { color: #059669; font-weight: 600; text-decoration: none; }
</style>
