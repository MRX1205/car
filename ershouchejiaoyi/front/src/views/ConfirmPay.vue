<template>
  <div class="confirm-pay-container">
    <div class="confirm-pay-wrapper">
      <el-card class="confirm-pay-card">
        <template #header>
          <div class="card-header">
            <el-icon class="header-icon"><Lock /></el-icon>
            <span>确认支付</span>
          </div>
        </template>

        <div class="pay-content" v-loading="loading">
          <!-- 订单信息 -->
          <div class="order-section">
            <h3 class="section-title">订单信息</h3>
            <div class="order-info-card">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="订单编号">
                  <span class="order-number">{{ orderInfo?.cheliangOrderUuidNumber || '-' }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="下单时间">
                  {{ formatTime(orderInfo?.insertTime) }}
                </el-descriptions-item>
                <el-descriptions-item label="订单状态">
                  <el-tag type="warning">待支付</el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>

          <!-- 车辆信息 -->
          <div class="vehicle-section">
            <h3 class="section-title">车辆信息</h3>
            <div class="vehicle-info-card" v-if="orderInfo">
              <div class="vehicle-image">
                <img :src="getVehicleImage()" :alt="orderInfo.cheliangName" />
              </div>
              <div class="vehicle-details">
                <h4 class="vehicle-name">{{ orderInfo.cheliangName }}</h4>
                <p class="vehicle-brand" v-if="orderInfo.cheliangTypes">
                  品牌编号：{{ orderInfo.cheliangTypes }}
                </p>
                <div class="vehicle-desc" v-if="orderInfo.cheliangContent">
                  {{ truncateText(orderInfo.cheliangContent, 100) }}
                </div>
              </div>
            </div>
          </div>

          <!-- 商家信息 -->
          <div class="merchant-section" v-if="orderInfo?.shangjiaName">
            <h3 class="section-title">商家信息</h3>
            <div class="merchant-info-card">
              <div class="info-item">
                <el-icon><Shop /></el-icon>
                <span>商家名称：{{ orderInfo.shangjiaName }}</span>
              </div>
              <div class="info-item" v-if="orderInfo.shangjiaPhone">
                <el-icon><Phone /></el-icon>
                <span>联系电话：{{ orderInfo.shangjiaPhone }}</span>
              </div>
            </div>
          </div>

          <!-- 支付金额 -->
          <div class="amount-section">
            <div class="amount-row">
              <span class="amount-label">订单金额</span>
              <span class="amount-value">¥{{ orderInfo?.cheliangOrderPrice || 0 }} 万</span>
            </div>
            <div class="amount-row total">
              <span class="amount-label">实付金额</span>
              <span class="amount-value total-amount">¥{{ orderInfo?.cheliangOrderPrice || 0 }} 万</span>
            </div>
          </div>

          <!-- 支付密码输入 -->
          <div class="password-section">
            <h3 class="section-title">
              <el-icon><Lock /></el-icon>
              输入支付密码
            </h3>
            <el-form :model="payForm" :rules="payRules" ref="payFormRef">
              <el-form-item prop="payPassword">
                <el-input
                  v-model="payForm.payPassword"
                  type="password"
                  placeholder="请输入支付密码"
                  show-password
                  size="large"
                  maxlength="20"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-form>
            <div class="password-tips">
              <el-icon><InfoFilled /></el-icon>
              <span>支付密码默认与登录密码相同，可在个人中心修改</span>
            </div>
          </div>

          <!-- 支付按钮 -->
          <div class="pay-actions">
            <el-button size="large" @click="handleCancel">取消支付</el-button>
            <el-button
              type="primary"
              size="large"
              @click="handleConfirmPay"
              :loading="paying"
            >
              <el-icon v-if="!paying"><Check /></el-icon>
              确认支付
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Lock,
  Shop,
  Phone,
  InfoFilled,
  Check
} from '@element-plus/icons-vue'
import * as orderApi from '../api/order'
import * as userApi from '../api/user'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const paying = ref(false)
const orderId = ref(route.query.orderId || '')
const orderInfo = ref(null)
const payFormRef = ref(null)

const payForm = reactive({
  payPassword: ''
})

const payRules = {
  payPassword: [
    { required: true, message: '请输入支付密码', trigger: 'blur' },
    { min: 6, max: 20, message: '支付密码长度为 6-20 个字符', trigger: 'blur' }
  ]
}

// 获取车辆图片
const getVehicleImage = () => {
  if (!orderInfo.value?.cheliangPhoto) {
    return 'https://via.placeholder.com/400x300?text=No+Image'
  }
  const photo = orderInfo.value.cheliangPhoto
  if (photo.startsWith('http') || photo.startsWith('/')) {
    return photo.split(',')[0]
  }
  return `/ershouchejiaoyi/upload/${photo.split(',')[0]}`
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  const cleanText = text.replace(/<[^>]*>/g, '')
  if (cleanText.length <= maxLength) return cleanText
  return cleanText.substring(0, maxLength) + '...'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载订单信息
const loadOrderInfo = async () => {
  if (!orderId.value) {
    ElMessage.error('订单ID不存在')
    router.back()
    return
  }

  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(orderId.value)
    if (res.code === 0 || res.code === 200) {
      orderInfo.value = res.data

      // 检查订单状态
      if (orderInfo.value.cheliangOrderStatusTypes !== 1) {
        ElMessage.warning('该订单无需支付或已支付')
        router.push('/user/orders')
      }
    } else {
      ElMessage.error(res.msg || '加载订单信息失败')
      router.back()
    }
  } catch (error) {
    console.error('加载订单信息失败:', error)
    ElMessage.error('加载订单信息失败')
    router.back()
  } finally {
    loading.value = false
  }
}

// 验证支付密码
const verifyPayPassword = async (password) => {
  try {
    // 获取用户信息，验证支付密码
    const userId = authStore.userInfo?.id
    const res = await userApi.getUserInfo(userId)

    if (res.code === 0 || res.code === 200) {
      const userData = res.data
      // 支付密码优先，如果没有则使用登录密码
      const correctPassword = userData.payPassword || userData.password

      if (password === correctPassword) {
        return true
      } else {
        ElMessage.error('支付密码错误')
        return false
      }
    } else {
      ElMessage.error('验证失败，请重试')
      return false
    }
  } catch (error) {
    console.error('验证支付密码失败:', error)
    ElMessage.error('验证失败，请重试')
    return false
  }
}

// 确认支付
const handleConfirmPay = async () => {
  if (!payFormRef.value) return

  await payFormRef.value.validate(async (valid) => {
    if (!valid) return

    // 验证支付密码
    const isPasswordCorrect = await verifyPayPassword(payForm.payPassword)
    if (!isPasswordCorrect) {
      return
    }

    // 二次确认
    ElMessageBox.confirm(
      `确认支付 ¥${orderInfo.value.cheliangOrderPrice} 万元？`,
      '确认支付',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    ).then(async () => {
      paying.value = true
      try {
        const res = await orderApi.payOrder(orderId.value)
        if (res.code === 0 || res.code === 200) {
          ElMessage.success('支付成功')
          setTimeout(() => {
            router.push(`/pay-success?orderId=${orderId.value}&amount=${orderInfo.value.cheliangOrderPrice}`)
          }, 500)
        } else {
          ElMessage.error(res.msg || '支付失败')
        }
      } catch (error) {
        console.error('支付失败:', error)
        ElMessage.error('支付失败，请重试')
      } finally {
        paying.value = false
      }
    }).catch(() => {
      // 用户取消支付确认
    })
  })
}

// 取消支付
const handleCancel = () => {
  ElMessageBox.confirm(
    '确定要取消支付吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '返回',
      type: 'warning',
    }
  ).then(() => {
    router.back()
  }).catch(() => {})
}

onMounted(() => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadOrderInfo()
})
</script>

<style scoped>
.confirm-pay-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.confirm-pay-wrapper {
  width: 100%;
  max-width: 1000px;
}

.confirm-pay-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

:deep(.el-card__header) {
  padding: 30px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-bottom: none;
}

:deep(.el-card__body) {
  padding: 40px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.8rem;
  font-weight: 700;
  color: white;
}

.header-icon {
  font-size: 2rem;
}

.pay-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.2rem;
  color: #1e293b;
  font-weight: 600;
  margin: 0 0 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f1f5f9;
}

/* 订单信息 */
.order-info-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
}

.order-number {
  font-family: monospace;
  font-weight: 600;
  color: #3b82f6;
}

/* 车辆信息 */
.vehicle-info-card {
  display: flex;
  gap: 24px;
  padding: 24px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.vehicle-image {
  flex-shrink: 0;
  width: 200px;
  height: 150px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.vehicle-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.vehicle-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.vehicle-name {
  font-size: 1.3rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.vehicle-brand {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0;
}

.vehicle-desc {
  font-size: 0.9rem;
  color: #64748b;
  line-height: 1.6;
  margin-top: 8px;
}

/* 商家信息 */
.merchant-info-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 12px;
  border: 1px solid #bae6fd;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #0369a1;
  font-size: 1rem;
}

.info-item .el-icon {
  font-size: 1.1rem;
}

/* 金额区域 */
.amount-section {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  padding: 24px;
  border-radius: 12px;
  border: 2px solid #fbbf24;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.amount-row.total {
  border-top: 2px dashed #f59e0b;
  padding-top: 20px;
  margin-top: 8px;
}

.amount-label {
  font-size: 1.1rem;
  color: #78350f;
  font-weight: 500;
}

.amount-value {
  font-size: 1.3rem;
  color: #ef4444;
  font-weight: 700;
}

.total-amount {
  font-size: 2rem;
}

/* 支付密码 */
.password-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  border: 2px solid #e5e7eb;
}

.password-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #eff6ff;
  border-radius: 8px;
  color: #3b82f6;
  font-size: 0.9rem;
  margin-top: 12px;
}

/* 按钮 */
.pay-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 20px;
}

.pay-actions :deep(.el-button) {
  padding: 16px 48px;
  font-size: 1.1rem;
  border-radius: 30px;
  font-weight: 600;
}

/* 响应式 */
@media (max-width: 768px) {
  .confirm-pay-container {
    padding: 20px 12px;
  }

  :deep(.el-card__header),
  :deep(.el-card__body) {
    padding: 20px;
  }

  .card-header {
    font-size: 1.4rem;
  }

  .vehicle-info-card {
    flex-direction: column;
  }

  .vehicle-image {
    width: 100%;
    height: 200px;
  }

  .pay-actions {
    flex-direction: column;
  }

  .pay-actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
