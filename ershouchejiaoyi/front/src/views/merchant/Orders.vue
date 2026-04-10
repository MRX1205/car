<template>
  <div class="orders-container">
    <div class="orders-header">
      <h1>订单管理</h1>
    </div>

    <div class="orders-content" v-loading="loading">
      <!-- 状态筛选 -->
      <div class="filter-section">
        <el-radio-group v-model="filterStatus" @change="loadOrders" class="status-filter">
          <el-radio label="">全部订单</el-radio>
          <el-radio label="1">待支付</el-radio>
          <el-radio label="2">已支付</el-radio>
          <el-radio label="3">已发货</el-radio>
          <el-radio label="4">已完成</el-radio>
          <el-radio label="5">已取消</el-radio>
        </el-radio-group>
      </div>

      <!-- 订单列表 -->
      <div v-if="orders.length === 0" class="empty-orders">
        <el-empty description="暂无订单" />
      </div>

      <div v-else class="orders-list">
        <div class="order-card" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <div class="order-info">
              <span class="order-number">订单号：{{ order.cheliangOrderUuidNumber }}</span>
              <span class="order-time">{{ formatTime(order.insertTime) }}</span>
            </div>
            <span :class="['order-status', `status-${order.cheliangOrderStatusTypes}`]">
              {{ getStatusText(order.cheliangOrderStatusTypes) }}
            </span>
          </div>

          <div class="order-body">
            <div class="order-item">
              <img :src="order.cheliangPhoto" :alt="order.cheliangName" class="item-image" />
              <div class="item-details">
                <h4>{{ order.cheliangName }}</h4>
                <p>买家：{{ order.yonghuName }}</p>
                <p>联系电话：{{ order.yonghuPhone }}</p>
              </div>
              <div class="item-price">
                <span class="price">¥{{ order.cheliangOrderPrice }} 万</span>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-total">
              <span>订单金额：</span>
              <span class="total-price">¥{{ order.cheliangOrderPrice }} 万</span>
            </div>

            <div class="order-actions">
              <!-- 已支付：发货 -->
              <template v-if="order.cheliangOrderStatusTypes === 2">
                <el-button type="primary" size="small" @click="shipOrder(order.id)">
                  发货
                </el-button>
                <el-button size="small" @click="rejectOrder(order.id)">
                  拒绝
                </el-button>
                <el-button type="danger" size="small" @click="deleteOrder(order.id)">
                  删除
                </el-button>
              </template>

              <!-- 已发货：完成 -->
              <template v-if="order.cheliangOrderStatusTypes === 3">
                <el-button type="primary" size="small" @click="completeOrder(order.id)">
                  完成
                </el-button>
                <el-button type="danger" size="small" @click="deleteOrder(order.id)">
                  删除
                </el-button>
              </template>

              <!-- 其他状态：查看详情 + 删除 -->
              <template v-if="![2, 3].includes(order.cheliangOrderStatusTypes)">
                <el-button type="primary" text @click="viewDetail(order.id)">
                  查看详情
                </el-button>
                <el-button type="danger" size="small" @click="deleteOrder(order.id)">
                  删除
                </el-button>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="orders.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @change="loadOrders"
        />
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="550px">
      <div v-if="currentOrder" class="order-detail-info">
        <div class="detail-section">
          <h4>订单信息</h4>
          <p><strong>交易单号：</strong>{{ currentOrder.cheliangOrderUuidNumber }}</p>
          <p>
            <strong>交易状态：</strong>
            <span :class="['status-badge', `status-${currentOrder.cheliangOrderStatusTypes}`]">
              {{ getStatusText(currentOrder.cheliangOrderStatusTypes) }}
            </span>
          </p>
          <p v-if="currentOrder.cheliangOrderStatusTypes === 6 && currentOrder.cancelReason">
            <strong>拒绝原因：</strong>
            <span class="reject-reason">{{ currentOrder.cancelReason }}</span>
          </p>
        </div>
        
        <div class="detail-section">
          <h4>车辆信息</h4>
          <div class="vehicle-detail">
            <img :src="currentOrder.cheliangPhoto" :alt="currentOrder.cheliangName" class="vehicle-image" />
            <div class="vehicle-info">
              <p><strong>车辆名称：</strong>{{ currentOrder.cheliangName }}</p>
              <p><strong>实付金额：</strong><span class="price">¥{{ currentOrder.cheliangOrderPrice }} 万</span></p>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h4>买家信息</h4>
          <p><strong>买家名称：</strong>{{ currentOrder.yonghuName }}</p>
          <p><strong>买家电话：</strong>{{ currentOrder.yonghuPhone }}</p>
        </div>
        
        <div class="detail-section">
          <h4>商家信息</h4>
          <p><strong>商家名称：</strong>{{ currentOrder.shangjiaName }}</p>
          <p><strong>商家电话：</strong>{{ currentOrder.shangjiaPhone }}</p>
        </div>
        
        <div class="detail-section">
          <h4>时间信息</h4>
          <p><strong>下单时间：</strong>{{ formatTime(currentOrder.insertTime) }}</p>
          <p v-if="currentOrder.cheliangOrderStatusTypes >= 3">
            <strong>发货时间：</strong>{{ formatTime(currentOrder.updateTime) }}
          </p>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as orderApi from '../../api/order'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()

const loading = ref(false)
const orders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const detailVisible = ref(false)
const currentOrder = ref(null)

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

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value
    }

    if (filterStatus.value) {
      params.cheliangOrderStatusTypes = filterStatus.value
    }

    const res = await orderApi.getOrderList(params)
    if (res.code === 0 || res.code === 200) {
      // 后端分页数据结构: res.data = { list: [], total, pageSize, currPage }
      const pageData = res.data || {}
      orders.value = pageData.list || []
      total.value = pageData.total || 0
    } else {
      console.error('加载订单失败:', res)
      ElMessage.error(res.msg || '加载订单失败')
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const shipOrder = async (orderId) => {
  ElMessageBox.confirm(
    '确定要发货吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderApi.shipOrder(orderId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('已发货')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '发货失败')
      }
    } catch (error) {
      console.error('发货失败:', error)
      ElMessage.error('发货失败')
    }
  }).catch(() => {})
}

const rejectOrder = async (orderId) => {
  ElMessageBox.prompt(
    '请输入拒绝原因',
    '拒绝订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.{1,}/,
      inputErrorMessage: '拒绝原因不能为空',
    }
  ).then(async ({ value }) => {
    try {
      const res = await orderApi.rejectOrder(orderId, value)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('订单已拒绝')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '拒绝失败')
      }
    } catch (error) {
      console.error('拒绝失败:', error)
      ElMessage.error('拒绝失败')
    }
  }).catch(() => {})
}

const completeOrder = async (orderId) => {
  ElMessageBox.confirm(
    '确定订单已完成吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderApi.completeOrder(orderId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('订单已完成')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '完成失败')
      }
    } catch (error) {
      console.error('完成失败:', error)
      ElMessage.error('完成失败')
    }
  }).catch(() => {})
}

const deleteOrder = async (orderId) => {
  ElMessageBox.confirm(
    '确定要删除该订单吗？删除后无法恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderApi.deleteOrder(orderId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('订单已删除')
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

const viewDetail = async (orderId) => {
  try {
    const res = await orderApi.getOrderDetail(orderId)
    if (res.code === 0 || res.code === 200) {
      currentOrder.value = res.data
      detailVisible.value = true
    } else {
      ElMessage.error(res.msg || '获取详情失败')
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

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
  padding: 30px;
}

.orders-header {
  margin-bottom: 30px;
}

.orders-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.orders-content {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.filter-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.status-filter {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.empty-orders {
  padding: 60px 20px;
  text-align: center;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.order-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-bottom: 1px solid #f1f5f9;
}

.order-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.order-number {
  font-weight: 600;
  color: #1e293b;
}

.order-time {
  font-size: 0.9rem;
  color: #64748b;
}

.order-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
}

.status-1 {
  background: #f59e0b;
}

.status-2 {
  background: #3b82f6;
}

.status-3 {
  background: #8b5cf6;
}

.status-4 {
  background: #10b981;
}

.status-5 {
  background: #ef4444;
}

.status-6 {
  background: #dc2626;
  border: 2px solid #b91c1c;
  box-shadow: 0 0 0 2px rgba(220, 38, 38, 0.2);
}

.order-body {
  padding: 16px;
}

.order-item {
  display: grid;
  grid-template-columns: 100px 1fr auto;
  gap: 16px;
  align-items: center;
}

.item-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  background: #f1f5f9;
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-details h4 {
  margin: 0;
  font-size: 1rem;
  color: #1e293b;
  font-weight: 600;
}

.item-details p {
  margin: 0;
  font-size: 0.9rem;
  color: #64748b;
}

.item-price {
  text-align: right;
}

.price {
  font-size: 1.25rem;
  color: #ef4444;
  font-weight: 700;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
}

.order-total {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.total-price {
  color: #ef4444;
  font-size: 1.25rem;
}

.order-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .orders-container {
    padding: 16px;
  }

  .orders-content {
    padding: 16px;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .order-info {
    flex-direction: column;
    gap: 4px;
  }

  .order-item {
    grid-template-columns: 80px 1fr;
  }

  .item-image {
    width: 80px;
    height: 80px;
  }

  .item-price {
    grid-column: 1 / -1;
    text-align: left;
    margin-top: 8px;
  }

  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .order-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .order-actions :deep(.el-button) {
    flex: 1;
    min-width: 80px;
  }
}

.order-detail-info p {
  margin: 10px 0;
  line-height: 1.5;
  color: #334155;
  font-size: 15px;
}
.order-detail-info strong {
  color: #64748b;
  display: inline-block;
  width: 80px;
}

.detail-section {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.detail-section h4 {
  margin: 0 0 12px;
  font-size: 15px;
  color: #1e293b;
  font-weight: 600;
}

.vehicle-detail {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.vehicle-image {
  width: 120px;
  height: 90px;
  object-fit: cover;
  border-radius: 8px;
  background: #f1f5f9;
}

.vehicle-info {
  flex: 1;
}

.vehicle-info p {
  margin: 6px 0;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: white;
}

.reject-reason {
  display: inline-block;
  padding: 8px 12px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  color: #dc2626;
  font-size: 14px;
}

.price {
  color: #ef4444;
  font-weight: 700;
  font-size: 16px;
}
</style>
