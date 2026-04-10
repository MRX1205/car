<template>
  <div class="orders-container">
    <div class="orders-header">
      <h1>交易订单</h1>
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
          <el-radio label="6">已拒绝</el-radio>
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
                <p>{{ order.cheliangContent }}</p>
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
              <el-button type="primary" text @click="viewDetail(order.id)">
                查看详情
              </el-button>

              <!-- 待支付：支付、取消 -->
              <template v-if="order.cheliangOrderStatusTypes === 1">
                <el-button type="primary" size="small" @click="payOrder(order.id)">
                  支付
                </el-button>
                <el-button size="small" @click="cancelOrder(order.id)">
                  取消
                </el-button>
              </template>

              <!-- 已发货：确认收货 -->
              <template v-if="order.cheliangOrderStatusTypes === 3">
                <el-button type="primary" size="small" @click="confirmOrder(order.id)">
                  确认收货
                </el-button>
              </template>

              <!-- 已完成：评价 -->
              <template v-if="order.cheliangOrderStatusTypes === 4">
                <el-button type="primary" size="small" @click="goToReview(order.id)">
                  评价
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
    <el-dialog v-model="detailVisible" title="订单详情" width="500px">
      <div v-if="currentOrder" class="order-detail-info">
        <p><strong>交易单号：</strong>{{ currentOrder.cheliangOrderUuidNumber }}</p>
        <p><strong>订单状态：</strong>
          <el-tag :type="getStatusTagType(currentOrder.cheliangOrderStatusTypes)">
            {{ getStatusText(currentOrder.cheliangOrderStatusTypes) }}
          </el-tag>
        </p>
        <p><strong>车辆名称：</strong>{{ currentOrder.cheliangName }}</p>
        <p><strong>实付金额：</strong>¥{{ currentOrder.cheliangOrderPrice }} 万</p>
        <p><strong>买家名称：</strong>{{ currentOrder.yonghuName }}</p>
        <p><strong>买家电话：</strong>{{ currentOrder.yonghuPhone }}</p>
        <p><strong>商家名称：</strong>{{ currentOrder.shangjiaName }}</p>
        <p><strong>商家电话：</strong>{{ currentOrder.shangjiaPhone }}</p>
        <p><strong>创建时间：</strong>{{ formatTime(currentOrder.insertTime) }}</p>
        <div v-if="currentOrder.cheliangOrderStatusTypes === 6 && currentOrder.cancelReason" class="reject-reason">
          <p><strong>拒绝原因：</strong></p>
          <div class="reason-box">{{ currentOrder.cancelReason }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewVisible" title="订单评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.text"
            type="textarea"
            rows="4"
            placeholder="写下你对这辆车的评价吧..."
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewLoading">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as orderApi from '../../api/order'
import * as communityApi from '../../api/community'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const orders = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const detailVisible = ref(false)
const currentOrder = ref(null)
const reviewVisible = ref(false)
const reviewLoading = ref(false)
const reviewForm = ref({ text: '', cheliangId: null })

const statusMap = {
  1: '待支付',
  2: '已支付',
  3: '已发货',
  4: '已完成',
  5: '已取消',
  6: '商家已拒绝'
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
      ElMessage.error(res.msg || '加载订单失败')
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
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

const payOrder = async (orderId) => {
  ElMessageBox.confirm(
    '确定要支付此订单吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderApi.payOrder(orderId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('支付成功')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '支付失败')
      }
    } catch (error) {
      console.error('支付失败:', error)
      ElMessage.error('支付失败')
    }
  }).catch(() => {})
}

const cancelOrder = async (orderId) => {
  ElMessageBox.prompt(
    '请输入取消原因',
    '取消订单',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.{1,}/,
      inputErrorMessage: '取消原因不能为空',
    }
  ).then(async ({ value }) => {
    try {
      const res = await orderApi.cancelOrder(orderId, value)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('订单已取消')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '取消失败')
      }
    } catch (error) {
      console.error('取消失败:', error)
      ElMessage.error('取消失败')
    }
  }).catch(() => {})
}

const confirmOrder = async (orderId) => {
  ElMessageBox.confirm(
    '确定已收到货物吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderApi.confirmOrder(orderId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('已确认收货')
        loadOrders()
      } else {
        ElMessage.error(res.msg || '确认失败')
      }
    } catch (error) {
      console.error('确认失败:', error)
      ElMessage.error('确认失败')
    }
  }).catch(() => {})
}

const goToReview = (orderId) => {
  const order = orders.value.find(o => o.id === orderId)
  if (order) {
    reviewForm.value = { text: '', cheliangId: order.cheliangId }
    reviewVisible.value = true
  }
}

const submitReview = async () => {
  if (!reviewForm.value.text.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  reviewLoading.value = true
  try {
    const res = await communityApi.addCheliangComment({
      cheliangId: reviewForm.value.cheliangId,
      cheliangLiuyanText: reviewForm.value.text
    })
    if (res.code === 0 || res.code === 200) {
      ElMessage.success('评价成功')
      reviewVisible.value = false
    } else {
      ElMessage.error(res.msg || '评价失败')
    }
  } catch (error) {
    ElMessage.error('评价失败')
  } finally {
    reviewLoading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  padding: 40px 20px;
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
  border-radius: 16px;
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
  background: transparent;
  color: #ef4444;
  border: 2px solid #ef4444;
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
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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

@media (max-width: 480px) {
  .orders-container {
    padding: 20px 12px;
  }

  .orders-content {
    padding: 16px;
  }

  .status-filter {
    gap: 8px;
  }

  .status-filter :deep(.el-radio) {
    font-size: 0.9rem;
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

.reject-reason {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.reason-box {
  margin-top: 8px;
  padding: 12px 16px;
  background: #ef4444;
  color: white;
  border-radius: 8px;
  line-height: 1.6;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.2);
}
</style>
