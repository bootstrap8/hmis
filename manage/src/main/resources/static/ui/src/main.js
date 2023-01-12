import {createApp} from 'vue'

import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import Store from './store'

import request from '@/network'

const app = createApp(App)
.use(ElementPlus, {locale: zhCn})
.use(Store)
.use(router)
.mount('#app')

// app.config.globalProperties.$store=Store;
