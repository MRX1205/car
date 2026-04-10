<template>
  <div class="success-container">
    <el-card class="success-card">
      <div class="success-content">
        <div class="success-icon">✓</div>
        <h2>支付成功</h2>
        <p class="order-id">订单号: {{ orderId }}</p>
        <p class="amount">支付金额: ¥{{ amount }}</p>

        <div class="actions">
          <el-button @click="$router.push('/user/orders')">查看订单</el-button>
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const orderId = ref(route.query.orderId || '')
const amount = ref(route.query.amount || '0')

onMounted(() => {
  // 可以在这里调用API确认支付状态
})
</script>

<style scoped>
.success-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e0f2fe 0%, #ffffff 100%);
  padding: 20px;
}

.success-card {
  width: 100%;
  max-width: 900px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08);
}

:deep(.el-card__body) {
  padding: 60px 40px;
}

.success-content {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  font-size: 120px;
  color: #10b981;
  margin-bottom: 30px;
  font-weight: bold;
  animation: successPulse 1s ease-in-out;
}

@keyframes successPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.success-content h2 {
  font-size: 2.5rem;
  color: #1e293b;
  margin-bottom: 24px;
  font-weight: 700;
}

.order-id,
.amount {
  color: #64748b;
  margin-bottom: 12px;
  font-size: 1.1rem;
}

.amount {
  font-size: 2rem;
  color: #ef4444;
  font-weight: 700;
  margin-top: 16px;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 50px;
}

.actions :deep(.el-button) {
  padding: 14px 40px;
  font-size: 1.1rem;
  height: 50px;
  border-radius: 25px;
}

@media (max-width: 480px) {
  .success-container {
    padding: 12px;
  }

  .success-icon {
    font-size: 60px;
  }

  .success-content h2 {
    font-size: 1.5rem;
  }

  .actions {
    flex-direction: column;
  }

  .actions :deep(.el-button) {
    width: 100%;
  }
}
</style>
