import request from './request'

export const login = (phone, password) => request.post('/auth/login', { phone, password })
export const getUserInfo = () => request.get('/auth/user-info')

export const getMenuList = () => request.get('/admin/menu/list')
export const saveMenu = (data) => request.post('/admin/menu', data)
export const deleteMenu = (id) => request.delete(`/admin/menu/${id}`)

export const getRoleList = () => request.get('/admin/role/list')
export const saveRole = (data) => request.post('/admin/role', data)
export const deleteRole = (id) => request.delete(`/admin/role/${id}`)
export const getRoleMenus = (roleId) => request.get(`/admin/role/${roleId}/menus`)
export const setRoleMenus = (roleId, menuIds) => request.post(`/admin/role/${roleId}/menus`, menuIds)

export const getUserPage = (params) => request.get('/admin/user/page', { params })
export const saveUser = (data) => request.post('/admin/user', data)
export const updateUserStatus = (id, status) => request.put(`/admin/user/${id}/status`, null, { params: { status } })
export const getUserRoles = (userId) => request.get(`/admin/user/${userId}/roles`)
export const setUserRoles = (userId, roleIds) => request.post(`/admin/user/${userId}/roles`, roleIds)

export const getAreaList = () => request.get('/admin/area/list')
export const saveArea = (data) => request.post('/admin/area', data)

export const getStorePage = (params) => request.get('/admin/store/page', { params })
export const saveStore = (data) => request.post('/admin/store', data)
export const deleteStore = (id) => request.delete(`/admin/store/${id}`)

export const getNoticePage = (params) => request.get('/admin/community/notice/page', { params })
export const saveNotice = (data) => request.post('/admin/community/notice', data)
export const deleteNotice = (id) => request.delete(`/admin/community/notice/${id}`)

export const getParkingPage = (params) => request.get('/admin/community/parking/page', { params })
export const saveParking = (data) => request.post('/admin/community/parking', data)

export const getComplaintPage = (params) => request.get('/admin/community/complaint/page', { params })
export const updateComplaint = (id, status) => request.put(`/admin/community/complaint/${id}`, null, { params: { status } })

export const getVisitorPage = (params) => request.get('/admin/community/visitor/page', { params })
export const getChargePage = (params) => request.get('/admin/community/charge/page', { params })
export const getChargeDetails = (chargeNo) => request.get(`/admin/community/charge/${chargeNo}/details`)

export const getGoodsPage = (params) => request.get('/admin/goods/page', { params })
export const saveGoods = (data) => request.post('/admin/goods', data)
export const deleteGoods = (id) => request.delete(`/admin/goods/${id}`)
export const uploadGoodsImage = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/admin/file/image', formData)
}

export const getCategoryList = () => request.get('/admin/category/list')
export const saveCategory = (data) => request.post('/admin/category', data)
export const deleteCategory = (id) => request.delete(`/admin/category/${id}`)

export const getOrderPage = (params) => request.get('/admin/order/page', { params })
export const getOrderDetails = (orderNo) => request.get(`/admin/order/${orderNo}/details`)

export const getSpecialList = () => request.get('/admin/special/list')
export const saveSpecial = (data) => request.post('/admin/special', data)
export const deleteSpecial = (id) => request.delete(`/admin/special/${id}`)
