import {createRouter, createWebHistory} from 'vue-router'

const routes = [
  {
    path: '/home',
    name: 'home',
    component: () => import("@/App.vue")
  },
  {
    path: '/route',
    name: 'route',
    children: [
      {
        path: 'list',
        name: 'list',
        component: () => import('@/views/route/RouteList.vue')
      },
      {
        path: 'info',
        name: 'info',
        component: () => import('@/views/route/RouteInfo.vue')
      }]
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
