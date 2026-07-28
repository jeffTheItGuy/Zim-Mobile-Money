import axios from 'axios'
import { useAuthStore } from '../stores/auth'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(config => {
  const auth = useAuthStore()
  if (auth.token) config.headers.Authorization = `Bearer ${auth.token}`
  return config
})

api.interceptors.response.use(
  r => r,
  err => {
    const msg = err.response?.data?.message || err.message || 'Request failed'
    window.dispatchEvent(new CustomEvent('toast', { detail: { message: msg, type: 'error' } }))
    return Promise.reject(err)
  }
)

export default {
  // Auth
  login: (phone, pin) => api.post('/auth/login', { phoneNumber: phone, pin }),

  // Users
  register: (data) => api.post('/users', data),
  getUser: (id) => api.get(`/users/${id}`),
  getUserByPhone: (phone) => api.get(`/users/phone/${phone}`),

  // Wallets
  getWallets: () => api.get('/wallets/my'),
  getWallet: (id) => api.get(`/wallets/${id}`),

  // Transactions
  transfer: (data) => api.post('/transactions/transfer', data),
  cashIn: (data) => api.post('/transactions/cash-in', data),
  cashOut: (data) => api.post('/transactions/cash-out', data),
  getTransactions: (walletId, page = 0, size = 20) =>
    api.get(`/transactions/wallet/${walletId}?page=${page}&size=${size}`),
  getTransaction: (id) => api.get(`/transactions/${id}`),

  // Agents
  getAgent: () => api.get('/agents/my'),
  topUpFloat: (data) => api.post('/transactions/agent-float-topup', data),
}
