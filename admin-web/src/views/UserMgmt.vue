<!--
  组件名称：UserMgmt
  功能描述：后台用户管理页面，展示用户列表、新增/编辑用户、分配角色、切换用户状态
  路由路径：/admin/auth/user
-->
<template>
  <div class=”page-user”>
    <h2>用户管理</h2>
    <!-- 操作工具栏 -->
    <div class=”page-toolbar”>
      <el-button type=”primary” @click=”openEdit({})”>新增用户</el-button>
    </div>
    <!-- 用户列表表格 -->
    <div class=”table-wrap”>
      <el-table :data=”list” stripe>
        <el-table-column prop=”phone” label=”手机号” min-width=”130” />
        <el-table-column prop=”userName” label=”用户名” min-width=”120” />
        <!-- 业主门牌号：优先展示完整地址，没有则用楼栋/单元/门牌拼接 -->
        <el-table-column label=”业主门牌” min-width=”190” show-overflow-tooltip>
          <template #default=”{ row }”>{{ residenceSummary(row) || '未填写' }}</template>
        </el-table-column>
        <el-table-column prop=”mail” label=”邮箱” min-width=”160” show-overflow-tooltip />
        <el-table-column label=”紧急联系” min-width=”150” show-overflow-tooltip>
          <template #default=”{ row }”>
            {{ row.emergencyContact || '未填写' }}
            <span v-if=”row.emergencyPhone”> / {{ row.emergencyPhone }}</span>
          </template>
        </el-table-column>
        <!-- 用户状态开关 -->
        <el-table-column label=”状态” min-width=”80”>
          <template #default=”{ row }”>
            <el-switch :model-value=”row.userStatus === 1” @change=”toggleStatus(row)” />
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column label=”操作” min-width=”160”>
          <template #default=”{ row }”>
            <el-button size=”small” @click=”openRoles(row)”>角色</el-button>
            <el-button size=”small” @click=”openEdit(row)”>编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog v-model=”editVisible” :title=”editForm.id ? '编辑' : '新增'” :close-on-click-modal=”false”>
      <el-form :model=”editForm” label-position=”top”>
        <div class=”form-grid”>
          <el-form-item label=”手机号”><el-input v-model=”editForm.phone” /></el-form-item>
          <el-form-item label=”用户名”><el-input v-model=”editForm.userName” /></el-form-item>
        </div>
        <!-- 新增时才显示密码输入 -->
        <el-form-item v-if=”!editForm.id” label=”密码”>
          <el-input v-model=”editForm.userPassword” type=”password” />
        </el-form-item>
        <div class=”form-grid”>
          <el-form-item label=”性别”>
            <el-select v-model=”editForm.sex” clearable placeholder=”请选择”>
              <el-option label=”男” value=”男” />
              <el-option label=”女” value=”女” />
            </el-select>
          </el-form-item>
          <el-form-item label=”邮箱”><el-input v-model=”editForm.mail” /></el-form-item>
        </div>
        <!-- 住户信息区域 -->
        <el-divider content-position=”left”>住户信息</el-divider>
        <div class=”form-grid”>
          <el-form-item label=”业主姓名”><el-input v-model=”editForm.ownerName” placeholder=”例如：张三” /></el-form-item>
          <el-form-item label=”完整住址”><el-input v-model=”editForm.fullAddress” placeholder=”例如：智慧社区 3栋2单元1201” /></el-form-item>
          <el-form-item label=”楼栋号”><el-input v-model=”editForm.buildingNo” placeholder=”例如：3栋” /></el-form-item>
          <el-form-item label=”单元号”><el-input v-model=”editForm.unitNo” placeholder=”例如：2单元” /></el-form-item>
          <el-form-item label=”门牌号”><el-input v-model=”editForm.roomNo” placeholder=”例如：1201” /></el-form-item>
          <el-form-item label=”紧急联系人”><el-input v-model=”editForm.emergencyContact” /></el-form-item>
          <el-form-item label=”紧急联系电话”><el-input v-model=”editForm.emergencyPhone” /></el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click=”editVisible = false”>取消</el-button>
        <el-button type=”primary” @click=”saveEdit”>确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model=”roleVisible” title=”分配角色” :close-on-click-modal=”false”>
      <el-checkbox-group v-model=”selectedRoles”>
        <el-checkbox v-for=”r in allRoles” :key=”r.id” :value=”r.id” :label=”r.roleName” />
      </el-checkbox-group>
      <template #footer>
        <el-button @click=”roleVisible = false”>取消</el-button>
        <el-button type=”primary” @click=”saveRoles”>保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * UserMgmt - 后台用户管理
 *
 * 功能：
 * - 分页展示用户列表（含住户资料）
 * - 新增/编辑用户（账号+住户信息）
 * - 分配角色权限
 * - 启用/禁用用户状态
 */

import { ref, onMounted } from 'vue'
import { getUserPage, saveUser, updateUserStatus, getUserRoles, setUserRoles, getRoleList } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])              // 用户列表数据
const editVisible = ref(false)    // 编辑弹窗可见性
const editForm = ref({})          // 编辑表单数据
const roleVisible = ref(false)    // 角色弹窗可见性
const allRoles = ref([])          // 全部角色列表
const selectedRoles = ref([])     // 当前用户已选角色 ID 列表
const currentUser = ref(null)     // 当前操作的用户

onMounted(loadData)

/** 加载用户列表 */
async function loadData() {
  // 用户列表接口已经在后端合并了 user 表和 user_resident_profile 表，所以这里可以直接展示住户字段。
  const res = await getUserPage({ page: 1, size: 50 })
  list.value = res?.data?.records || []
}

/**
 * 打开新增/编辑用户弹窗
 * @param {Object} row - 用户数据（空对象时表示新增）
 */
function openEdit(row) {
  // 新增用户时 row 是空对象，默认启用；编辑用户时把当前行完整复制到弹窗表单。
  editForm.value = {
    userStatus: 1,
    ...row
  }
  editVisible.value = true
}

/** 保存用户（新增或编辑） */
async function saveEdit() {
  // 同一个保存接口同时保存账号资料和住户资料，后端会拆分到不同数据表。
  await saveUser(editForm.value)
  editVisible.value = false
  ElMessage.success('保存成功')
  loadData()
}

/**
 * 切换用户启用/禁用状态
 * @param {Object} row - 用户行数据
 */
async function toggleStatus(row) {
  // 这里不直接 v-model 修改状态，先调用后端成功后再更新本地行，避免界面和数据库不一致。
  const newStatus = row.userStatus === 1 ? 2 : 1
  await updateUserStatus(row.id, newStatus)
  row.userStatus = newStatus
  ElMessage.success('状态已更新')
}

/**
 * 打开分配角色弹窗
 * 需要同时加载当前用户已有角色和系统全部角色
 * @param {Object} row - 用户行数据
 */
async function openRoles(row) {
  // 角色弹窗需要同时加载”当前用户已有角色”和”系统全部角色”。
  currentUser.value = row
  const [ur, ar] = await Promise.all([getUserRoles(row.id), getRoleList()])
  allRoles.value = ar?.data || []
  selectedRoles.value = ur?.data || []
  roleVisible.value = true
}

/** 保存用户角色分配 */
async function saveRoles() {
  await setUserRoles(currentUser.value.id, selectedRoles.value)
  roleVisible.value = false
  ElMessage.success('角色已保存')
}

/**
 * 拼接住户门牌号摘要
 * @param {Object} row - 用户行数据
 * @returns {string} 地址摘要
 */
function residenceSummary(row) {
  // 优先展示用户手动维护的完整地址；没有完整地址时用楼栋/单元/门牌号拼接。
  if (row.fullAddress) return row.fullAddress
  return [row.buildingNo, row.unitNo, row.roomNo].filter(Boolean).join('')
}
</script>

<style scoped>
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 14px;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
