<template>
  <div class="auth-page">
    <div class="card auth-box">
      <div class="brand-center">
        <span class="flag">🇿🇼</span>
        <h2>Zim Mobile Money</h2>
        <p>Sign in with your phone & PIN</p>
      </div>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Phone Number</label>
          <input v-model="phone" placeholder="e.g. 0772123456" required />
        </div>
        <div class="form-group">
          <label>PIN</label>
          <input v-model="pin" type="password" placeholder="4-6 digits" required minlength="4" maxlength="6" />
        </div>
        <button type="submit" class="btn btn-primary" style="width:100%" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign In' }}
        </button>
        <p class="switch">No account? <router-link to="/register">Register</router-link></p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const phone = ref('')
const pin = ref('')
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()

async function handleLogin() {
  loading.value = true
  try {
    await auth.login(phone.value, pin.value)
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: 'Welcome back!', type: 'success' } }))
    router.push('/')
  } catch (e) { /* interceptor shows toast */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.auth-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 1rem; }
.auth-box { width: 100%; max-width: 380px; }
.brand-center { text-align: center; margin-bottom: 1.5rem; }
.brand-center .flag { font-size: 2.5rem; }
.brand-center h2 { margin-top: 0.5rem; font-size: 1.4rem; }
.brand-center p { color: #6b7280; font-size: 0.9rem; margin-top: 0.25rem; }
.switch { text-align: center; margin-top: 1rem; font-size: 0.9rem; color: #4b5563; }
.switch a { color: #059669; font-weight: 600; text-decoration: none; }
</style>
