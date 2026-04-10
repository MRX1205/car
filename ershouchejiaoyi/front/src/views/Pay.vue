<template>
  <div class="pay-container">
    <el-card class="pay-card">
      <template #header>
        <div class="card-header">
          <span>订单支付</span>
        </div>
      </template>

      <!-- 订单信息 -->
      <div class="order-info" v-loading="loading">
        <div class="car-info" v-if="orderInfo">
          <img :src="getCover(orderInfo.cheliangPhoto)" class="car-cover" v-if="orderInfo.cheliangPhoto" />
          <div class="car-details">
            <h4 class="car-name">{{ orderInfo.cheliangName }}</h4>
            <p class="car-merchant" v-if="orderInfo.shangjiaName">商家: {{ orderInfo.shangjiaName }}</p>
          </div>
        </div>
        <el-divider v-if="orderInfo" />
        <div class="info-item">
          <span class="label">交易单号:</span>
          <span class="value">{{ orderInfo?.cheliangOrderUuidNumber || orderId }}</span>
        </div>
        <div class="info-item">
          <span class="label">支付金额:</span>
          <span class="value amount">¥{{ totalAmount }} 万</span>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <h3>选择支付方式</h3>
        <el-radio-group v-model="paymentMethod" class="method-group">
          <el-radio label="wechat" class="method-option">
            <div class="method-content">
              <span>💳 微信支付</span>
            </div>
          </el-radio>
          <el-radio label="alipay" class="method-option">
            <div class="method-content">
              <span>💳 支付宝支付</span>
            </div>
          </el-radio>
        </el-radio-group>
      </div>

      <!-- 支付按钮 -->
      <div class="pay-actions">
        <el-button @click="$router.back()">返回</el-button>
        <el-button type="primary" @click="handlePay" :loading="paying">
          确认支付
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as orderApi from '../api/order'

const router = useRouter()
const route = useRoute()

const orderId = ref(route.query.orderId || '')
const orderInfo = ref(null)
const totalAmount = ref(0)
const paymentMethod = ref('wechat')
const paying = ref(false)
const loading = ref(false)

const getCover = (photo) => {
  if (!photo) return ''
  return photo.split(',')[0]
}

onMounted(() => {
  loadOrderInfo()
})

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
      totalAmount.value = res.data.cheliangOrderPrice || 0
    }
  } catch (error) {
    ElMessage.error('加载订单信息失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  if (!paymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  // 跳转到确认支付页面
  router.push(`/confirm-pay?orderId=${orderId.value}`)
}
</script>

<style scoped>
.pay-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #ffffff 100%);
  padding: 20px;
}

.pay-card {
  width: 100%;
  max-width: 900px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08);
}

:deep(.el-card__header) {
  padding: 24px 32px;
  font-size: 1.5rem;
}

:deep(.el-card__body) {
  padding: 32px;
}

.card-header {
  font-weight: 600;
  color: #1e293b;
}

.order-info {
  margin-bottom: 40px;
  padding: 30px;
  background: #f8fafc;
  border-radius: 12px;
}

.car-info {
  display: flex;
  gap: 24px;
  align-items: center;
  margin-bottom: 24px;
}

.car-cover {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.car-details {
  flex: 1;
}

.car-name {
  margin: 0 0 12px;
  font-size: 1.4rem;
  font-weight: 600;
  color: #1e293b;
}

.car-merchant {
  margin: 0;
  font-size: 1rem;
  color: #64748b;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  font-size: 1.1rem;
}

.info-item .label {
  font-weight: 600;
  color: #64748b;
  font-size: 1.1rem;
}

.info-item .value {
  color: #1e293b;
  font-size: 1.1rem;
}

.info-item .amount {
  font-size: 2rem;
  color: #ef4444;
  font-weight: 700;
}

.payment-methods {
  margin-bottom: 40px;
}

.payment-methods h3 {
  margin-bottom: 20px;
  color: #1e293b;
  font-size: 1.3rem;
  font-weight: 600;
}

.method-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.method-option {
  padding: 20px 24px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-option:hover {
  border-color: #3b82f6;
  background: #f0f9ff;
}

.method-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.1rem;
}

.pay-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-top: 20px;
}

.pay-actions :deep(.el-button) {
  padding: 12px 32px;
  font-size: 1.1rem;
  height: 48px;
}

@media (max-width: 480px) {
  .pay-container {
    padding: 12px;
  }

  .pay-card {
    border-radius: 8px;
  }

  .pay-actions {
    flex-direction: column;
  }

  .pay-actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
