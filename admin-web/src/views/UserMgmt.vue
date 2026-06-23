<template>
  <div class="page-user">
    <h2>用户管理</h2>
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增用户</el-button>
    </div>
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="userName" label="用户名" min-width="120" />
        <el-table-column prop="mail" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" min-width="80">
          <template #default="{ row }">
            <el-switch :model-value="row.userStatus === 1" @change="toggleStatus(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openRoles(row)">角色</el-button>
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="editVisible" :title="editForm.id ? '编辑' : '新增'" :close-on-click-modal="false">
      <el-form :model="editForm">
        <el-form-item label="手机号"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="用户名"><el-input v-model="editForm.userName" /></el-form-item>
        <el-form-item v-if="!editForm.id" label="密码">
          <el-input v-model="editForm.userPassword" type="password" />
        </el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.mail" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleVisible" title="分配角色" :close-on-click-modal="false">
      <el-checkbox-group v-model="selectedRoles">
        <el-checkbox v-for="r in allRoles" :key="r.id" :value="r.id" :label="r.roleName" />
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserPage, saveUser, updateUserStatus, getUserRoles, setUserRoles, getRoleList } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])
const editVisible = ref(false)
const editForm = ref({})
const roleVisible = ref(false)
const allRoles = ref([])
const selectedRoles = ref([])
const currentUser = ref(null)

onMounted(loadData)
async function loadData() {
  const res = await getUserPage({ page: 1, size: 50 })
  list.value = res?.data?.records || []
}

function openEdit(row) {
  editForm.value = { ...row }
  editVisible.value = true
}

async function saveEdit() {
  await saveUser(editForm.value)
  editVisible.value = false
  ElMessage.success('保存成功')
  loadData()
}

async function toggleStatus(row) {
  const newStatus = row.userStatus === 1 ? 2 : 1
  await updateUserStatus(row.id, newStatus)
  row.userStatus = newStatus
  ElMessage.success('状态已更新')
}

async function openRoles(row) {
  currentUser.value = row
  const [ur, ar] = await Promise.all([getUserRoles(row.id), getRoleList()])
  allRoles.value = ar?.data || []
  selectedRoles.value = ur?.data || []
  roleVisible.value = true
}

async function saveRoles() {
  await setUserRoles(currentUser.value.id, selectedRoles.value)
  roleVisible.value = false
  ElMessage.success('角色已保存')
}
</script>
