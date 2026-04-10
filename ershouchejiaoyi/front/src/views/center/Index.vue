<template>
  <div class="user-center-container page-container">
    <div class="center-layout">
      <!-- 左侧导航侧边栏 -->
      <div class="center-sidebar">
        <el-card class="sidebar-card" shadow="never">
          <div class="user-profile-mini">
            <el-avatar :size="64" :src="userAvatar" />
            <div class="username">{{ authStore.userInfo?.yonghuName || authStore.userInfo?.shangjiaName || authStore.userInfo?.username || '个人中心' }}</div>
            <div class="role-tag">{{ roleName }}</div>
          </div>

          <el-menu
            :default-active="activeIndex"
            class="center-menu"
            router
            text-color="#64748b"
            active-text-color="#3b82f6"
          >
            <el-menu-item index="/user/center">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-menu-item>
            <el-menu-item index="/user/orders">
              <el-icon><List /></el-icon>
              <span>交易订单</span>
            </el-menu-item>
            <el-menu-item index="/user/cart" v-if="userRole === 'yonghu'">
              <el-icon><ShoppingCart /></el-icon>
              <span>购物车</span>
            </el-menu-item>
            <el-menu-item index="/user/collections" v-if="userRole === 'yonghu'">
              <el-icon><Star /></el-icon>
              <span>我的收藏</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </div>

      <!-- 右侧内容区 -->
      <div class="center-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const authStore = useAuthStore()
const activeIndex = computed(() => route.path)

const userRole = ref(localStorage.getItem('userTable') || 'yonghu')

const roleName = computed(() => {
  return userRole.value === 'shangjia' ? '商家' : '普通用户'
})

const userAvatar = computed(() => {
  const photo = userRole.value === 'yonghu' ? authStore.userInfo?.yonghuPhoto : authStore.userInfo?.shangjiaPhoto
  if (!photo || typeof photo !== 'string') {
    return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  }
  const cleanPhoto = photo.split(',')[0].trim()
  if (cleanPhoto.startsWith('/') || cleanPhoto.startsWith('http')) {
    return cleanPhoto
  }
  return '/ershouchejiaoyi/upload/' + cleanPhoto
})
</script>

<style scoped>
.page-container {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 70px - 100px);
}

.center-layout {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

/* 侧边栏样式 */
.center-sidebar {
  width: 260px;
  flex-shrink: 0;
}

.sidebar-card {
  border-radius: 24px !important;
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  overflow: hidden;
  padding: 0;
}

:deep(.el-card__body) {
  padding: 0;
}

.user-profile-mini {
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), transparent);
}

.username {
  margin-top: 15px;
  font-size: 1.15rem;
  font-weight: 600;
  color: #1e293b;
}

.role-tag {
  margin-top: 8px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  background: #eff6ff;
  color: #3b82f6;
  font-weight: 500;
}

.center-menu {
  border-right: none;
  padding: 15px 0;
  background: transparent;
}

.center-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 8px 15px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.center-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), transparent);
  color: #3b82f6;
  font-weight: 600;
}

.center-menu :deep(.el-menu-item:hover) {
  background: #f8fafc;
}

/* 右侧内容区样式 */
.center-content {
  flex: 1;
  min-width: 0; /* 防止子元素撑破父容器 */
}

/* 路由动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 响应式 */
@media (max-width: 768px) {
  .center-layout {
    flex-direction: column;
  }
  .center-sidebar {
    width: 100%;
  }
}
</style>
