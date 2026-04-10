<template>
  <div class="orders-container">
    <div class="orders-header">
      <h1>订单管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索订单号或车辆名称"
          clearable
          style="width: 300px"
          @clear="loadOrders"
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

    <div class="orders-content" v-loading="loading">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon" style="background: #fef3c7;">
            <el-icon color="#f59e0b" :size="28"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ total }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #dbeafe;">
            <el-icon color="#3b82f6" :size="28"><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending || 0 }}</div>
            <div class="stat-label">待支付</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #dcfce7;">
            <el-icon color="#10b981" :size="28"><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.completed || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon" style="background: #fee2e2;">
            <el-icon color="#ef4444" :size="28"><Close /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.cancelled || 0 }}</div>
            <div class="stat-label">已取消</div>
          </div>
        </div>
      </div>

      <!-- 状态筛选 -->
      <div class="filter-section">
        <el-radio-group v-model="filterStatus" @change="handleStatusFilter" class="status-filter">
          <el-radio-button label="">全部订单</el-radio-button>
          <el-radio-button label="1">待支付</el-radio-button>
          <el-radio-button label="2">已支付</el-radio-button>
          <el-radio-button label="3">已发货</el-radio-button>
          <el-radio-button label="4">已完成</el-radio-button>
          <el-radio-button label="5">已取消</el-radio-button>
          <el-radio-button label="6">已拒绝</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 订单列表 -->
      <div class="table-container">
        <el-table :data="orders" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="cheliangOrderUuidNumber" label="订单号" width="180" />
          <el-table-column prop="cheliangName" label="车辆名称" width="180" show-overflow-tooltip />
          <el-table-column prop="yonghuName" label="买家" width="120" />
          <el-table-column prop="shangjiaName" label="商家" width="120" />
          <el-table-column prop="cheliangOrderPrice" label="订单金额" width="120" align="right">
            <template #default="{ row }">
              <span class="price-text">¥{{ row.cheliangOrderPrice }} 万</span>
            </template>
          </el-table-column>
          <el-table-column prop="cheliangOrderStatusTypes" label="订单状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.cheliangOrderStatusTypes)" size="small">
                {{ getStatusText(row.cheliangOrderStatusTypes) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="insertTime" label="下单时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.insertTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="handleView(row)">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button
                v-if="row.cheliangOrderStatusTypes === 5 || row.cheliangOrderStatusTypes === 6"
                type="danger"
                text
                size="small"
                @click="handleDelete(row)"
              >
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
            @size-change="loadOrders"
            @current-change="loadOrders"
          />
        </div>
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <div v-if="currentOrder" class="order-detail">
        <div class="detail-section">
          <label>订单号：</label>
          <div>{{ currentOrder.cheliangOrderUuidNumber }}</div>
        </div>
        <div class="detail-section">
          <label>订单状态：</label>
          <div>
            <el-tag :type="getStatusTagType(currentOrder.cheliangOrderStatusTypes)">
              {{ getStatusText(currentOrder.cheliangOrderStatusTypes) }}
            </el-tag>
          </div>
        </div>
        <div class="detail-section">
          <label>车辆信息：</label>
          <div class="vehicle-info">
            <img
              v-if="currentOrder.cheliangPhoto"
              :src="getImageUrl(currentOrder.cheliangPhoto)"
              class="vehicle-image"
            />
            <div>
              <p><strong>车辆名称：</strong>{{ currentOrder.cheliangName }}</p>
              <p><strong>车辆品牌：</strong>{{ currentOrder.cheliangValue || '未知' }}</p>
            </div>
          </div>
        </div>
        <div class="detail-section">
          <label>订单金额：</label>
          <div class="price-highlight">¥{{ currentOrder.cheliangOrderPrice }} 万</div>
        </div>
        <div class="detail-section">
          <label>买家信息：</label>
          <div>
            <p><strong>买家姓名：</strong>{{ currentOrder.yonghuName }}</p>
            <p><strong>联系电话：</strong>{{ currentOrder.yonghuPhone || '未提供' }}</p>
          </div>
        </div>
        <div class="detail-section">
          <label>商家信息：</label>
          <div>
            <p><strong>商家名称：</strong>{{ currentOrder.shangjiaName }}</p>
            <p><strong>联系电话：</strong>{{ currentOrder.shangjiaPhone || '未提供' }}</p>
          </div>
        </div>
        <div class="detail-section">
          <label>下单时间：</label>
          <div>{{ formatTime(currentOrder.insertTime) }}</div>
        </div>
        <div class="detail-section" v-if="currentOrder.payTime">
          <label>支付时间：</label>
          <div>{{ formatTime(currentOrder.payTime) }}</div>
        </div>
        <div class="detail-section" v-if="currentOrder.finishTime">
          <label>完成时间：</label>
          <div>{{ formatTime(currentOrder.finishTime) }}</div>
        </div>
        <div class="detail-section" v-if="currentOrder.cancelReason">
          <label>取消原因：</label>
          <div class="cancel-reason">{{ currentOrder.cancelReason }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ShoppingCart, Clock, Check, Close, View, Delete } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const orders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterStatus = ref('')

const detailDialogVisible = ref(false)
const currentOrder = ref(null)

const stats = reactive({
  pending: 0,
  completed: 0,
  cancelled: 0
})

const statusMap = {
  1: '待支付',
  2: '已支付',
  3: '已发货',
  4: '已完成',
  5: '已取消',
  6: '已拒绝'
}

const getStatusText = (status) => {
  return statusMap[status] || '未知'
}

const getStatusTagType = (status) => {
  const typeMap = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger',
    6: 'danger'
  }
  return typeMap[status] || ''
}

// 获取图片URL
const getImageUrl = (photo) => {
  if (!photo) return ''
  if (photo.startsWith('http')) return photo
  if (photo.startsWith('/')) return photo
  return `/ershouchejiaoyi/upload/${photo}`
}

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value
    }

    if (searchKeyword.value) {
      params.cheliangOrderUuidNumber = searchKeyword.value
    }

    if (filterStatus.value) {
      params.cheliangOrderStatusTypes = filterStatus.value
    }

    const res = await request.get('/cheliangOrder/page', { params })

    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      orders.value = pageData.list || []
      total.value = pageData.total || 0

      // 计算统计数据
      calculateStats()
    } else {
      ElMessage.error(res.msg || '加载订单列表失败')
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

// 计算统计数据
const calculateStats = async () => {
  try {
    // 获取各状态订单数量
    const pendingRes = await request.get('/cheliangOrder/page', {
      params: { page: 1, limit: 1, cheliangOrderStatusTypes: 1 }
    })
    const completedRes = await request.get('/cheliangOrder/page', {
      params: { page: 1, limit: 1, cheliangOrderStatusTypes: 4 }
    })
    const cancelledRes = await request.get('/cheliangOrder/page', {
      params: { page: 1, limit: 1, cheliangOrderStatusTypes: 5 }
    })

    stats.pending = pendingRes.data?.total || 0
    stats.completed = completedRes.data?.total || 0
    stats.cancelled = cancelledRes.data?.total || 0
  } catch (error) {
    console.error('计算统计数据失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

// 状态筛选
const handleStatusFilter = () => {
  currentPage.value = 1
  loadOrders()
}

// 查看详情
const handleView = async (row) => {
  try {
    const res = await request.get(`/cheliangOrder/info/${row.id}`)

      if (res.code === 0 || res.code === 200) {
        currentOrder.value = res.data || row
        detailDialogVisible.value = true
      } else {
        ElMessage.error(res.msg || '获取订单详情失败')
      }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 删除订单
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除订单"${row.cheliangOrderUuidNumber}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/cheliangOrder/delete', [row.id])

      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        loadOrders()
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
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  min-height: 100%;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.orders-header h1 {
  font-size: 1.75rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.orders-content {
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

.filter-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.status-filter {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.table-container {
  margin-top: 16px;
}

.price-text {
  color: #ef4444;
  font-weight: 600;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
  margin-top: 16px;
}

/* 订单详情 */
.order-detail {
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

.detail-section p {
  margin: 6px 0;
  color: #334155;
}

.vehicle-info {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.vehicle-image {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.price-highlight {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ef4444;
}

.cancel-reason {
  padding: 12px;
  background: #fef2f2;
  border-left: 3px solid #ef4444;
  border-radius: 4px;
  color: #dc2626;
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
  .orders-header {
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

  .orders-content {
    padding: 16px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .vehicle-info {
    flex-direction: column;
  }
}
</style>
