<template>
  <div class="merchant-layout">
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-header">
        <el-icon class="logo-icon" v-if="!sidebarCollapsed"><Shop /></el-icon>
        <span class="logo-text" v-if="!sidebarCollapsed">商家后台</span>
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
    <div class="merchant-main">
      <!-- Top Bar -->
      <header class="top-bar">
        <div class="top-bar-left">
          <h2>{{ currentPageTitle }}</h2>
        </div>
        <div class="top-bar-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <img :src="getMerchantAvatar()" :alt="authStore.userInfo?.shangjiaName || '商家'" class="merchant-avatar" />
              <span>{{ authStore.userInfo?.shangjiaName || '商家用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">商家资料</el-dropdown-item>
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
import { ArrowDown, Shop, Expand, Fold } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)
const merchantInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const baseUrl = '/ershouchejiaoyi/upload/'

const getMerchantAvatar = () => {
  const photo = authStore.userInfo?.shangjiaPhoto
  if (!photo || typeof photo !== 'string') return defaultAvatar
  if (photo.startsWith('http') || photo.startsWith('/')) return photo.split(',')[0]
  return baseUrl + photo.split(',')[0]
}

const menuItems = [
  { path: '/merchant/profile', label: '商家资料', icon: 'User' },
  { path: '/merchant/vehicles', label: '车辆管理', icon: 'Van' },
  { path: '/merchant/messages', label: '汽车留言', icon: 'ChatLineSquare' },
  { path: '/merchant/orders', label: '订单管理', icon: 'ShoppingCart' },
  { path: '/merchant/forum', label: '交流论坛', icon: 'ChatDotRound' }
]

const activeMenu = computed(() => {
  const path = route.path
  if (path === '/merchant') return '/merchant/profile'
  return '/' + path.split('/').slice(1, 3).join('/')
})

const currentPageTitle = computed(() => {
  const item = menuItems.find(m => m.path === activeMenu.value)
  return item ? item.label : '商家资料'
})

const navigateTo = (path) => {
  router.push(path)
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/merchant/profile')
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
  if (!authStore.isMerchant) {
    ElMessage.error('无权访问商家后台')
    router.push('/login')
  }
})
</script>

<style scoped>
.merchant-layout {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

/* Sidebar */
.sidebar {
  width: 260px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  color: #1e293b;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
}

.sidebar.collapsed {
  width: 80px;
}

.sidebar-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid #f1f5f9;
  font-weight: 700;
  font-size: 1.1rem;
}

.logo-icon {
  font-size: 28px;
  color: #3b82f6;
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
  color: #64748b;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background: #f8fafc;
  color: #3b82f6;
}

.menu-item.active {
  background: #eff6ff;
  color: #3b82f6;
  border-left-color: #3b82f6;
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
  border-top: 1px solid #f1f5f9;
}

.collapse-btn {
  width: 100%;
  color: #64748b;
}

.collapse-btn:hover {
  color: #3b82f6;
}

/* Main Content */
.merchant-main {
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

.merchant-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

/* Content Area */
.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 30px;
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
