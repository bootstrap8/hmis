import {createRouter, createWebHashHistory} from 'vue-router'

const routes = [
  {
    path: '',
    name: '配置中心UI控制台',
    meta: {breadcrumb: ['配置管理', 'UI控制台']},
    component: () => import('@/views/config/main.vue')
  }
]

const router = createRouter({
  // history: createWebHistory(process.env.BASE_URL),
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

export default router
