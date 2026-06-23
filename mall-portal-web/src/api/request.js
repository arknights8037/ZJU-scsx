import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
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

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { data } = response
    if (data.code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error(data.message))
    }
    if (data.code !== 200) {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
    return data
  },
  error => {
    if (error.response) {
      const { status } = error.response
      if (status === 401 || status === 403) {
        localStorage.removeItem('token')
        window.location.href = '/login'
        return Promise.reject(error)
      }
    }
    ElMessage.error('网络错误，请稍后再试')
    return Promise.reject(error)
  }
)

export default request
