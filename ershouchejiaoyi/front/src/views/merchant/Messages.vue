<template>
  <div class="messages-container">
    <div class="messages-header">
      <h1>汽车留言管理</h1>
      <div class="header-actions">
        <el-select
          v-model="filterVehicle"
          placeholder="筛选车辆"
          clearable
          style="width: 200px"
          @change="loadMessages"
        >
          <el-option
            v-for="vehicle in vehicles"
            :key="vehicle.id"
            :label="vehicle.cheliangName"
            :value="vehicle.id"
          />
        </el-select>
        <el-button type="primary" @click="loadMessages">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <div class="messages-content" v-loading="loading">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon" style="background: #dbeafe;">
            <el-icon color="#3b82f6" :size="28"><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ total }}</div>
            <div class="stat-label">留言总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #fef3c7;">
            <el-icon color="#f59e0b" :size="28"><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ unrepliedCount }}</div>
            <div class="stat-label">待回复</div>
          </div>
        </div>
      </div>

      <!-- 留言列表 -->
      <div v-if="messages.length === 0" class="empty-messages">
        <el-empty description="暂无留言" />
      </div>

      <div v-else class="messages-list">
        <div class="message-card" v-for="message in messages" :key="message.id">
          <div class="message-header">
            <div class="vehicle-info">
              <img
                v-if="message.cheliangPhoto"
                :src="getImageUrl(message.cheliangPhoto)"
                :alt="message.cheliangName"
                class="vehicle-thumb"
              />
              <div class="vehicle-details">
                <h4>{{ message.cheliangName }}</h4>
                <span class="vehicle-price">¥{{ message.cheliangNewMoney }} 万</span>
              </div>
            </div>
            <div class="message-status">
              <el-tag v-if="message.replyText" type="success" size="small">已回复</el-tag>
              <el-tag v-else type="warning" size="small">待回复</el-tag>
            </div>
          </div>

          <div class="message-body">
            <div class="user-message">
              <div class="message-meta">
                <span class="user-name">
                  <el-icon><User /></el-icon>
                  {{ message.yonghuName || '匿名用户' }}
                </span>
                <span class="message-time">{{ formatTime(message.insertTime) }}</span>
              </div>
              <div class="message-text">{{ message.cheliangLiuyanText }}</div>
            </div>

            <div v-if="message.replyText" class="reply-message">
              <div class="message-meta">
                <span class="user-name">
                  <el-icon><Shop /></el-icon>
                  商家回复
                </span>
                <span class="message-time">{{ formatTime(message.updateTime) }}</span>
              </div>
              <div class="message-text">{{ message.replyText }}</div>
            </div>
          </div>

          <div class="message-footer">
            <el-button
              v-if="!message.replyText"
              type="primary"
              size="small"
              @click="handleReply(message)"
            >
              <el-icon><ChatDotRound /></el-icon>
              回复
            </el-button>
            <el-button
              v-else
              type="default"
              size="small"
              @click="handleReply(message)"
            >
              <el-icon><Edit /></el-icon>
              修改回复
            </el-button>
            <el-button
              type="danger"
              text
              size="small"
              @click="handleDelete(message)"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="messages.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadMessages"
          @current-change="loadMessages"
        />
      </div>
    </div>

    <!-- 回复对话框 -->
    <el-dialog
      v-model="replyDialogVisible"
      :title="currentMessage?.replyText ? '修改回复' : '回复留言'"
      width="600px"
      @close="resetReplyForm"
    >
      <div v-if="currentMessage" class="reply-dialog-content">
        <div class="original-message">
          <label>用户留言：</label>
          <div class="message-box">{{ currentMessage.cheliangLiuyanText }}</div>
        </div>
        <el-form
          ref="replyFormRef"
          :model="replyForm"
          :rules="replyRules"
          label-width="80px"
        >
          <el-form-item label="回复内容" prop="replyText">
            <el-input
              v-model="replyForm.replyText"
              type="textarea"
              :rows="6"
              placeholder="请输入回复内容"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReply" :loading="submitLoading">
          提交
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotRound,
  Warning,
  Refresh,
  User,
  Shop,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import request from '../../utils/request'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

const loading = ref(false)
const messages = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterVehicle = ref('')
const vehicles = ref([])

const replyDialogVisible = ref(false)
const replyFormRef = ref(null)
const submitLoading = ref(false)
const currentMessage = ref(null)

const replyForm = reactive({
  replyText: ''
})

const replyRules = {
  replyText: [
    { required: true, message: '请输入回复内容', trigger: 'blur' },
    { min: 1, max: 500, message: '回复内容长度在 1 到 500 个字符', trigger: 'blur' }
  ]
}

// 计算未回复数量
const unrepliedCount = computed(() => {
  return messages.value.filter(m => !m.replyText).length
})

// 获取图片URL
const getImageUrl = (photo) => {
  if (!photo) return ''
  if (photo.startsWith('http')) return photo
  if (photo.startsWith('/')) return photo
  return `/ershouchejiaoyi/upload/${photo}`
}

// 加载车辆列表
const loadVehicles = async () => {
  loading.value = true
  try {
    // 使用 /cheliang/page 接口，后端会根据 session 自动过滤商家ID
    const res = await request.get('/cheliang/page', {
      params: {
        page: 1,
        limit: 1000
      }
    })

    console.log('车辆列表响应:', res)

    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      vehicles.value = pageData.list || []
      console.log('车辆列表:', vehicles.value)
      // 加载完车辆后再加载留言
      if (vehicles.value.length > 0) {
        await loadMessages()
      } else {
        messages.value = []
        total.value = 0
        loading.value = false
      }
    } else {
      ElMessage.error(res.msg || '加载车辆列表失败')
      loading.value = false
    }
  } catch (error) {
    console.error('加载车辆列表失败:', error)
    ElMessage.error('加载车辆列表失败')
    loading.value = false
  }
}

// 加载留言列表
const loadMessages = async () => {
  try {
    // 首先获取商家的车辆ID列表
    const vehicleIds = vehicles.value.map(v => v.id)
    console.log('车辆ID列表:', vehicleIds)

    if (vehicleIds.length === 0) {
      messages.value = []
      total.value = 0
      loading.value = false
      return
    }

    const params = {
      page: 1,
      limit: 1000
    }

    if (filterVehicle.value) {
      params.cheliangId = filterVehicle.value
    }

    const res = await request.get('/cheliangLiuyan/page', { params })

    console.log('留言列表响应:', res)

    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      let allMessages = pageData.list || []
      
      console.log('所有留言:', allMessages.length, '条')

      // 前端过滤：只显示属于该商家车辆的留言
      // 同时附加车辆信息到留言中
      const vehicleMap = {}
      vehicles.value.forEach(v => {
        vehicleMap[v.id] = v
      })
      
      const filteredMessages = allMessages.filter(msg =>
        vehicleIds.includes(msg.cheliangId)
      ).map(msg => ({
        ...msg,
        cheliangPhoto: vehicleMap[msg.cheliangId]?.cheliangPhoto || msg.cheliangPhoto,
        cheliangName: vehicleMap[msg.cheliangId]?.cheliangName || msg.cheliangName,
        cheliangNewMoney: vehicleMap[msg.cheliangId]?.cheliangNewMoney || msg.cheliangNewMoney
      }))
      
      console.log('过滤后留言:', filteredMessages.length, '条')

      messages.value = filteredMessages
      total.value = filteredMessages.length
    } else {
      ElMessage.error(res.msg || '加载留言列表失败')
    }
  } catch (error) {
    console.error('加载留言列表失败:', error)
    ElMessage.error('加载留言列表失败')
  } finally {
    loading.value = false
  }
}

// 回复留言
const handleReply = (message) => {
  currentMessage.value = message
  replyForm.replyText = message.replyText || ''
  replyDialogVisible.value = true
}

// 提交回复
const handleSubmitReply = async () => {
  if (!replyFormRef.value) return

  await replyFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const data = {
        id: currentMessage.value.id,
        replyText: replyForm.replyText
      }

      const res = await request.post('/cheliangLiuyan/update', data)

      if (res.code === 0 || res.code === 200) {
        ElMessage.success('回复成功')
        replyDialogVisible.value = false
        loadMessages()
      } else {
        ElMessage.error(res.msg || '回复失败')
      }
    } catch (error) {
      console.error('回复失败:', error)
      ElMessage.error('回复失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除留言
const handleDelete = (message) => {
  ElMessageBox.confirm(
    '确定要删除这条留言吗？此操作不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/cheliangLiuyan/delete', [message.id])

      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        loadMessages()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 重置回复表单
const resetReplyForm = () => {
  replyForm.replyText = ''
  currentMessage.value = null
  if (replyFormRef.value) {
    replyFormRef.value.clearValidate()
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadVehicles()
})
</script>

<style scoped>
.messages-container {
  padding: 30px;
}

.messages-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.messages-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.messages-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
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

.empty-messages {
  padding: 60px 20px;
  text-align: center;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.message-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.vehicle-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.vehicle-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  background: #f1f5f9;
}

.vehicle-details h4 {
  margin: 0 0 4px;
  font-size: 1rem;
  color: #1e293b;
  font-weight: 600;
}

.vehicle-price {
  color: #ef4444;
  font-weight: 600;
  font-size: 0.95rem;
}

.message-body {
  padding: 16px;
}

.user-message,
.reply-message {
  margin-bottom: 12px;
}

.message-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
  color: #475569;
  font-size: 0.9rem;
}

.message-time {
  font-size: 0.85rem;
  color: #94a3b8;
}

.message-text {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  color: #1e293b;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.reply-message .message-text {
  background: #eff6ff;
  border-left: 3px solid #3b82f6;
}

.message-footer {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f1f5f9;
  background: #fafbfc;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 24px;
}

/* 回复对话框 */
.reply-dialog-content {
  padding: 10px 0;
}

.original-message {
  margin-bottom: 20px;
}

.original-message label {
  display: block;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
  font-size: 14px;
}

.message-box {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  color: #1e293b;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
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
  .messages-container {
    padding: 16px;
  }

  .messages-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .header-actions :deep(.el-select) {
    width: 100% !important;
  }

  .messages-content {
    padding: 16px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .message-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .vehicle-info {
    width: 100%;
  }

  .message-footer {
    flex-wrap: wrap;
  }
}
</style>
