<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>仪表板</h1>
      <p>欢迎回来，{{ merchantInfo.shangjiaName || '商家用户' }}</p>
    </div>

    <div class="stats-grid" v-loading="loading">
      <!-- 车辆统计 -->
      <div class="stat-card">
        <div class="stat-icon vehicles">
          <el-icon><Van /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.vehicleCount }}</div>
          <div class="stat-label">发布车辆</div>
        </div>
      </div>

      <!-- 订单统计 -->
      <div class="stat-card">
        <div class="stat-icon orders">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.orderCount }}</div>
          <div class="stat-label">订单总数</div>
        </div>
      </div>

      <!-- 待处理订单 -->
      <div class="stat-card">
        <div class="stat-icon pending">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pendingCount }}</div>
          <div class="stat-label">待处理订单</div>
        </div>
      </div>

      <!-- 已完成订单 -->
      <div class="stat-card">
        <div class="stat-icon completed">
          <el-icon><SuccessFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.completedCount }}</div>
          <div class="stat-label">已完成订单</div>
        </div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <h2>快速操作</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="$router.push('/merchant/vehicles/add')">
          <el-icon><Plus /></el-icon>
          发布新车辆
        </el-button>
        <el-button @click="$router.push('/merchant/vehicles')">
          <el-icon><Van /></el-icon>
          管理车辆
        </el-button>
        <el-button @click="$router.push('/merchant/orders')">
          <el-icon><ShoppingCart /></el-icon>
          查看订单
        </el-button>
        <el-button @click="$router.push('/merchant/forum')">
          <el-icon><ChatDotRound /></el-icon>
          论坛管理
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Van, ShoppingCart, Clock, SuccessFilled, Plus, ChatDotRound } from '@element-plus/icons-vue'
import * as cheliangApi from '../../api/cheliang'
import * as orderApi from '../../api/order'

const loading = ref(false)
const merchantInfo = reactive({
  shangjiaName: localStorage.getItem('username') || '商家用户'
})

const stats = reactive({
  vehicleCount: 0,
  orderCount: 0,
  pendingCount: 0,
  completedCount: 0
})

const loadStats = async () => {
  loading.value = true
  try {
    // 加载车辆统计
    const vehiclesRes = await cheliangApi.getCheliangPage({ page: 1, limit: 1 })
    if (vehiclesRes.code === 0 || vehiclesRes.code === 200) {
      const pageData = vehiclesRes.data || {}
      stats.vehicleCount = pageData.total || 0
    }

    // 加载订单统计
    const ordersRes = await orderApi.getOrderList({ page: 1, limit: 1 })
    if (ordersRes.code === 0 || ordersRes.code === 200) {
      const pageData = ordersRes.data || {}
      stats.orderCount = pageData.total || 0
    }

    // 加载待处理订单（状态为 2）
    const pendingRes = await orderApi.getOrderList({ page: 1, limit: 1, cheliangOrderStatusTypes: 2 })
    if (pendingRes.code === 0 || pendingRes.code === 200) {
      const pageData = pendingRes.data || {}
      stats.pendingCount = pageData.total || 0
    }

    // 加载已完成订单（状态为 4）
    const completedRes = await orderApi.getOrderList({ page: 1, limit: 1, cheliangOrderStatusTypes: 4 })
    if (completedRes.code === 0 || completedRes.code === 200) {
      const pageData = completedRes.data || {}
      stats.completedCount = pageData.total || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 30px;
}

.dashboard-header {
  margin-bottom: 40px;
}

.dashboard-header h1 {
  font-size: 2.5rem;
  color: #1e293b;
  margin: 0 0 10px;
  font-weight: 700;
}

.dashboard-header p {
  font-size: 1.1rem;
  color: #64748b;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-icon.vehicles {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.stat-icon.orders {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 0.95rem;
  color: #64748b;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.quick-actions h2 {
  font-size: 1.3rem;
  color: #1e293b;
  margin: 0 0 20px;
  font-weight: 600;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
}

.action-buttons :deep(.el-button) {
  height: 40px;
  font-size: 0.95rem;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .dashboard-header h1 {
    font-size: 1.8rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-card {
    padding: 16px;
    gap: 12px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .action-buttons {
    grid-template-columns: 1fr;
  }
}
</style>
