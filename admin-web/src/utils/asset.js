/**
 * 静态资源 URL 工具模块。
 * 根据传入的原始路径，自动判断并拼接完整的可访问 URL。
 * 在开发环境下，后端托管的静态资源需加上后端地址前缀以解决跨域问题。
 */

/** 本地开发后端源地址，用于拼接后端静态资源URL */
const DEV_BACKEND_ORIGIN = 'http://localhost:8082'

/**
 * 将资源原始路径转换为完整可访问 URL。
 * 支持三种场景：绝对 URL（直接返回）、后端静态资源（开发环境加域名前缀）、普通路径（直接使用）。
 * @param {string} value - 原始资源路径，可能是相对路径或绝对 URL
 * @returns {string} 完整的可访问资源 URL，传入空值则返回空字符串
 */
export function assetUrl(value) {
  // 空值直接返回空字符串，避免后续拼接出错
  if (!value) return ''
  const url = String(value).trim()
  if (!url) return ''
  // 已经是完整 URL（http/https/data:/blob:）则直接返回，无需处理
  if (/^(https?:)?\/\//i.test(url) || url.startsWith('data:') || url.startsWith('blob:')) {
    return url
  }

  // 将 /community/ 前缀标准化为 /community-products/，兼容后端资源目录结构
  const normalized = url.replace(/^\/community\//, '/community-products/')
  const path = normalized.startsWith('/') ? normalized : `/${normalized}`
  // 判断是否为后端托管的静态资源路径
  const backendStatic = path.startsWith('/community-products/')
    || path.startsWith('/mall-uploads/')
    || path.startsWith('/admin-uploads/')

  // 开发环境下后端静态资源需补全后端源地址，生产环境直接使用相对路径
  return import.meta.env.DEV && backendStatic ? `${DEV_BACKEND_ORIGIN}${path}` : path
}
