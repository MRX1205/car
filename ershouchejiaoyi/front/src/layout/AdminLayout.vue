<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <el-icon class="logo-icon" v-if="!sidebarCollapsed"><Platform /></el-icon>
        <span class="logo-text" v-if="!sidebarCollapsed">管理员后台</span>
      </div>

      <nav class="sidebar-menu">
        <div
          class="menu-item"
          v-for="item in menuItems"
          :key="item.path"
          :class="{ active: activeMenu === item.path }"
          @click="navigateTo(item.path)"
        >
          <el-icon class="menu-icon"><component :is="item.icon" /></el-icon>
          <span class="menu-text" v-if="!sidebarCollapsed">{{ item.label }}</span>
        </div>
      </nav>

      <div class="sidebar-footer">
        <el-button
          text
          class="collapse-btn"
          @click="sidebarCollapsed = !sidebarCollapsed"
        >
          <el-icon><component :is="sidebarCollapsed ? 'Expand' : 'Fold'" /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="admin-main">
      <!-- Top Bar -->
      <header class="top-bar">
        <div class="top-bar-left">
          <h2>{{ currentPageTitle }}</h2>
        </div>
        <div class="top-bar-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-icon class="user-avatar"><UserFilled /></el-icon>
              <span>{{ authStore.userInfo?.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="home">返回前台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- Content Area -->
      <main class="content-area">
        <router-view v-slot="{ Component, route }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Platform, Expand, Fold, UserFilled } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)

const menuItems = [
  { path: '/admin/merchants', label: '商家管理', icon: 'Shop' },
  { path: '/admin/brands', label: '品牌管理', icon: 'Menu' },
  { path: '/admin/news', label: '公告管理', icon: 'Bell' },
  { path: '/admin/forum', label: '论坛管理', icon: 'ChatDotRound' },
  { path: '/admin/orders', label: '订单管理', icon: 'ShoppingCart' }
]

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/admin') return '/admin/merchants'
  return '/' + path.split('/').slice(1, 3).join('/')
})

const currentPageTitle = computed(() => {
  const item = menuItems.find(m => m.path === activeMenu.value)
  return item ? item.label : '管理员后台'
})

const navigateTo = (path) => {
  router.push(path)
}

const handleCommand = (command) => {
  if (command === 'home') {
    router.push('/')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      authStore.clearAuth()
      ElMessage.success('已退出登录')
      router.push('/login')
    }).catch(() => {})
  }
}

onMounted(() => {
  // 检查权限
  if (!authStore.isAdmin) {
    ElMessage.error('无权访问管理员后台')
    router.push('/login')
  }
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

/* Sidebar */
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
  color: white;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  font-weight: 700;
  font-size: 1.1rem;
}

.logo-icon {
  font-size: 28px;
  color: #60a5fa;
}

.logo-text {
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  padding: 20px 0;
  overflow-y: auto;
}

.menu-item {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: rgba(255, 255, 255, 0.7);
  border-left: 3px solid transparent;
  margin: 4px 0;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.menu-item.active {
  background: rgba(96, 165, 250, 0.2);
  color: #60a5fa;
  border-left-color: #60a5fa;
  font-weight: 600;
}

.menu-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.menu-text {
  white-space: nowrap;
}

.sidebar-footer {
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-btn {
  width: 100%;
  color: rgba(255, 255, 255, 0.7);
}

.collapse-btn:hover {
  color: white;
}

/* Main Content */
.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* Top Bar */
.top-bar {
  height: 70px;
  background: #fff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.top-bar-left h2 {
  margin: 0;
  color: #1e293b;
  font-size: 1.5rem;
  font-weight: 700;
}

.top-bar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background 0.3s ease;
  color: #334155;
  font-weight: 500;
}

.user-info:hover {
  background: #f1f5f9;
}

.user-avatar {
  font-size: 24px;
  color: #60a5fa;
}

/* Content Area */
.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
}

/* Transition */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* Scrollbar */
.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}

.sidebar-menu::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.05);
}

.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.sidebar-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
  .sidebar {
    width: 80px;
  }

  .sidebar-header {
    justify-content: center;
  }

  .logo-text,
  .menu-text {
    display: none;
  }

  .menu-item {
    justify-content: center;
    padding: 12px;
  }

  .top-bar {
    padding: 0 15px;
  }

  .top-bar-left h2 {
    font-size: 1.2rem;
  }

  .content-area {
    padding: 15px;
  }
}
</style>
