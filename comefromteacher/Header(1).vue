<template>
    <!-- 外层容器控制左右分布 -->
    <div class="header-menu-wrap">
        <el-menu
                :default-active="activeIndex2"
                class="el-menu-demo"
                mode="horizontal"
                background-color="#2c6bff"
                text-color="#fff"
                active-text-color="#ffd04b"
                @select="handleSelect"
        >
            <el-menu-item index="1">首页</el-menu-item>
            <el-menu-item index="2">促销</el-menu-item>
            <el-menu-item index="3">秒杀</el-menu-item>
            <el-menu-item index="4">团购</el-menu-item>
        </el-menu>

        <!-- 右侧登录、购物车区域 -->
        <div class="menu-right">
            <el-button text type="text" icon="ShoppingCart" @click="goCart">购物车</el-button>
            <!-- 美化用户名+退出拆分 -->
            <span v-if="user" class="username-text">
                欢迎 {{user.userName}}
                <span class="split-line">|</span>
                <span class="logout-btn" @click="logout">退出</span>
            </span>
            <span v-else>
                <el-button text type="text" icon="User" @click="goLogin">登录</el-button>
            </span>
        </div>
    </div>
</template>

<script setup>
    import { ref } from 'vue'
    import {useRouter} from 'vue-router'
    import { ElMessage } from 'element-plus'

    //取出sessionStorage中存入的用户对象
    const user = ref(JSON.parse(sessionStorage.getItem("user")))

    const router = useRouter()

    // 菜单激活项
    const activeIndex2 = ref('1')
    // 菜单选中回调
    const handleSelect = (key) => {
        console.log('选中菜单：', key)
    }
    // 登录跳转
    const goLogin = () => {
        router.push('/login')
    }
    // 购物车跳转
    const goCart = () => {
        console.log('打开购物车')
    }
    // 退出登录逻辑
    const logout = () => {
        sessionStorage.removeItem('user')
        // 先替换路由，再刷新页面
        router.replace('/')
        location.reload()
    }
</script>

<style scoped>
    .header-menu-wrap {
        display: flex;
        align-items: center;
        background-color: #2c6bff;
    }
    /* 左侧菜单占满剩余宽度，自动挤右侧按钮 */
    .el-menu-demo {
        flex: 1;
        border-bottom: none;
    }
    .menu-right {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 0 20px;
    }
    /* 按钮文字白色 */
    .menu-right :deep(.el-button--text) {
        color: #fff;
        font-size: 15px;
    }
    /* hover高亮黄色和菜单选中色统一 */
    .menu-right :deep(.el-button--text:hover) {
        color: #ffd04b;
    }

    /* 美化用户名样式 */
    .username-text {
        color: #ffffff;
        font-size: 15px;
        padding: 4px 8px;
        border-radius: 4px;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        transition: color 0.2s;
    }
    .username-text:hover {
        background-color: rgba(255, 255, 255, 0.1);
    }
    /* 分隔线样式 */
    .split-line {
        color: rgba(255,255,255,0.4);
    }
    /* 退出按钮美化 */
    .logout-btn {
        cursor: pointer;
        transition: color 0.2s;
    }
    .logout-btn:hover {
        color: #ffd04b;
    }
</style>