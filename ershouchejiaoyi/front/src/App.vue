<template>
  <div id="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade-slide" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
// App.vue root component
</script>

<style>
/* 全局样式和用户要求的主题设计 */

html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 浅蓝白色渐变背景 */
#app {
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #e0f2fe 0%, #ffffff 100%);
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
}

#app-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

/* --- 圆润与简洁设计 --- */

/* 明显的按钮，带圆角和悬浮动画 */
.el-button {
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.el-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 主要按钮采用蓝色边框白色背景 */
.el-button--primary {
  background: #ffffff !important;
  color: #3b82f6 !important;
  border: 2px solid #3b82f6 !important;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.15) !important;
}

.el-button--primary:hover {
  background: #eff6ff !important;
  color: #2563eb !important;
  border-color: #2563eb !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25) !important;
}

/* 危险/警告/成功按钮的亮色渐变 */
.el-button--danger {
  background: linear-gradient(135deg, #dc2626, #ef4444);
  color: #ffffff;
  border: none;
}
.el-button--success {
  background: linear-gradient(135deg, #16a34a, #22c55e);
  color: #ffffff;
  border: none;
}
.el-button--warning {
  background: linear-gradient(135deg, #d97706, #f59e0b);
  color: #ffffff;
  border: none;
}

/* 卡片圆润和毛玻璃效果 */
.el-card {
  border-radius: 16px;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(149, 157, 165, 0.15);
  transition: all 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 12px 48px rgba(149, 157, 165, 0.25);
}

/* 输入框圆润化 */
.el-input__wrapper, .el-textarea__inner {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

/* --- 链接样式 --- */

/* 确保所有链接可点击 */
a, .router-link-active, .router-link-exact-active {
  cursor: pointer !important;
  pointer-events: auto !important;
  position: relative;
  z-index: 1;
}

a:hover {
  text-decoration: underline;
}

/* --- 动画效果 --- */

/* 路由切换过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}
</style>
