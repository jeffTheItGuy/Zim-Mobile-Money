import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../services/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAgent = computed(() => user.value?.userType === 'AGENT')
  const isAdmin = computed(() => user.value?.userType === 'ADMIN')

  async function login(phone, pin) {
    const res = await api.login(phone, pin)
    const { token: t } = res.data.data
    token.value = t
    localStorage.setItem('token', t)
    // decode basic user info from JWT payload (no verification, just display)
    const payload = JSON.parse(atob(t.split('.')[1]))
    user.value = { userId: payload.sub, phone: payload.phone, role: payload.role }
    localStorage.setItem('user', JSON.stringify(user.value))
    return res
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login'
  }

  return { token, user, isLoggedIn, isAgent, isAdmin, login, logout }
})
