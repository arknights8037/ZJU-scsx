import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, error => Promise.reject(error))

request.interceptors.response.use(response => {
  const { data } = response
  if (data.code === 401) {
    localStorage.removeItem('admin_token')
    window.location.href = '/login'
    return Promise.reject(new Error(data.message))
  }
  if (data.code !== 200) { ElMessage.error(data.message || '请求失败'); return Promise.reject(new Error(data.message)) }
  return data
}, error => {
  if (error.response) {
    const { status } = error.response
    if (status === 401 || status === 403) {
      localStorage.removeItem('admin_token')
      window.location.href = '/login'
      return Promise.reject(error)
    }
  }
  ElMessage.error('网络错误'); return Promise.reject(error)
})

export default request
