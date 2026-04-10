<template>
  <div class="forum-container page-container">
    <!-- 发帖按钮 -->
    <div class="forum-header">
      <el-button type="primary" round size="large" @click="showPostDialog = true">
        <el-icon><Edit /></el-icon>发表新帖
      </el-button>
    </div>

    <!-- 论坛帖子列表 -->
    <div class="forum-list" v-loading="loading">
      <div
        class="forum-post-card"
        v-for="(item, index) in postList"
        :key="item.id"
        :style="{ animationDelay: `${index * 0.05}s` }"
        @click="goToDetail(item.id)"
      >
        <div class="post-header">
          <div class="author-info">
            <img :src="getAuthorAvatar(item)" :alt="getAuthorName(item)" class="author-avatar" />
            <div class="author-details">
              <div class="author-name">
                {{ getAuthorName(item) }}
                <el-tag v-if="getAuthorRole(item)" :type="getRoleTagType(getAuthorRole(item))" size="small" effect="light" class="role-tag">
                  {{ getAuthorRole(item) }}
                </el-tag>
              </div>
              <div class="post-time">{{ item.insertTime }}</div>
            </div>
          </div>
          <div class="post-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ item.forumClicknum || 0 }}
            </span>
            <span class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              {{ item.replyCount || 0 }}
            </span>
          </div>
        </div>

        <div class="post-content">
          <h3 class="post-title">{{ item.forumName }}</h3>
          <p class="post-desc">{{ stripHtml(item.forumContent) }}</p>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && postList.length === 0" description="暂无帖子，快来发表第一个帖子吧~">
        <el-button type="primary" @click="showPostDialog = true">发表帖子</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        background
        layout="prev, pager, next, jumper"
        :total="total"
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.limit"
        @current-change="fetchPostList"
      />
    </div>

    <!-- 发帖对话框 -->
    <el-dialog v-model="showPostDialog" title="发表新帖" width="600px" @close="resetPostForm">
      <el-form :model="postForm" label-width="80px" class="post-form">
        <el-form-item label="帖子标题" prop="forumName">
          <el-input v-model="postForm.forumName" placeholder="请输入帖子标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="帖子内容" prop="forumContent">
          <el-input v-model="postForm.forumContent" placeholder="请输入帖子内容" type="textarea" rows="6" maxlength="2000" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showPostDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPost" :loading="postLoading">发表</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { Edit, View, ChatDotRound } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const postLoading = ref(false)
const postList = ref([])
const total = ref(0)
const showPostDialog = ref(false)
const forumTypesList = ref([])

const pageParams = reactive({
  page: 1,
  limit: 10
})

const postForm = reactive({
  forumName: '',
  forumContent: ''
})

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const baseUrl = '/ershouchejiaoyi/upload/'

const getAvatarUrl = (photo) => {
  if (!photo) return defaultAvatar
  if (photo.startsWith('http') || photo.startsWith('/')) return photo.split(',')[0]
  return baseUrl + photo.split(',')[0]
}

const getAuthorAvatar = (item) => {
  if (item.shangjiaPhoto) return getAvatarUrl(item.shangjiaPhoto)
  if (item.yonghuPhoto) return getAvatarUrl(item.yonghuPhoto)
  return defaultAvatar
}

const getAuthorName = (item) => {
  if (item.shangjiaName) return item.shangjiaName
  if (item.yonghuName) return item.yonghuName
  if (item.uusername) return item.uusername
  return '匿名用户'
}

const getAuthorRole = (item) => {
  if (item.shangjiaId) return '商家'
  if (item.yonghuId) return '用户'
  if (item.usersId) return '管理员'
  return ''
}

const getRoleTagType = (role) => {
  const typeMap = {
    '用户': '',
    '商家': 'success',
    '管理员': 'danger'
  }
  return typeMap[role] || ''
}

const stripHtml = (html) => {
  if (!html) return '暂无内容...'
  let tmp = document.createElement('div')
  tmp.innerHTML = html
  let text = tmp.textContent || tmp.innerText || ''
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 获取论坛分类
const fetchForumTypes = async () => {
  try {
    const res = await request.get('/dictionary/page', {
      params: {
        page: 1,
        limit: 100,
        dicCode: 'forum_types'
      }
    })
    if (res.code === 0) {
      forumTypesList.value = res.data.list || []
    }
  } catch (error) {
    console.error('Fetch forum types error:', error)
  }
}

// 获取帖子列表
const fetchPostList = async () => {
  loading.value = true
  try {
    const res = await request.get('/forum/page', {
      params: {
        page: pageParams.page,
        limit: pageParams.limit
      }
    })

    if (res.code === 0) {
      postList.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取帖子列表失败')
    }
  } catch (error) {
    console.error('Fetch post list error:', error)
  } finally {
    loading.value = false
  }
}

// 提交新帖
const submitPost = async () => {
  if (!postForm.forumName || !postForm.forumContent) {
    ElMessage.warning('请填写帖子标题和内容')
    return
  }

  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录后再发帖')
    router.push('/login')
    return
  }

  postLoading.value = true
  try {
    const data = {
      forumName: postForm.forumName,
      forumContent: postForm.forumContent,
      forumStateTypes: 1
    }

    const res = await request.post('/forum/save', data)
    if (res.code === 0) {
      ElMessage.success('发帖成功')
      showPostDialog.value = false
      resetPostForm()
      pageParams.page = 1
      fetchPostList()
    } else {
      ElMessage.error(res.msg || '发帖失败')
    }
  } catch (error) {
    console.error('Submit post error:', error)
    ElMessage.error('发帖失败')
  } finally {
    postLoading.value = false
  }
}

const resetPostForm = () => {
  postForm.forumName = ''
  postForm.forumContent = ''
}

const goToDetail = (id) => {
  router.push(`/forum/detail/${id}`)
}

onMounted(() => {
  fetchForumTypes()
  fetchPostList()
})
</script>

<style scoped>
.page-container {
  max-width: 1400px;
  width: 92%;
  margin: 0 auto;
  padding: 30px 20px;
  min-height: calc(100vh - 70px - 100px);
}

.forum-header {
  margin-bottom: 30px;
  display: flex;
  justify-content: flex-end;
}

.forum-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.forum-post-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  animation: slideUpFadeIn 0.5s ease forwards;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);
}

.forum-post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(59, 130, 246, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f1f5f9;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-tag {
  font-size: 0.75rem;
}

.post-time {
  color: #94a3b8;
  font-size: 0.85rem;
}

.post-stats {
  display: flex;
  gap: 15px;
  color: #64748b;
  font-size: 0.9rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-content {
  margin-bottom: 15px;
}

.post-title {
  margin: 0 0 10px 0;
  font-size: 1.2rem;
  color: #1e293b;
  font-weight: 600;
  line-height: 1.4;
}

.post-desc {
  margin: 0;
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.6;
}

.post-footer {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.post-form {
  padding: 20px 0;
}

@keyframes slideUpFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .post-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .post-stats {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
