<template>
  <div class="news-container page-container">
    <!-- 搜索筛选区 -->
    <el-card class="search-card" shadow="hover">
      <div class="header-action-bar">
        <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
          <el-form-item>
            <el-input
              v-model="searchForm.newsName"
              placeholder="请输入公告标题进行搜索"
              clearable
              prefix-icon="Search"
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" round class="action-btn" @click="handleSearch">查 询</el-button>
            <el-button round class="reset-btn" @click="resetSearch">重 置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 公告列表区 -->
    <div class="news-list" v-loading="loading">
      <div
        class="news-card"
        v-for="(item, index) in newsList"
        :key="item.id"
        :style="{ animationDelay: `${index * 0.08}s` }"
        @click="goToDetail(item.id)"
      >
        <div class="img-wrapper">
          <img :src="getImageUrl(item.newsPhoto)" :alt="item.newsName" class="news-img" @error="handleImgError" />
          <div class="tag">系统公告</div>
        </div>

        <div class="news-content">
          <h3 class="news-title" :title="item.newsName">{{ item.newsName }}</h3>
          <p class="news-desc" v-html="stripHtml(item.newsContent)"></p>

          <div class="news-meta">
            <div class="meta-left">
              <el-icon><Calendar /></el-icon>
              <span>发布时间：{{ item.insertTime || '近期' }}</span>
            </div>
            <el-button type="primary" link class="read-more">
              阅读全文 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-wrapper" v-if="!loading && newsList.length === 0">
      <el-empty description="暂无公告信息" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next, jumper"
        :total="total"
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.limit"
        @current-change="fetchNewsList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()

// 基础配置
const baseUrl = '/ershouchejiaoyi/upload/'
const defaultImage = 'https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png'

const loading = ref(false)
const newsList = ref([])
const total = ref(0)

const searchForm = reactive({
  newsName: ''
})

const pageParams = reactive({
  page: 1,
  limit: 8 // 每页显示8条
})

// 处理图片URL
const getImageUrl = (photo) => {
  if (!photo) return defaultImage
  if (photo.startsWith('http') || photo.startsWith('/')) return photo.split(',')[0]
  const firstPhoto = photo.split(',')[0]
  return baseUrl + firstPhoto
}

const handleImgError = (e) => {
  e.target.src = defaultImage
}

// 提取纯文本简介
const stripHtml = (html) => {
  if (!html) return '暂无内容简介...'
  let tmp = document.createElement('div')
  tmp.innerHTML = html
  let text = tmp.textContent || tmp.innerText || ''
  return text.length > 120 ? text.substring(0, 120) + '...' : text
}

// 获取公告列表数据
const fetchNewsList = async () => {
  loading.value = true
  try {
    const res = await request.get('/news/page', {
      params: {
        page: pageParams.page,
        limit: pageParams.limit,
        newsName: searchForm.newsName ? searchForm.newsName : undefined
      }
    })

    if (res.code === 0) {
      newsList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取公告数据失败')
    }
  } catch (error) {
    console.error('获取公告列表出错:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageParams.page = 1
  fetchNewsList()
}

const resetSearch = () => {
  searchForm.newsName = ''
  pageParams.page = 1
  fetchNewsList()
}

const goToDetail = (id) => {
  router.push(`/news/detail/${id}`)
}

onMounted(() => {
  fetchNewsList()
})
</script>

<style scoped>
.page-container {
  max-width: 1400px;
  width: 92%; /* 资讯类页面稍微收窄，阅读体验更好 */
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 70px - 100px);
}

/* 搜索卡片 - 圆润与毛玻璃效果 */
.search-card {
  border-radius: 20px !important;
  margin-bottom: 30px;
  border: none !important;
  background: rgba(255, 255, 255, 0.7) !important;
  backdrop-filter: blur(10px);
}

.header-action-bar {
  display: flex;
  align-items: center;
}

.search-form {
  display: flex;
  align-items: center;
  margin-bottom: 0;
  width: 100%;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

/* 明显的按钮设计 */
.action-btn {
  background: linear-gradient(135deg, #3b82f6, #60a5fa) !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3) !important;
  padding: 0 24px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4) !important;
}

.reset-btn {
  color: #64748b;
  border-color: #cbd5e1;
}

.reset-btn:hover {
  color: #3b82f6;
  border-color: #93c5fd;
  background-color: #eff6ff;
}

/* 公告列表布局 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
  margin-bottom: 40px;
}

/* 公告卡片设计 */
.news-card {
  display: flex;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(14, 165, 233, 0.05);
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.5);
  opacity: 0;
  transform: translateY(40px);
  animation: slideUpFadeIn 0.6s forwards;
  padding: 20px;
  gap: 25px;
}

.news-card:hover {
  transform: translateY(-8px) scale(1.01);
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.12);
  background: #ffffff;
}

/* 左侧图片区域 */
.img-wrapper {
  position: relative;
  width: 240px;
  min-width: 240px;
  height: 160px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.news-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.news-card:hover .news-img {
  transform: scale(1.1);
}

/* 标签 */
.tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

/* 右侧内容区域 */
.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.news-title {
  margin: 0 0 12px 0;
  font-size: 1.35rem;
  color: #1e293b;
  font-weight: 700;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  transition: color 0.3s;
}

.news-card:hover .news-title {
  color: #3b82f6;
}

.news-desc {
  margin: 0 0 15px 0;
  color: #64748b;
  font-size: 1rem;
  line-height: 1.6;
  flex: 1;
}

/* 底部元信息 */
.news-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px dashed #e2e8f0;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #94a3b8;
  font-size: 0.9rem;
}

.read-more {
  font-weight: 600;
  font-size: 0.95rem;
}

/* 分页组件样式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

:deep(.el-pagination.is-background .el-pager li) {
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #3b82f6;
}

:deep(.el-pagination.is-background .btn-next),
:deep(.el-pagination.is-background .btn-prev) {
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.8);
}

/* 动画 */
@keyframes slideUpFadeIn {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .news-card {
    flex-direction: column;
    padding: 15px;
    gap: 15px;
  }

  .img-wrapper {
    width: 100%;
    min-width: 100%;
    height: 180px;
  }

  .news-title {
    font-size: 1.2rem;
  }

  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style>
