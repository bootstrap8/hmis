import {createRouter, createWebHistory, createWebHashHistory} from 'vue-router'

const routes = [
  {
    path: '/default',
    name: '主页',
    meta: {breadcrumb: []},
    component: () => import("@/views/HomeView.vue")
  },
  {
    path: '/home',
    name: '主页',
    meta: {breadcrumb: ['首页']},
    component: () => import("@/App.vue")
  },
  {
    path: '/route/list',
    name: '路由列表',
    meta: {breadcrumb: ['路由管理', '路由配置']},
    component: () => import('@/views/route/RouteList.vue')
  },
  {
    path: '/route/info',
    name: '路由详情',
    meta: {breadcrumb: ['路由管理', '路由配置', '路由详情']},
    component: () => import('@/views/route/RouteInfo.vue')
  },
  {
    path: '/dict/list',
    name: '字典列表',
    meta: {breadcrumb: ['字典管理', '字典配置']},
    component: () => import('@/views/dict/DictList.vue')
  },
  {
    path: '/dict/info',
    name: '字典详情',
    meta: {breadcrumb: ['字典管理', '字典配置', '字典详情']},
    component: () => import('@/views/dict/DictInfo.vue')
  },
  {
    path: '/agent/kafkaIn/appList',
    name: 'kafka入口消息应用列表',
    meta: {breadcrumb: ['指标监控', '流控管理']},
    component: () => import('@/views/agent/kafkaIn/AppList.vue')
  },
  {
    path: '/agent/kafkaIn/instanceList',
    name: 'kafka入口消息应用实例列表',
    meta: {breadcrumb: ['指标监控', '流控管理', '实例列表']},
    component: () => import('@/views/agent/kafkaIn/InstanceList.vue')
  },
  {
    path: '/monitor/quotaList',
    name: '指标列表',
    meta: {breadcrumb: ['指标监控', '指标列表']},
    component: () => import('@/views/monitor/QuotaList.vue')
  },
  {
    path: '/monitor/instanceList',
    name: '指标应用实例列表',
    meta: {breadcrumb: ['指标监控', '指标列表', '实例列表']},
    component: () => import('@/views/monitor/InstanceList.vue')
  },
  {
    path: '/open-ai/chatgpt',
    name: 'ChatGBT人工智能',
    meta: {breadcrumb: ['Open AI', 'Chat-GPT']},
    component: () => import('@/views/chatgpt/ChatGPT.vue')
  }
]

const router = createRouter({
  // history: createWebHistory(process.env.BASE_URL),
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

export default router
