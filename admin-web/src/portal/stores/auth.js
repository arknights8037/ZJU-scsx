/**
 * 居民端认证状态管理模块。
 * 使用 Pinia 管理登录状态、用户信息和会话持久化。
 * Token 和用户信息同步存储于 localStorage，确保页面刷新后状态不丢失。
 * 与管理端的认证状态完全隔离，使用不同的 localStorage key。
 */
import { defineStore } from 'pinia'
import { getUserProfile, login as loginApi } from '@/portal/api'

/**
 * 从 localStorage 中读取已持久化的用户信息。
 * 页面初始化时用于恢复上次登录的用户状态。
 * @returns {{id: string, phone: string, userName: string, avatar: string}} 用户信息对象
 */
function readStoredUser() {
  return {
    id: localStorage.getItem('userId') || '',
    phone: localStorage.getItem('phone') || '',
    userName: localStorage.getItem('userName') || '',
    avatar: localStorage.getItem('avatar') || ''
  }
}

/**
 * 居民端认证 Pinia Store。
 * 提供登录、登出、加载/更新用户资料等操作，所有方法自动同步 localStorage。
 */
export const usePortalAuthStore = defineStore('portalAuth', {
  /** 状态：token 及用户基本信息，初始化时从 localStorage 恢复 */
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: readStoredUser()
  }),
  /** 计算属性：isLoggedIn（是否已登录）、displayName（展示用用户名） */
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    displayName: (state) => state.user.userName || state.user.phone || '用户'
  },
  /** 动作方法 */
  actions: {
    /**
     * 保存登录会话信息到 store 和 localStorage。
     * 在登录成功或 Token 刷新后调用。
     * @param {Object} [data={}] - 会话数据，包含 token、userId、phone、userName、avatar
     */
    setSession(data = {}) {
      this.token = data.token || ''
      this.user = {
        id: data.userId || data.id || '',
        phone: data.phone || '',
        userName: data.userName || '',
        avatar: data.avatar || ''
      }
      // 同步持久化到 localStorage，刷新后自动恢复
      localStorage.setItem('token', this.token)
      localStorage.setItem('userId', String(this.user.id || ''))
      localStorage.setItem('phone', this.user.phone || '')
      localStorage.setItem('userName', this.user.userName || '')
      localStorage.setItem('avatar', this.user.avatar || '')
    },
    /**
     * 执行登录操作。
     * 调用后端登录接口，成功后自动保存会话信息。
     * @param {string} phone - 手机号
     * @param {string} password - 密码
     * @returns {Promise} 返回登录接口响应结果
     */
    async login(phone, password) {
      const res = await loginApi(phone, password)
      this.setSession(res?.data || {})
      return res
    },
    /**
     * 从后端重新加载当前用户资料并更新 store。
     * 适用于页面初始化或用户信息变更后刷新。
     * @returns {Promise<Object|null>} 返回用户资料对象，未登录时返回 null
     */
    async loadProfile() {
      if (!this.token) return null
      const res = await getUserProfile()
      const user = res?.data || {}
      this.user = {
        id: user.id || this.user.id,
        phone: user.phone || '',
        userName: user.userName || '',
        avatar: user.avatar || ''
      }
      localStorage.setItem('userId', String(this.user.id || ''))
      localStorage.setItem('phone', this.user.phone || '')
      localStorage.setItem('userName', this.user.userName || '')
      localStorage.setItem('avatar', this.user.avatar || '')
      return user
    },
    /**
     * 局部更新当前用户信息（如修改昵称、头像后）。
     * 只覆盖传入的字段，未传入的字段保持原值。
     * @param {Object} [user={}] - 要更新的用户字段
     */
    updateUser(user = {}) {
      this.user = {
        ...this.user,
        id: user.id || this.user.id,
        phone: user.phone || this.user.phone,
        userName: user.userName ?? this.user.userName,
        avatar: user.avatar ?? this.user.avatar
      }
      localStorage.setItem('userName', this.user.userName || '')
      localStorage.setItem('avatar', this.user.avatar || '')
    },
    /**
     * 登出：清空 store 中的认证状态，并清除 localStorage 中的全部用户数据。
     */
    logout() {
      this.token = ''
      this.user = { id: '', phone: '', userName: '', avatar: '' }
      localStorage.removeItem('token')
      localStorage.removeItem('phone')
      localStorage.removeItem('userId')
      localStorage.removeItem('userName')
      localStorage.removeItem('avatar')
    }
  }
})
