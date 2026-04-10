<template>
  <div class="layout-container">
    <!-- Header Navigation -->
    <header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/')">
          <el-icon class="logo-icon"><Van /></el-icon>
          <span class="title">二手车交易平台</span>
        </div>

        <!-- Desktop Navigation Menu -->
        <nav class="nav-menu">
          <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
            router
            background-color="transparent"
            text-color="#606266"
            active-text-color="#409EFF"
            :ellipsis="false"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/cars">二手车辆</el-menu-item>
            <el-menu-item index="/forum">交流论坛</el-menu-item>
            <el-menu-item index="/news">公告信息</el-menu-item>
          </el-menu>
        </nav>

        <!-- Mobile Menu Toggle Button -->
        <el-button class="mobile-menu-btn" text @click="mobileMenuOpen = !mobileMenuOpen">
          <el-icon><component :is="mobileMenuOpen ? 'Close' : 'Menu'" /></el-icon>
        </el-button>

        <div class="user-action">
          <template v-if="authStore.isLoggedIn">
            <!-- 商家按钮 -->
            <el-button v-if="authStore.isMerchant" type="primary" text @click="$router.push('/merchant')">
              商家后台
            </el-button>

            <el-dropdown trigger="click" @command="handleCommand">
              <span class="el-dropdown-link user-dropdown">
                <el-avatar :size="32" :src="getUserAvatar()" />
                <span class="username">{{ authStore.userInfo.yonghuName || authStore.userInfo.shangjiaName || authStore.userInfo.username || '个人中心' }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="center">个人中心</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isUser" command="collections">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isUser" command="orders">交易订单</el-dropdown-item>
                  <el-dropdown-item v-if="authStore.isUser" command="cart">购物车</el-dropdown-item>
                  <el-dropdown-item divided command="logout" class="logout-item">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button round class="login-btn" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" round class="register-btn" @click="$router.push('/register')">免费注册</el-button>
          </template>
        </div>
      </div>

      <!-- Mobile Navigation Menu -->
      <div class="mobile-menu" v-if="mobileMenuOpen">
        <div class="mobile-menu-item" @click="navigateTo('/'); mobileMenuOpen = false">首页</div>
        <div class="mobile-menu-item" @click="navigateTo('/cars'); mobileMenuOpen = false">二手车辆</div>
        <div class="mobile-menu-item" @click="navigateTo('/forum'); mobileMenuOpen = false">交流论坛</div>
        <div class="mobile-menu-item" @click="navigateTo('/news'); mobileMenuOpen = false">公告信息</div>
      </div>
    </header>

    <!-- Main Content Area -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" :key="$route.path" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 二手车交易系统 版权所有</p>
        <p class="sub-text">提供优质、透明、安全的二手车交易服务</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Menu, Close } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const getUserAvatar = () => {
  const photo = authStore.userInfo?.yonghuPhoto || authStore.userInfo?.shangjiaPhoto
  if (!photo) return defaultAvatar
  if (photo.startsWith('http') || photo.startsWith('/')) return photo.split(',')[0]
  return `/ershouchejiaoyi/upload/${photo.split(',')[0]}`
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Calculate active menu item based on current route
const activeIndex = computed(() => {
  if (route.path.startsWith('/cars')) return '/cars'
  if (route.path.startsWith('/forum')) return '/forum'
  if (route.path.startsWith('/news')) return '/news'
  return route.path
})

const mobileMenuOpen = ref(false)

// Handle menu selection
const handleSelect = (key) => {
  // router handles navigation automatically due to 'router' prop on el-menu
}

// Navigate to path
const navigateTo = (path) => {
  router.push(path)
}

// Handle user dropdown actions
const handleCommand = (command) => {
  switch (command) {
    case 'center':
      router.push('/user/center')
      break
    case 'collections':
      router.push('/user/collections')
      break
    case 'orders':
      router.push('/user/orders')
      break
    case 'cart':
      router.push('/user/cart')
      break
    case 'logout':
      ElMessageBox.confirm(
        '确定要退出登录吗?',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        authStore.clearAuth()
        ElMessage.success('已退出登录')
        if (route.path.startsWith('/user')) {
          router.push('/')
        }
      }).catch(() => {})
      break
  }
}

// Update token status if changed in other tabs/windows
onMounted(() => {
  window.addEventListener('storage', (e) => {
    if (e.key === 'Token') {
      if (e.newValue) {
        authStore.setAuth(e.newValue, localStorage.getItem('userTable'), JSON.parse(localStorage.getItem('userInfo') || '{}'))
      } else {
        authStore.clearAuth()
      }
    }
  })
})
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* Header Styles */
.header {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.header-content {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
  flex-shrink: 0;
}

.logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 32px;
  color: #3b82f6;
}

.logo .title {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  white-space: nowrap;
}

/* Nav Menu Styles */
.nav-menu {
  flex: 1;
  display: flex;
  justify-content: center;
}

:deep(.el-menu) {
  border-bottom: none !important;
  background: transparent !important;
}

:deep(.el-menu-item) {
  font-size: 1.05rem;
  font-weight: 500;
  padding: 0 24px;
  border-bottom: 3px solid transparent !important;
  transition: all 0.3s ease;
}

:deep(.el-menu-item:hover) {
  background-color: transparent !important;
  color: #3b82f6 !important;
}

:deep(.el-menu-item.is-active) {
  background-color: transparent !important;
  color: #3b82f6 !important;
  border-bottom-color: #3b82f6 !important;
  font-weight: 600;
}

/* Mobile Menu Button */
.mobile-menu-btn {
  display: none;
  font-size: 24px;
  color: #1e293b;
  margin-right: 10px;
}

/* Mobile Menu */
.mobile-menu {
  display: none;
  background: rgba(255, 255, 255, 0.95);
  border-top: 1px solid #f1f5f9;
  padding: 10px 0;
}

.mobile-menu-item {
  padding: 12px 20px;
  color: #334155;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.mobile-menu-item:hover {
  background: #f8fafc;
  color: #3b82f6;
  border-left-color: #3b82f6;
}

/* User Action Styles */
.user-action {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.login-btn {
  border: 2px solid #e2e8f0;
  color: #64748b;
}

.login-btn:hover {
  border-color: #cbd5e1;
  color: #475569;
}

.register-btn {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  border: none;
  padding: 0 24px;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 24px;
  transition: background 0.3s ease;
}

.user-dropdown:hover {
  background: #f1f5f9;
}

.username {
  font-weight: 500;
  color: #334155;
}

.logout-item {
  color: #ef4444;
}

/* Main Content Styles */
.main-content {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
}

/* Footer Styles */
.footer {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  padding: 30px 20px;
  text-align: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  margin-top: 40px;
}

.footer-content {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
}

.footer p {
  color: #64748b;
  margin: 5px 0;
  font-weight: 500;
}

.footer .sub-text {
  font-size: 0.9rem;
  color: #94a3b8;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .nav-menu {
    display: none;
  }

  .mobile-menu-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .mobile-menu {
    display: block;
  }

  .user-action {
    gap: 8px;
  }

  .register-btn {
    padding: 0 16px;
  }
}

@media (max-width: 640px) {
  .header-content {
    height: auto;
    padding: 12px 15px;
    flex-wrap: wrap;
  }

  .logo .title {
    font-size: 1rem;
  }

  .user-action {
    width: 100%;
    justify-content: flex-end;
    margin-top: 10px;
  }

  .login-btn,
  .register-btn {
    padding: 0 12px;
    font-size: 0.9rem;
  }

  .username {
    display: none;
  }

  .user-dropdown {
    padding: 4px 8px;
  }

  .footer {
    padding: 20px 15px;
  }

  .footer p {
    font-size: 0.9rem;
  }
}
</style>
