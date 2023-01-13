import {createRouter, createWebHistory} from 'vue-router'

const routes = [
  {
    path: '/home',
    name: '主页',
    component: () => import("@/App.vue")
  },
  {
    path: '/route/list',
    name: '路由列表',
    component: () => import('@/views/route/RouteList.vue')
  },
  {
    path: '/route/info',
    name: '路由详情',
    component: () => import('@/views/route/RouteInfo.vue')
  },
  {
    path: '/dict/list',
    name: '字典列表',
    component: () => import('@/views/dict/DictList.vue')
  },
  {
    path: '/dict/info',
    name: '字典详情',
    component: () => import('@/views/dict/DictInfo.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
