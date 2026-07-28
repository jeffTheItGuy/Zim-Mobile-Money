import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/LoginView.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('../views/RegisterView.vue'), meta: { public: true } },
  { path: '/', name: 'Dashboard', component: () => import('../views/DashboardView.vue') },
  { path: '/wallet', name: 'Wallet', component: () => import('../views/WalletView.vue') },
  { path: '/transfer', name: 'Transfer', component: () => import('../views/TransferView.vue') },
  { path: '/agent', name: 'Agent', component: () => import('../views/AgentView.vue') },
  { path: '/transactions', name: 'Transactions', component: () => import('../views/TransactionsView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.token) next('/login')
  else next()
})

export default router
