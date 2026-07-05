/**
 * 管理端 API 接口模块。
 * 页面组件只调用此模块中的语义化方法，不直接拼接后端 URL。
 * 当后端路径调整时，只需在此集中修改，降低维护成本。
 */
import request from './request'

// ========== 认证 ==========

/**
 * 登录接口。
 * @param {string} phone - 管理员手机号
 * @param {string} password - 登录密码
 * @returns {Promise} 返回登录结果，包含 token 和用户信息
 */
export const login = (phone, password) => request.post('/auth/login', { phone, password })
/**
 * 获取当前登录用户信息。
 * @returns {Promise} 返回当前用户信息对象
 */
export const getUserInfo = () => request.get('/auth/user-info')
/**
 * 全局搜索接口。
 * @param {string} keyword - 搜索关键词
 * @returns {Promise} 返回搜索结果列表
 */
export const globalSearch = (keyword) => request.get('/admin/global-search', { params: { keyword } })

// ========== 菜单管理 ==========

/**
 * 获取菜单列表。
 * @returns {Promise} 返回菜单树形列表
 */
export const getMenuList = () => request.get('/admin/menu/list')
/**
 * 新增或更新菜单。
 * @param {Object} data - 菜单数据（id、name、path、parentId 等）
 * @returns {Promise} 返回操作结果
 */
export const saveMenu = (data) => request.post('/admin/menu', data)
/**
 * 删除指定菜单。
 * @param {number|string} id - 菜单 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteMenu = (id) => request.delete(`/admin/menu/${id}`)

// ========== 角色管理 ==========

/**
 * 获取角色列表。
 * @returns {Promise} 返回角色列表
 */
export const getRoleList = () => request.get('/admin/role/list')
/**
 * 新增或更新角色。
 * @param {Object} data - 角色数据（id、name、description 等）
 * @returns {Promise} 返回操作结果
 */
export const saveRole = (data) => request.post('/admin/role', data)
/**
 * 删除指定角色。
 * @param {number|string} id - 角色 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteRole = (id) => request.delete(`/admin/role/${id}`)
/**
 * 获取指定角色的菜单权限列表。
 * @param {number|string} roleId - 角色 ID
 * @returns {Promise} 返回已授权的菜单 ID 列表
 */
export const getRoleMenus = (roleId) => request.get(`/admin/role/${roleId}/menus`)
/**
 * 设置指定角色的菜单权限。
 * @param {number|string} roleId - 角色 ID
 * @param {number[]} menuIds - 菜单 ID 数组
 * @returns {Promise} 返回操作结果
 */
export const setRoleMenus = (roleId, menuIds) => request.post(`/admin/role/${roleId}/menus`, menuIds)

// ========== 用户管理 ==========

/**
 * 分页查询用户列表。
 * @param {Object} params - 查询参数（page、size、keyword 等）
 * @returns {Promise} 返回分页用户数据
 */
export const getUserPage = (params) => request.get('/admin/user/page', { params })
/**
 * 新增或更新用户。
 * @param {Object} data - 用户数据
 * @returns {Promise} 返回操作结果
 */
export const saveUser = (data) => request.post('/admin/user', data)
/**
 * 更新用户状态（启用/禁用）。
 * @param {number|string} id - 用户 ID
 * @param {string} status - 目标状态值
 * @returns {Promise} 返回操作结果
 */
export const updateUserStatus = (id, status) => request.put(`/admin/user/${id}/status`, null, { params: { status } })
/**
 * 获取指定用户已分配的角色。
 * @param {number|string} userId - 用户 ID
 * @returns {Promise} 返回角色 ID 列表
 */
export const getUserRoles = (userId) => request.get(`/admin/user/${userId}/roles`)
/**
 * 设置指定用户的角色分配。
 * @param {number|string} userId - 用户 ID
 * @param {number[]} roleIds - 角色 ID 数组
 * @returns {Promise} 返回操作结果
 */
export const setUserRoles = (userId, roleIds) => request.post(`/admin/user/${userId}/roles`, roleIds)

// ========== 区域管理 ==========

/**
 * 获取区域列表。
 * @returns {Promise} 返回区域列表树形数据
 */
export const getAreaList = () => request.get('/admin/area/list')
/**
 * 新增或更新区域。
 * @param {Object} data - 区域数据
 * @returns {Promise} 返回操作结果
 */
export const saveArea = (data) => request.post('/admin/area', data)

// ========== 门店管理 ==========

/**
 * 分页查询门店列表。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页门店数据
 */
export const getStorePage = (params) => request.get('/admin/store/page', { params })
/**
 * 新增或更新门店。
 * @param {Object} data - 门店数据
 * @returns {Promise} 返回操作结果
 */
export const saveStore = (data) => request.post('/admin/store', data)
/**
 * 删除指定门店。
 * @param {number|string} id - 门店 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteStore = (id) => request.delete(`/admin/store/${id}`)
/**
 * 获取指定门店的商品列表。
 * @param {number|string} storeNo - 门店编号
 * @returns {Promise} 返回该门店的商品列表
 */
export const getStoreGoods = (storeNo) => request.get(`/admin/store/${storeNo}/goods`)
/**
 * 保存指定门店的商品配置。
 * @param {number|string} storeNo - 门店编号
 * @param {Object[]} data - 商品配置数据
 * @returns {Promise} 返回操作结果
 */
export const saveStoreGoods = (storeNo, data) => request.post(`/admin/store/${storeNo}/goods`, data)
/**
 * 删除指定门店的某个商品配置。
 * @param {number|string} storeNo - 门店编号
 * @param {number|string} id - 商品配置 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteStoreGoods = (storeNo, id) => request.delete(`/admin/store/${storeNo}/goods/${id}`)

// ========== 通知公告 ==========

/**
 * 分页查询通知公告。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页通知公告数据
 */
export const getNoticePage = (params) => request.get('/admin/community/notice/page', { params })
/**
 * 新增或更新通知公告。
 * @param {Object} data - 通知公告数据
 * @returns {Promise} 返回操作结果
 */
export const saveNotice = (data) => request.post('/admin/community/notice', data)
/**
 * 删除指定通知公告。
 * @param {number|string} id - 通知公告 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteNotice = (id) => request.delete(`/admin/community/notice/${id}`)

// ========== 车位管理 ==========

/**
 * 分页查询车位信息。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页车位数据
 */
export const getParkingPage = (params) => request.get('/admin/community/parking/page', { params })
/**
 * 新增或更新车位信息。
 * @param {Object} data - 车位数据
 * @returns {Promise} 返回操作结果
 */
export const saveParking = (data) => request.post('/admin/community/parking', data)

// ========== 投诉报修 ==========

/**
 * 分页查询投诉报修记录。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页投诉数据
 */
export const getComplaintPage = (params) => request.get('/admin/community/complaint/page', { params })
/**
 * 更新投诉报修处理状态。
 * @param {number|string} id - 投诉记录 ID
 * @param {string} status - 目标状态（如 resolved/rejected）
 * @returns {Promise} 返回操作结果
 */
export const updateComplaint = (id, status) => request.put(`/admin/community/complaint/${id}`, null, { params: { status } })

// ========== 访客记录 ==========

/**
 * 分页查询访客记录。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页访客数据
 */
export const getVisitorPage = (params) => request.get('/admin/community/visitor/page', { params })
/**
 * 更新访客状态（如已到访/已离开）。
 * @param {number|string} id - 访客记录 ID
 * @param {string} status - 目标状态值
 * @returns {Promise} 返回操作结果
 */
export const updateVisitorStatus = (id, status) => request.put(`/admin/community/visitor/${id}`, null, { params: { status } })

// ========== 缴费管理 ==========

/**
 * 分页查询缴费记录。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页缴费数据
 */
export const getChargePage = (params) => request.get('/admin/community/charge/page', { params })
/**
 * 获取指定缴费单的明细。
 * @param {string} chargeNo - 缴费单号
 * @returns {Promise} 返回缴费明细数据
 */
export const getChargeDetails = (chargeNo) => request.get(`/admin/community/charge/${chargeNo}/details`)

// ========== 商品管理 ==========

/**
 * 分页查询商品列表。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页商品数据
 */
export const getGoodsPage = (params) => request.get('/admin/goods/page', { params })
/**
 * 新增或更新商品。
 * @param {Object} data - 商品数据
 * @returns {Promise} 返回操作结果
 */
export const saveGoods = (data) => request.post('/admin/goods', data)
/**
 * 删除指定商品。
 * @param {number|string} id - 商品 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteGoods = (id) => request.delete(`/admin/goods/${id}`)
/**
 * 上传商品图片。
 * 使用 FormData 格式提交文件，支持 multipart/form-data 内容类型。
 * @param {File} file - 待上传的图片文件对象
 * @returns {Promise} 返回上传结果，包含图片 URL
 */
export const uploadGoodsImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/file/image', formData)
}

// ========== 商品分类 ==========

/**
 * 获取分类列表。
 * @returns {Promise} 返回分类树形列表
 */
export const getCategoryList = () => request.get('/admin/category/list')
/**
 * 新增或更新分类。
 * @param {Object} data - 分类数据
 * @returns {Promise} 返回操作结果
 */
export const saveCategory = (data) => request.post('/admin/category', data)
/**
 * 删除指定分类。
 * @param {number|string} id - 分类 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteCategory = (id) => request.delete(`/admin/category/${id}`)

// ========== 订单管理 ==========

/**
 * 分页查询订单列表。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页订单数据
 */
export const getOrderPage = (params) => request.get('/admin/order/page', { params })
/**
 * 获取订单仪表盘统计数据。
 * @param {Object} params - 查询参数（日期范围等）
 * @returns {Promise} 返回订单统计数据
 */
export const getOrderDashboard = (params) => request.get('/admin/order/dashboard', { params })
/**
 * 获取指定订单的详细信息。
 * @param {string} orderNo - 订单号
 * @returns {Promise} 返回订单详情
 */
export const getOrderDetails = (orderNo) => request.get(`/admin/order/${orderNo}/details`)
/**
 * 审批通过订单退款申请。
 * @param {string} orderNo - 订单号
 * @returns {Promise} 返回操作结果
 */
export const approveOrderRefund = (orderNo) => request.put(`/admin/order/${orderNo}/refund/approve`)

// ========== 系统日志与待办 ==========

/**
 * 分页查询系统操作日志。
 * @param {Object} params - 查询参数
 * @returns {Promise} 返回分页日志数据
 */
export const getSystemLogPage = (params) => request.get('/admin/system-log/page', { params })
/**
 * 获取管理端待办事项列表。
 * @returns {Promise} 返回待办列表
 */
export const getTodoList = () => request.get('/admin/todo/list')

// ========== 促销活动 ==========

/**
 * 获取促销活动列表。
 * @returns {Promise} 返回促销列表
 */
export const getSpecialList = () => request.get('/admin/special/list')
/**
 * 获取促销活动详情。
 * @param {number|string} id - 促销活动 ID
 * @returns {Promise} 返回促销详情
 */
export const getSpecialDetail = (id) => request.get(`/admin/special/${id}`)
/**
 * 新增或更新促销活动。
 * @param {Object} data - 促销活动数据
 * @returns {Promise} 返回操作结果
 */
export const saveSpecial = (data) => request.post('/admin/special', data)
/**
 * 删除指定促销活动。
 * @param {number|string} id - 促销活动 ID
 * @returns {Promise} 返回操作结果
 */
export const deleteSpecial = (id) => request.delete(`/admin/special/${id}`)
