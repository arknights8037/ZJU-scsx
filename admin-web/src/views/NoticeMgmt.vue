<!--
  组件名称：NoticeMgmt
  功能描述：后台通知公告管理页面，支持公告列表、新增/编辑/删除公告
  路由路径：/admin/community/notice
-->
<template>
  <div class="page-notice">
    <h2>通知公告</h2>
    <!-- 操作工具栏 -->
    <div class="page-toolbar">
      <el-button type="primary" @click="openEdit({})">新增通知</el-button>
    </div>
    <!-- 通知列表 -->
    <div class="table-wrap">
      <el-table :data="list" stripe>
        <el-table-column prop="noticeTitle" label="标题" min-width="200" />
        <el-table-column prop="noticeStatus" label="状态" min-width="100" />
        <el-table-column label="操作" min-width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑通知弹窗 -->
    <el-dialog v-model="visible" title="通知" width="600px" :close-on-click-modal="false">
      <el-form :model="form">
        <el-form-item label="标题">
          <el-input v-model="form.noticeTitle" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.noticeContent" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.noticeStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * NoticeMgmt - 后台通知公告管理
 *
 * 功能：
 * - 通知列表展示
 * - 新增/编辑通知（标题、内容、发布状态）
 * - 删除通知
 */

import { ref, onMounted } from 'vue'
import { getNoticePage, saveNotice, deleteNotice } from '@/api'
import { ElMessage } from 'element-plus'

const list = ref([])      // 通知列表
const visible = ref(false) // 编辑弹窗可见性
const form = ref({})       // 编辑表单数据

onMounted(loadData)

/** 加载通知列表 */
async function loadData() {
  const r = await getNoticePage({ page: 1, size: 50 })
  list.value = r?.data?.records || []
}

/**
 * 打开新增/编辑通知弹窗
 * @param {Object} row - 通知数据
 */
function openEdit(row) {
  form.value = { ...row }
  visible.value = true
}

/** 保存通知 */
async function save() {
  await saveNotice(form.value)
  visible.value = false
  ElMessage.success('保存成功')
  loadData()
}

/**
 * 删除通知
 * @param {number|string} id - 通知 ID
 */
async function del(id) {
  await deleteNotice(id)
  ElMessage.success('已删除')
  loadData()
}
</script>
