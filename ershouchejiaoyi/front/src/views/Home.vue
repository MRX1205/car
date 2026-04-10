<template>
  <div class="home-container">
    <div class="content-wrapper">
      <!-- 英雄视觉区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <div class="badge">世界500强</div>
          <h1 class="hero-title">
            <span class="gradient-text">二手车交易平台</span>
          </h1>
          <p class="hero-subtitle">
            海量精选真实高级座驾，全网比价，透明公道。<br />
            采用前沿引擎渲染，赋予您触手可及的看车感受，即刻启程！
          </p>
          <div class="hero-actions">
            <el-button
              type="primary"
              size="large"
              round
              class="action-btn primary-btn"
              @click="goToList"
            >
              浏览全系座驾
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
            <el-button
              size="large"
              round
              class="action-btn secondary-btn"
              @click="goToSell"
            >
              我要买车
            </el-button>
          </div>

          <div class="stats-row">
            <div class="stat-item">
              <span class="stat-number">10k+</span>
              <span class="stat-label">真实车源</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">300+</span>
              <span class="stat-label">认证商家</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">98%</span>
              <span class="stat-label">好评率</span>
            </div>
          </div>
        </div>

        <div class="hero-image-wrapper">
          <!-- 3D 氛围打光 -->
          <div class="glow-effect" :style="{ background: currentColor.glow }"></div>
          <div class="circle-bg"></div>

          <transition name="fade" mode="out-in">
            <img :key="currentColor.id" :src="currentColor.image" alt="Mercedes AMG 3D" class="amg-car" />
          </transition>

          <!-- 颜色选择器 -->
          <div class="color-picker-panel">
            <div class="color-text">自选心仪涂装</div>
            <div class="color-options">
              <div
                v-for="color in colors"
                :key="color.id"
                class="color-dot"
                :class="{ active: currentColor.id === color.id }"
                :style="{ background: color.hex }"
                @click="currentColor = color"
              ></div>
            </div>
          </div>
        </div>
      </section>

      <!-- 服务优势区域 -->
      <section class="features-section">
        <div
          class="feature-card"
          v-for="(feature, index) in features"
          :key="index"
          :style="{ animationDelay: `${index * 0.15}s` }"
        >
          <div class="feature-icon-wrapper">
            <el-icon class="feature-icon"><component :is="feature.icon" /></el-icon>
          </div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.desc }}</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef } from 'vue'
import { useRouter } from 'vue-router'

// 颜色资产引入
import SilverAmg from '../assets/amg_hero.png'
import BlueAmg from '../assets/amg_light_blue.png'
import GreenAmg from '../assets/amg_green.png'
import OrangeAmg from '../assets/amg_orange.png'

const router = useRouter()

const features = ref([
  { icon: 'Check', title: 'AMG 级品质保障', desc: '100%经过严格赛事级筛选，车况透明有保障，每一辆都如新出厂一般亮眼。' },
  { icon: 'Money', title: '全网智能比价', desc: '海量全网数据实时智能抓取与分析计算，强大算法给出最公道，最精准的购车底价。' },
  { icon: 'Service', title: '一对一管家服务', desc: '超金牌专业顾问团队，全程陪伴交易过程，一对一快速响应，解决您所有的后顾之忧。' }
])

const colors = [
  { id: 'silver', hex: '#cbd5e1', image: SilverAmg, glow: 'radial-gradient(circle, rgba(203,213,225,0.4) 0%, rgba(255,255,255,0) 70%)' },
  { id: 'blue', hex: '#3b82f6', image: BlueAmg, glow: 'radial-gradient(circle, rgba(59,130,246,0.4) 0%, rgba(255,255,255,0) 70%)' },
  { id: 'green', hex: '#10b981', image: GreenAmg, glow: 'radial-gradient(circle, rgba(16,185,129,0.4) 0%, rgba(255,255,255,0) 70%)' },
  { id: 'orange', hex: '#f97316', image: OrangeAmg, glow: 'radial-gradient(circle, rgba(249,115,22,0.4) 0%, rgba(255,255,255,0) 70%)' }
]

const currentColor = shallowRef(colors[0])

const goToList = () => {
  router.push('/cars')
}

const goToSell = () => {
  router.push('/cars')
}
</script>

<style scoped>
.home-container {
  min-height: calc(100vh - 70px);
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  background: linear-gradient(135deg, #f8fafc 0%, #e0f2fe 100%);
  padding: 40px 20px;
  overflow: hidden;
}

.content-wrapper {
  width: 95%;
  max-width: 1600px;
  margin: 0 auto;
}

/* --- Hero Section --- */
.hero-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 70vh;
  gap: 40px;
  position: relative;
  z-index: 10;
}

.hero-content {
  flex: 1;
  max-width: 650px;
  animation: slideRightFade 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.badge {
  display: inline-block;
  padding: 8px 16px;
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
  border-radius: 30px;
  font-weight: 600;
  font-size: 0.95rem;
  margin-bottom: 25px;
  border: 1px solid rgba(59, 130, 246, 0.2);
}

.hero-title {
  font-size: 4rem;
  line-height: 1.15;
  color: #0f172a;
  margin: 0 0 25px 0;
  font-weight: 800;
  letter-spacing: -2px;
}

.gradient-text {
  background: linear-gradient(135deg, #1d4ed8 0%, #3b82f6 50%, #60a5fa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-subtitle {
  font-size: 1.25rem;
  line-height: 1.8;
  color: #475569;
  margin-bottom: 45px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  margin-bottom: 50px;
}

.action-btn {
  padding: 15px 40px;
  font-size: 1.15rem;
  font-weight: 600;
  height: auto;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.primary-btn {
  background: #2563eb !important;
  border: 2px solid #2563eb !important;
  color: #ffffff !important;
  font-weight: 700 !important;
  font-size: 16px !important;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.primary-btn:hover {
  background: #1d4ed8 !important;
  border-color: #1d4ed8 !important;
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(37, 99, 235, 0.4);
}

.secondary-btn {
  color: #1e293b;
  border: 2px solid #cbd5e1;
  background-color: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
}

.secondary-btn:hover {
  background-color: #ffffff;
  border-color: #3b82f6;
  color: #3b82f6;
  transform: translateY(-4px);
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 2rem;
  font-weight: 800;
  color: #0f172a;
}

.stat-label {
  font-size: 0.95rem;
  color: #64748b;
  font-weight: 500;
}

.stat-divider {
  width: 2px;
  height: 40px;
  background: #cbd5e1;
}

/* 3D Model Image Area */
.hero-image-wrapper {
  flex: 1.2;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  animation: float 6s ease-in-out infinite;
}

.glow-effect {
  position: absolute;
  width: 600px;
  height: 600px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 0;
  transition: background 0.5s ease;
}

.circle-bg {
  position: absolute;
  width: 450px;
  height: 450px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255,255,255,0.8), rgba(255,255,255,0.2));
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  z-index: 1;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.05);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.amg-car {
  position: relative;
  z-index: 2;
  width: 130%;
  max-width: 900px;
  transform: scale(1.1) translateX(20px);
  filter: drop-shadow(0 30px 25px rgba(0, 0, 0, 0.15));
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: scale(1.1) translateX(20px) translateY(10px);
}

/* Color Picker */
.color-picker-panel {
  position: relative;
  z-index: 10;
  margin-top: 40px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  padding: 12px 24px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.color-text {
  font-weight: 600;
  color: #475569;
  font-size: 0.95rem;
}

.color-options {
  display: flex;
  gap: 12px;
}

.color-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid white;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.color-dot:hover {
  transform: scale(1.2);
}

.color-dot.active {
  transform: scale(1.3);
  box-shadow: 0 0 0 2px #ffffff, 0 0 0 4px #3b82f6;
}

/* --- Features Section --- */
.features-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;
  margin-top: 50px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(16px);
  border-radius: 24px;
  padding: 40px 30px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 15px 35px rgba(15, 23, 42, 0.04);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  opacity: 0;
  transform: translateY(30px);
  animation: slideUpFade 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.feature-card:hover {
  transform: translateY(-10px) scale(1.02);
  background: #ffffff;
  box-shadow: 0 25px 50px rgba(59, 130, 246, 0.1);
}

.feature-icon-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 20px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 25px;
  transform: rotate(-5deg);
  transition: transform 0.4s ease;
}

.feature-card:hover .feature-icon-wrapper {
  transform: rotate(5deg) scale(1.1);
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 100%);
}

.feature-icon {
  font-size: 32px;
  color: #3b82f6;
  transition: color 0.4s ease;
}

.feature-card:hover .feature-icon {
  color: #ffffff;
}

.feature-card h3 {
  font-size: 1.35rem;
  color: #1e293b;
  margin: 0 0 15px 0;
  font-weight: 700;
}

.feature-card p {
  color: #64748b;
  font-size: 1.05rem;
  line-height: 1.7;
  margin: 0;
}

/* Animations */
@keyframes slideRightFade {
  from { opacity: 0; transform: translateX(-40px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes slideUpFade {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
  100% { transform: translateY(0px); }
}

/* Responsive Variables */
@media (max-width: 1200px) {
  .hero-title { font-size: 3.2rem; }
  .amg-car { width: 100%; transform: translateX(0); left: 0;}
  .circle-bg { width: 350px; height: 350px; }
  .features-section { gap: 20px; }
}

@media (max-width: 992px) {
  .hero-section { flex-direction: column; text-align: center; }
  .hero-content { max-width: 100%; display: flex; flex-direction: column; align-items: center; }
  .hero-actions { justify-content: center; flex-wrap: wrap; }
  .stats-row { justify-content: center; }
  .amg-car { width: 90%; transform: scale(1); max-width: 600px; margin-top: 40px;}
  .features-section { grid-template-columns: repeat(2, 1fr); }
  .color-picker-panel { margin-top: 20px; }
}

@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; letter-spacing: -1px; }
  .action-btn { width: 100%; margin-bottom: 10px; }
  .features-section { grid-template-columns: 1fr; }
  .circle-bg { width: 280px; height: 280px; }
  .stats-row { flex-wrap: wrap; gap: 15px; }
}
</style>
