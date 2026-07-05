/**
 * 居民端 Axios 请求实例模块。
 * 创建独立的 HTTP 请求实例，与管理端使用不同的 Token 存储 key 和登录跳转地址。
 * 所有 /portal 居民端页面的 API 调用都经过此实例。
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 居民端 Axios 请求实例。
 * 与管理端 request 分开管理，使用不同的 Token key（token）和不同的登录跳转地址（/portal/login）。
 */
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：每次请求前把居民端 token 放到 Authorization 头中。
// 后端 JwtAuthenticationFilter 会解析这个 token，并把 userId 注入 request。
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器：统一处理后端 Result 包装格式。
request.interceptors.response.use(
  response => {
    const { data } = response
    if (data.code === 401) {
      // 登录过期时保留当前路径，登录成功后可以回到原页面继续操作。
      localStorage.removeItem('token')
      const redirect = encodeURIComponent(window.location.pathname + window.location.search)
      window.location.href = `/portal/login?redirect=${redirect}`
      return Promise.reject(new Error(data.message))
    }
    if (data.code !== 200) {
      // 业务异常，例如库存不足、原密码错误等，在这里统一弹出。
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401 || status === 403) {
        // Spring Security 在进入 Controller 前拦截的异常会走 HTTP 状态码分支。
        localStorage.removeItem('token')
        const redirect = encodeURIComponent(window.location.pathname + window.location.search)
        window.location.href = `/portal/login?redirect=${redirect}`
        return Promise.reject(error)
      }
    }
    ElMessage.error('网络错误，请稍后再试')
    return Promise.reject(error)
  }
)

export default request
