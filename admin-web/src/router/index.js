/**
 * Vue Router 路由配置模块。
 * 单前端应用内同时承载两套路由：
 * - /admin：物业/管理员后台，使用 AdminLayout 布局；
 * - /portal：居民端门户，使用 portal/MainLayout 布局。
 *
 * 前后端合并后只需启动一个前端项目，路由前缀负责区分两种使用场景。
 * 路由采用懒加载方式，每个页面组件在访问时才按需加载。
 */
import { createRouter, createWebHistory } from 'vue-router'

// 应用级路由表
const routes = [
  // 根路径默认重定向到居民端首页
  { path: '/', redirect: '/portal' },
  // ========== 管理端路由 ==========
  {
    path: '/admin',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '首页' } },
      { path: 'auth/role', name: 'RoleMgmt', component: () => import('@/views/RoleMgmt.vue'), meta: { title: '角色管理' } },
      { path: 'auth/user', name: 'UserMgmt', component: () => import('@/views/UserMgmt.vue'), meta: { title: '用户管理' } },
      { path: 'system/log', name: 'SystemLog', component: () => import('@/views/SystemLog.vue'), meta: { title: '系统日志' } },
      { path: 'goods/category', name: 'CategoryMgmt', component: () => import('@/views/CategoryMgmt.vue'), meta: { title: '类别管理' } },
      { path: 'goods/index', name: 'GoodsMgmt', component: () => import('@/views/GoodsMgmt.vue'), meta: { title: '商品管理' } },
      { path: 'area/index', name: 'AreaMgmt', component: () => import('@/views/AreaMgmt.vue'), meta: { title: '区域管理' } },
      { path: 'area/store', name: 'StoreMgmt', component: () => import('@/views/StoreMgmt.vue'), meta: { title: '门店管理' } },
      { path: 'order/dashboard', name: 'OrderDashboard', component: () => import('@/views/OrderDashboard.vue'), meta: { title: '订单仪表盘' } },
      { path: 'order/index', name: 'OrderMgmt', component: () => import('@/views/OrderMgmt.vue'), meta: { title: '订单列表' } },
      { path: 'special/index', name: 'SpecialMgmt', component: () => import('@/views/SpecialMgmt.vue'), meta: { title: '促销管理' } },
      { path: 'community/notice', name: 'NoticeMgmt', component: () => import('@/views/NoticeMgmt.vue'), meta: { title: '通知公告' } },
      { path: 'community/parking', name: 'ParkingMgmt', component: () => import('@/views/ParkingMgmt.vue'), meta: { title: '车位管理' } },
      { path: 'community/visitor', name: 'VisitorMgmt', component: () => import('@/views/VisitorMgmt.vue'), meta: { title: '访客记录' } },
      { path: 'community/complaint', name: 'ComplaintMgmt', component: () => import('@/views/ComplaintMgmt.vue'), meta: { title: '报事报修' } },
      { path: 'community/charge', name: 'ChargeMgmt', component: () => import('@/views/ChargeMgmt.vue'), meta: { title: '缴纳记录' } },
    ]
  },
  // 管理端登录页（独立于 AdminLayout 之外，不嵌套侧边栏）
  { path: '/admin/login', name: 'AdminLogin', component: () => import('@/views/Login.vue'), meta: { title: '管理端登录' } },
  // ========== 居民端路由 ==========
  {
    path: '/portal',
    component: () => import('@/portal/layout/MainLayout.vue'),
    children: [
      { path: '', name: 'PortalHome', component: () => import('@/portal/views/Home.vue'), meta: { title: '首页' } },
      { path: 'goods', name: 'GoodsList', component: () => import('@/portal/views/GoodsList.vue'), meta: { title: '商品列表' } },
      { path: 'goods/:goodsNo', name: 'GoodsDetail', component: () => import('@/portal/views/GoodsDetail.vue'), meta: { title: '商品详情' } },
      { path: 'community', name: 'Community', component: () => import('@/portal/views/Community.vue'), meta: { title: '社区服务' } },
      { path: 'profile', name: 'Profile', component: () => import('@/portal/views/Profile.vue'), meta: { title: '个人中心' } },
      { path: 'favorite', name: 'Favorite', component: () => import('@/portal/views/Favorite.vue'), meta: { title: '我的收藏' } },
      { path: 'cart', name: 'Cart', component: () => import('@/portal/views/Cart.vue'), meta: { title: '购物车' } },
      { path: 'order', name: 'Order', component: () => import('@/portal/views/Order.vue'), meta: { title: '我的订单' } },
    ]
  },
  // 居民端登录页（独立于 MainLayout 之外）
  { path: '/portal/login', name: 'PortalLogin', component: () => import('@/portal/views/Login.vue'), meta: { title: '居民端登录' } },
  // 任意未知地址都回到居民端首页，避免刷新旧链接时出现空白页。
  { path: '/:pathMatch(.*)*', redirect: '/portal' },
]

/**
 * 创建 Vue Router 实例。
 * 使用 HTML5 History 模式（而非 Hash 模式），URL 更简洁。
 * 生产环境需要后端或 Nginx 将未知前端路径统一回退到 index.html。
 */
const router = createRouter({ history: createWebHistory(), routes })
export default router
