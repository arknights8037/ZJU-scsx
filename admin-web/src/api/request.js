/**
 * 管理端 Axios 请求实例模块。
 * 创建统一的 HTTP 请求实例，配置基础路径和超时时间。
 * 通过请求拦截器自动携带管理端 Token，通过响应拦截器统一处理业务异常和登录过期。
 * 所有 /admin 管理后台页面的 API 调用都经过此实例。
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 管理端 Axios 请求实例。
 * baseURL 固定为 /api，通过反向代理转发到后端服务。
 * 超时时间设为 10 秒，超出该时间未收到响应则自动中断请求。
 */
const request = axios.create({ baseURL: '/api', timeout: 10000 })

/**
 * 请求拦截器：在每次发送请求前，从 localStorage 中读取 admin_token 并注入到请求头。
 * 管理端和居民端使用不同 localStorage key（admin_token vs token），避免两个登录态互相覆盖。
 */
request.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, error => Promise.reject(error))

/**
 * 响应拦截器：统一处理后端返回的 Result 包装格式。
 * - code=401：Token 无效或已过期，清除本地 Token 并跳转管理端登录页。
 * - code!=200：业务异常（如参数校验失败），统一弹出错误提示并 reject。
 * - HTTP 401/403：Spring Security 拦截产生的响应，同样按登录失效处理。
 * - 其他网络错误：统一提示"网络错误"。
 */
request.interceptors.response.use(response => {
  const { data } = response
  // 后端业务 code=401 表示 Token 无效或未登录，直接回管理端登录页。
  if (data.code === 401) {
    localStorage.removeItem('admin_token')
    window.location.href = '/admin/login'
    return Promise.reject(new Error(data.message))
  }
  // 非 200 业务码统一弹提示，页面里就不用每个接口重复 try/catch 提示文案。
  if (data.code !== 200) { ElMessage.error(data.message || '请求失败'); return Promise.reject(new Error(data.message)) }
  return data
}, error => {
  // HTTP 层 401/403 也按登录失效处理，例如 Spring Security 直接拦截时会走这里。
  if (error.response) {
    const { status } = error.response
    if (status === 401 || status === 403) {
      localStorage.removeItem('admin_token')
      window.location.href = '/admin/login'
      return Promise.reject(error)
    }
  }
  ElMessage.error('网络错误'); return Promise.reject(error)
})

export default request
