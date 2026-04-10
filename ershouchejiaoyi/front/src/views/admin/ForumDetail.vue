<template>
  <div class="forum-detail-container">
    <div class="action-bar">
      <el-button class="back-btn" round @click="router.back()">
        <el-icon class="el-icon--left"><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <el-card class="detail-card" shadow="never" v-loading="loading">
      <div class="post-header">
        <div class="author-section">
          <img :src="getAuthorAvatar(postDetail)" :alt="getAuthorName(postDetail)" class="author-avatar" />
          <div class="author-info">
            <div class="author-name">
              {{ getAuthorName(postDetail) }}
              <el-tag v-if="getAuthorRole(postDetail)" :type="getRoleTagType(getAuthorRole(postDetail))" size="small" effect="light" class="role-tag">
                {{ getAuthorRole(postDetail) }}
              </el-tag>
            </div>
            <div class="post-meta">
              <span>{{ postDetail.insertTime }}</span>
              <span>浏览 {{ postDetail.forumClicknum || 0 }}</span>
            </div>
          </div>
        </div>
        <el-tag v-if="postDetail.forumTypes" type="info" effect="light">{{ postDetail.forumTypesValue || getTypeName(postDetail.forumTypes) }}</el-tag>
      </div>

      <h1 class="post-title">{{ postDetail.forumName }}</h1>

      <div class="post-content" v-html="postDetail.forumContent || '<p>暂无内容</p>'"></div>

      <div class="post-actions">
        <el-button type="primary" round @click="showReplyDialog = true">
          <el-icon><Edit /></el-icon>回复
        </el-button>
        <el-button type="danger" round @click="handleDeletePost">
          <el-icon><Delete /></el-icon>删除帖子
        </el-button>
      </div>
    </el-card>

    <el-card class="replies-card" shadow="never" v-if="replyList.length > 0">
      <template #header>
        <div class="card-header">
          <span>{{ replyList.length }} 条回复</span>
        </div>
      </template>

      <div class="reply-list">
        <div class="reply-item" v-for="(reply, index) in replyList" :key="reply.id" :style="{ animationDelay: `${index * 0.05}s` }">
          <div class="reply-header">
            <img :src="getReplyAvatar(reply)" :alt="getReplyName(reply)" class="reply-avatar" />
            <div class="reply-info">
              <div class="reply-author">
                {{ getReplyName(reply) }}
                <el-tag v-if="getReplyRole(reply)" :type="getRoleTagType(getReplyRole(reply))" size="small" effect="light" class="role-tag">
                  {{ getReplyRole(reply) }}
                </el-tag>
              </div>
              <div class="reply-time">{{ reply.insertTime }}</div>
            </div>
          </div>
          <div class="reply-content">{{ reply.forumContent }}</div>
          <div class="reply-actions">
            <el-button type="danger" text size="small" @click="handleDeleteReply(reply)">
              <el-icon><Delete /></el-icon>删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-empty v-else description="暂无回复，快来抢沙发吧~" />

    <el-dialog v-model="showReplyDialog" title="回复帖子" width="600px" @close="resetReplyForm">
      <el-form :model="replyForm" label-width="80px" class="reply-form">
        <el-form-item label="回复内容" prop="content">
          <el-input v-model="replyForm.content" placeholder="请输入回复内容" type="textarea" rows="4" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showReplyDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="replyLoading">回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'
import { ArrowLeft, Edit, Delete } from '@element-plus/icons-vue'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const replyLoading = ref(false)
const postDetail = ref({})
const replyList = ref([])
const showReplyDialog = ref(false)
const forumTypesList = ref([])

const replyForm = reactive({
  content: ''
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

const getReplyAvatar = (reply) => {
  if (reply.shangjiaPhoto) return getAvatarUrl(reply.shangjiaPhoto)
  if (reply.yonghuPhoto) return getAvatarUrl(reply.yonghuPhoto)
  return defaultAvatar
}

const getReplyName = (reply) => {
  if (reply.shangjiaName) return reply.shangjiaName
  if (reply.yonghuName) return reply.yonghuName
  return '匿名用户'
}

const getReplyRole = (reply) => {
  if (reply.shangjiaId) return '商家'
  if (reply.yonghuId) return '用户'
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
      forumTypesList.value = res.data?.list || []
    }
  } catch (error) {
    console.error('Fetch forum types error:', error)
  }
}

const getTypeName = (type) => {
  const item = forumTypesList.value.find(t => t.codeIndex === type)
  return item ? item.indexName : ''
}

const fetchPostDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('无效的帖子ID')
    return
  }

  loading.value = true
  try {
    const res = await request.get(`/forum/info/${id}`)
    if (res.code === 0) {
      postDetail.value = res.data
      postDetail.value.forumContent = postDetail.value.forumContent?.replace(/\n/g, '<br>') || ''
    } else {
      ElMessage.error(res.msg || '获取帖子详情失败')
    }
  } catch (error) {
    console.error('Fetch post detail error:', error)
  } finally {
    loading.value = false
  }
}

const fetchReplyList = async () => {
  const id = route.params.id
  try {
    const res = await request.get('/forum/page', {
      params: {
        page: 1,
        limit: 100,
        superIds: id,
        forumStateTypes: 2
      }
    })
    if (res.code === 0) {
      replyList.value = res.data.list || []
    }
  } catch (error) {
    console.error('Fetch reply list error:', error)
  }
}

const submitReply = async () => {
  if (!replyForm.content) {
    ElMessage.warning('请输入回复内容')
    return
  }

  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录后再回复')
    router.push('/login')
    return
  }

  replyLoading.value = true
  try {
    const res = await request.post('/forum/save', {
      superIds: route.params.id,
      forumContent: replyForm.content,
      forumStateTypes: 2
    })
    if (res.code === 0) {
      ElMessage.success('回复成功')
      showReplyDialog.value = false
      resetReplyForm()
      fetchReplyList()
    } else {
      ElMessage.error(res.msg || '回复失败')
    }
  } catch (error) {
    console.error('Submit reply error:', error)
    ElMessage.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}

const handleDeletePost = () => {
  ElMessageBox.confirm(
    '确定要删除此帖子吗？删除后将同时删除所有回复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/forum/delete', [postDetail.value.id])
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        router.back()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleDeleteReply = (reply) => {
  ElMessageBox.confirm(
    '确定要删除此回复吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/forum/delete', [reply.id])
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        fetchReplyList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const resetReplyForm = () => {
  replyForm.content = ''
}

onMounted(() => {
  fetchForumTypes()
  fetchPostDetail()
  fetchReplyList()
})
</script>

<style scoped>
.forum-detail-container {
  padding: 30px;
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
  border-radius: 16px !important;
  background: rgba(255, 255, 255, 0.95) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.04) !important;
  padding: 30px;
  margin-bottom: 30px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px dashed #e2e8f0;
}

.author-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 1rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-tag {
  font-size: 0.75rem;
}

.post-meta {
  color: #94a3b8;
  font-size: 0.85rem;
  display: flex;
  gap: 15px;
}

.post-title {
  font-size: 2rem;
  color: #0f172a;
  margin: 0 0 25px 0;
  font-weight: 700;
  line-height: 1.4;
}

.post-content {
  font-size: 1.05rem;
  line-height: 1.8;
  color: #334155;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
}

.post-content :deep(p) {
  margin-bottom: 1em;
}

.post-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 15px 0;
}

.post-actions {
  display: flex;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.replies-card {
  border-radius: 16px !important;
  background: rgba(255, 255, 255, 0.95) !important;
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  margin-bottom: 30px;
}

.card-header {
  font-weight: 600;
  color: #1e293b;
}

.reply-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.reply-item {
  padding: 15px;
  background: #f8fafc;
  border-radius: 12px;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.reply-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.reply-info {
  display: flex;
  flex-direction: column;
}

.reply-author {
  font-weight: 600;
  color: #1e293b;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 6px;
}

.reply-time {
  color: #94a3b8;
  font-size: 0.8rem;
}

.reply-content {
  color: #475569;
  font-size: 0.95rem;
  line-height: 1.6;
  padding-left: 46px;
  margin-bottom: 8px;
}

.reply-actions {
  padding-left: 46px;
}

.reply-form {
  padding: 20px 0;
}
</style>
