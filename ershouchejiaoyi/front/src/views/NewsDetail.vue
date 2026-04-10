<template>
  <div class="news-detail-container page-container">
    <!-- 顶部操作栏 -->
    <div class="action-bar">
      <el-button class="back-btn" round @click="router.back()">
        <el-icon class="el-icon--left"><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <!-- 内容卡片 -->
    <el-card class="detail-card" shadow="never" v-loading="loading">
      <div class="article-header">
        <h1 class="title">{{ newsInfo.newsName }}</h1>
        <div class="meta">
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>发布时间：{{ newsInfo.insertTime || '近期' }}</span>
          </div>
          <div class="meta-item tag">
            系统公告
          </div>
        </div>
      </div>

      <!-- 封面图 -->
      <div class="cover-image" v-if="newsInfo.newsPhoto">
        <img :src="getImageUrl(newsInfo.newsPhoto)" :alt="newsInfo.newsName" />
      </div>

      <!-- 正文内容 -->
      <div class="article-content" v-html="newsInfo.newsContent || '<p>暂无详细内容</p>'"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const newsInfo = ref({})

const baseUrl = '/ershouchejiaoyi/upload/'

// 处理图片URL
const getImageUrl = (photo) => {
  if (!photo) return ''
  if (photo.startsWith('http') || photo.startsWith('/')) return photo.split(',')[0]
  return baseUrl + photo.split(',')[0]
}

// 获取详情数据
const fetchDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('无效的公告ID')
    router.push('/news')
    return
  }

  loading.value = true
  try {
    const res = await request.get(`/news/info/${id}`)
    if (res.code === 0) {
      newsInfo.value = res.data
    } else {
      ElMessage.error(res.msg || '获取公告详情失败')
    }
  } catch (error) {
    console.error('fetch news detail error:', error)
    ElMessage.error('获取公告详情失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  width: 90%; /* 阅读类页面稍微收窄 */
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 70px - 100px);
}

.action-bar {
  margin-bottom: 20px;
}

.back-btn {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  color: #475569;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #ffffff;
  color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(59, 130, 246, 0.15);
}

.detail-card {
  border-radius: 24px !important;
  background: rgba(255, 255, 255, 0.85) !important;
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.04) !important;
  padding: 20px;
  animation: slideUpFade 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.article-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 25px;
  border-bottom: 1px dashed #e2e8f0;
}

.title {
  font-size: 2.2rem;
  color: #0f172a;
  margin: 0 0 20px 0;
  font-weight: 700;
  line-height: 1.4;
}

.meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  color: #64748b;
  font-size: 0.95rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag {
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.cover-image {
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 30px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  background: #f8fafc;
}

.cover-image img {
  width: 100%;
  height: auto;
  max-height: 500px;
  object-fit: cover;
  object-position: center;
  display: block;
}

.article-content {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #334155;
  padding: 0 10px;
}

/* 深度作用于 v-html 渲染出来的内容 */
.article-content :deep(p) {
  margin-bottom: 1.5em;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 12px;
  margin: 20px auto;
  display: block;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  color: #1e293b;
  margin-top: 1.5em;
  margin-bottom: 0.8em;
  font-weight: 600;
}

@keyframes slideUpFade {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .title {
    font-size: 1.5rem;
  }

  .detail-card {
    padding: 10px;
  }

  .article-content {
    font-size: 1rem;
  }
}
</style>
