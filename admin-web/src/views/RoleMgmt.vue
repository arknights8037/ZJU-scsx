<!--
  组件名称：RoleMgmt
  功能描述：后台角色管理页面，支持角色列表、新增/编辑/删除角色、分配菜单权限
  路由路径：/admin/auth/role
-->
<template>
  <div class="page-role">
    <h2>角色管理</h2>
    <!-- 操作工具栏 -->
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增角色</el-button>
    </div>
    <!-- 角色列表 -->
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="roleName" label="角色名" min-width="120" />
        <el-table-column prop="roleDesc" label="描述" min-width="160" />
        <el-table-column label="操作" min-width="280">
          <template #default="{ row }">
            <el-button size="small" @click="openMenus(row)">分配菜单</el-button>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑角色' : '新增角色'" :close-on-click-modal="false">
      <el-form :model="editForm">
        <el-form-item label="角色名"><el-input v-model="editForm.roleName" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.roleDesc" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.roleState" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单弹窗（树形勾选） -->
    <el-dialog v-model="menuVisible" title="分配菜单" :close-on-click-modal="false">
      <el-tree :data="allMenus" show-checkbox node-key="id" ref="menuTree" default-expand-all />
      <template #footer>
        <el-button @click="menuVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMenus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * RoleMgmt - 后台角色管理
 *
 * 功能：
 * - 角色列表展示
 * - 新增/编辑/删除角色
 * - 使用 el-tree 分配菜单权限
 */

import { ref, onMounted } from 'vue'
import { getRoleList, saveRole, deleteRole, getMenuList, getRoleMenus, setRoleMenus } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])           // 角色列表
const editVisible = ref(false) // 编辑弹窗可见性
const editForm = ref({})       // 编辑表单数据
const menuVisible = ref(false) // 菜单分配弹窗可见性
const allMenus = ref([])       // 全部菜单树
const menuTree = ref(null)     // el-tree 实例引用
const currentRole = ref(null)  // 当前操作的角色

onMounted(loadData)

/** 加载角色列表 */
async function loadData() {
  const res = await getRoleList()
  list.value = res?.data || []
}

/**
 * 打开新增/编辑角色弹窗
 * @param {Object} row - 角色数据
 */
function openEdit(row) {
  editForm.value = { ...row }
  editVisible.value = true
}

/** 保存角色 */
async function saveEdit() {
  await saveRole(editForm.value)
  editVisible.value = false
  ElMessage.success('保存成功')
  loadData()
}

/**
 * 删除角色
 * @param {number|string} id - 角色 ID
 */
async function del(id) {
  await deleteRole(id)
  ElMessage.success('已删除')
  loadData()
}

/**
 * 打开分配菜单弹窗
 * 同时加载当前角色的已有菜单和系统全部菜单
 * @param {Object} role - 角色数据
 */
async function openMenus(role) {
  currentRole.value = role
  const [mr, ar] = await Promise.all([getRoleMenus(role.id), getMenuList()])
  allMenus.value = buildTree(ar?.data || [])
  editVisible.value = false
  menuVisible.value = true
  setTimeout(() => {
    menuTree.value?.setCheckedKeys(mr?.data || [])
  }, 100)
}

/**
 * 将扁平菜单列表构建为树形结构
 * @param {Array} menus - 扁平菜单数组
 * @returns {Array} 树形菜单
 */
function buildTree(menus) {
  const map = {}, roots = []
  menus.forEach(m => { map[m.id] = { ...m, label: m.menuName, children: [] } })
  menus.forEach(m => {
    if (m.parentId && map[m.parentId]) map[m.parentId].children.push(map[m.id])
    else roots.push(map[m.id])
  })
  return roots
}

/** 保存角色菜单权限 */
async function saveMenus() {
  const keys = menuTree.value.getCheckedKeys()
  await setRoleMenus(currentRole.value.id, keys)
  menuVisible.value = false
  ElMessage.success('菜单已分配')
}
</script>
