<template>
  <div class="page-role">
    <h2>角色管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增角色</el-button>
    </div>
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
import { ref, onMounted } from 'vue'
import { getRoleList, saveRole, deleteRole, getMenuList, getRoleMenus, setRoleMenus } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const editVisible = ref(false)
const editForm = ref({})
const menuVisible = ref(false)
const allMenus = ref([])
const menuTree = ref(null)
const currentRole = ref(null)

onMounted(loadData)
async function loadData() {
  const res = await getRoleList()
  list.value = res?.data || []
}

function openEdit(row) {
  editForm.value = { ...row }
  editVisible.value = true
}

async function saveEdit() {
  await saveRole(editForm.value)
  editVisible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function del(id) {
  await deleteRole(id)
  ElMessage.success('已删除')
  loadData()
}

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

function buildTree(menus) {
  const map = {}, roots = []
  menus.forEach(m => { map[m.id] = { ...m, label: m.menuName, children: [] } })
  menus.forEach(m => {
    if (m.parentId && map[m.parentId]) map[m.parentId].children.push(map[m.id])
    else roots.push(map[m.id])
  })
  return roots
}

async function saveMenus() {
  const keys = menuTree.value.getCheckedKeys()
  await setRoleMenus(currentRole.value.id, keys)
  menuVisible.value = false
  ElMessage.success('菜单已分配')
}
</script>
