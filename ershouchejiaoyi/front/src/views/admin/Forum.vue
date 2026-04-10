<template>
  <div class="forum-container">
    <div class="forum-header">
      <h1>论坛管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索帖子标题或内容"
          clearable
          style="width: 300px"
          @clear="loadForums"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </div>

    <div class="forum-content" v-loading="loading">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon" style="background: #dbeafe;">
            <el-icon color="#3b82f6" :size="28"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ total }}</div>
            <div class="stat-label">帖子总数</div>
          </div>
        </div>
      </div>

      <!-- 论坛列表 -->
      <div class="table-container">
        <el-table :data="forums" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="forumName" label="帖子标题" width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span v-if="row.forumName">{{ row.forumName }}</span>
              <el-tag v-else type="info" size="small">回复</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="forumContent" label="内容" show-overflow-tooltip>
            <template #default="{ row }">
              <div class="content-text">{{ row.forumContent }}</div>
            </template>
          </el-table-column>
          <el-table-column label="发布者" width="120" align="center">
            <template #default="{ row }">
              <span v-if="row.yonghuName">{{ row.yonghuName }} (用户)</span>
              <span v-else-if="row.shangjiaName">{{ row.shangjiaName }} (商家)</span>
              <span v-else-if="row.usersId">管理员</span>
              <span v-else>未知</span>
            </template>
          </el-table-column>
          <el-table-column prop="forumStateTypes" label="类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.forumStateTypes === 1 ? 'primary' : 'success'" size="small">
                {{ row.forumStateTypes === 1 ? '发帖' : '回帖' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="insertTime" label="发布时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.insertTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadForums"
            @current-change="loadForums"
          />
        </div>
      </div>
    </div>

    <!-- 帖子详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="帖子详情"
      width="700px"
    >
      <div v-if="currentForum" class="forum-detail">
        <div class="detail-section" v-if="currentForum.forumName">
          <label>帖子标题：</label>
          <div>{{ currentForum.forumName }}</div>
        </div>
        <div class="detail-section">
          <label>帖子内容：</label>
          <div class="content-box">{{ currentForum.forumContent }}</div>
        </div>
        <div class="detail-section">
          <label>发布者：</label>
          <div>
            <span v-if="currentForum.yonghuName">{{ currentForum.yonghuName }} (用户)</span>
            <span v-else-if="currentForum.shangjiaName">{{ currentForum.shangjiaName }} (商家)</span>
            <span v-else-if="currentForum.usersId">管理员</span>
            <span v-else>未知</span>
          </div>
        </div>
        <div class="detail-section">
          <label>发布时间：</label>
          <div>{{ formatTime(currentForum.insertTime) }}</div>
        </div>
        <div class="detail-section" v-if="currentForum.updateTime">
          <label>更新时间：</label>
          <div>{{ formatTime(currentForum.updateTime) }}</div>
        </div>

        <!-- 如果是主帖，显示回复列表 -->
        <div v-if="currentForum.forumStateTypes === 1 && replies.length > 0" class="replies-section">
          <label>回复列表：</label>
          <div class="replies-list">
            <div v-for="reply in replies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-author">
                  <span v-if="reply.yonghuName">{{ reply.yonghuName }} (用户)</span>
                  <span v-else-if="reply.shangjiaName">{{ reply.shangjiaName }} (商家)</span>
                  <span v-else-if="reply.usersId">管理员</span>
                </span>
                <span class="reply-time">{{ formatTime(reply.insertTime) }}</span>
              </div>
              <div class="reply-content">{{ reply.forumContent }}</div>
              <el-button type="danger" text size="small" @click="handleDeleteReply(reply)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="danger" @click="handleDelete(currentForum)">删除此帖</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ChatDotRound, View, Delete } from '@element-plus/icons-vue'
import request from '../../utils/request'

const router = useRouter()

const loading = ref(false)
const forums = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const detailDialogVisible = ref(false)
const currentForum = ref(null)
const replies = ref([])
const forumTypesList = ref([])

const loadForumTypes = async () => {
  try {
    const res = await request.get('/dictionary/page', {
      params: {
        dicCode: 'forum_types',
        limit: 100
      }
    })
    if (res.code === 0 || res.code === 200) {
      forumTypesList.value = res.data?.list || []
    }
  } catch (error) {
    console.error('加载帖子分类失败:', error)
  }
}

const getTypeName = (type) => {
  const item = forumTypesList.value.find(t => t.codeIndex === type)
  return item ? item.indexName : '未知'
}

// 加载论坛列表
const loadForums = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value
    }

    if (searchKeyword.value) {
      params.forumName = searchKeyword.value
    }

    const res = await request.get('/forum/page', { params })

    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      forums.value = pageData.list || []
      total.value = pageData.total || 0
    } else {
      ElMessage.error(res.msg || '加载论坛列表失败')
    }
  } catch (error) {
    console.error('加载论坛列表失败:', error)
    ElMessage.error('加载论坛列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadForums()
}

// 查看详情
const handleView = (row) => {
  router.push(`/admin/forum/detail/${row.id}`)
}

// 加载回复列表
const loadReplies = async (forumId) => {
  try {
    const res = await request.get('/forum/list', {
      params: {
        superIds: forumId,
        forumStateTypes: 2
      }
    })

    if (res.code === 0 || res.code === 200) {
      replies.value = res.data || []
    }
  } catch (error) {
    console.error('加载回复失败:', error)
  }
}

// 删除帖子
const handleDelete = (row) => {
  const title = row.forumName || '此内容'
  ElMessageBox.confirm(
    `确定要删除"${title}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/forum/delete', [row.id])

      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        detailDialogVisible.value = false
        loadForums()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 删除回复
const handleDeleteReply = (reply) => {
  ElMessageBox.confirm(
    '确定要删除此回复吗？此操作不可恢复！',
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
        // 重新加载回复列表
        if (currentForum.value) {
          await loadReplies(currentForum.value.id)
        }
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadForumTypes()
  loadForums()
})
</script>

<style scoped>
.forum-container {
  min-height: 100%;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forum-header h1 {
  font-size: 1.75rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.forum-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #64748b;
}

.table-container {
  margin-top: 16px;
}

.content-text {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
  margin-top: 16px;
}

/* 详情对话框 */
.forum-detail {
  padding: 10px 0;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section label {
  display: block;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
  font-size: 14px;
}

.detail-section > div {
  color: #1e293b;
  line-height: 1.6;
}

.content-box {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  white-space: pre-wrap;
  word-break: break-word;
}

.replies-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 2px solid #f1f5f9;
}

.replies-section label {
  display: block;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
  font-size: 15px;
}

.replies-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-author {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: #94a3b8;
}

.reply-content {
  color: #475569;
  line-height: 1.6;
  margin-bottom: 8px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* Table 样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  font-weight: 600;
}

:deep(.el-table th.el-table__cell) {
  background: #f8fafc;
  color: #475569;
}

:deep(.el-table .el-table__cell) {
  padding: 14px 0;
}

/* 对话框样式优化 */
:deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
}

/* 响应式 */
@media (max-width: 768px) {
  .forum-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .header-actions :deep(.el-input) {
    width: 100% !important;
  }

  .forum-content {
    padding: 16px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
