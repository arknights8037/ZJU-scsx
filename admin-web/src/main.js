/**
 * 应用入口模块。
 * 负责初始化 Vue 应用实例，注册 Pinia 状态管理、Vue Router 路由、
 * Element Plus UI 库（中文语言包）及其图标组件。
 * 管理端和居民端共用同一个入口，通过路由前缀区分两种场景。
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'
import './style.css'

const app = createApp(App)

// Element Plus 图标全局注册，后台菜单、按钮和居民端页面可以直接按组件名使用。
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
// 注册 Pinia 状态管理库
app.use(createPinia())
// 注册 Vue Router 路由系统
app.use(router)
// 注册 Element Plus UI 库，使用简体中文作为组件默认语言
app.use(ElementPlus, { locale: zhCn })
// 挂载 Vue 应用实例到 #app DOM 节点
app.mount('#app')
