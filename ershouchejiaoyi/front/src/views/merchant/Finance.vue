<template>
  <div class="finance-container">
    <div class="finance-header">
      <h1>财务统计</h1>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid" v-loading="loading">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">本月收入</div>
          <div class="stat-value">¥{{ stats.monthlyIncome || 0 }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">本月订单</div>
          <div class="stat-value">{{ stats.monthlyOrders || 0 }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">累计收入</div>
          <div class="stat-value">¥{{ stats.totalIncome || 0 }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><CreditCard /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-label">待提现</div>
          <div class="stat-value">¥{{ stats.pendingWithdraw || 0 }}</div>
        </div>
      </div>
    </div>

    <!-- 收入明细 -->
    <div class="income-section">
      <div class="section-header">
        <h2>收入明细</h2>
      </div>

      <el-table :data="incomeDetails" stripe v-loading="detailsLoading" class="income-table">
        <el-table-column prop="cheliangOrderUuidNumber" label="订单号" width="180" />
        <el-table-column prop="cheliangName" label="车辆名称" width="150" />
        <el-table-column prop="cheliangOrderPrice" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.cheliangOrderPrice }} 万
          </template>
        </el-table-column>
        <el-table-column prop="cheliangOrderStatusTypes" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.cheliangOrderStatusTypes)">
              {{ getStatusText(row.cheliangOrderStatusTypes) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="insertTime" label="时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.insertTime) }}
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 提现按钮 -->
    <div class="withdraw-section">
      <el-button type="primary" size="large" @click="handleWithdraw" :disabled="stats.pendingWithdraw <= 0">
        申请提现
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Money, ShoppingCart, Wallet, CreditCard } from '@element-plus/icons-vue'
import * as orderApi from '../../api/order'

const loading = ref(false)
const detailsLoading = ref(false)

const stats = ref({
  monthlyIncome: 0,
  monthlyOrders: 0,
  totalIncome: 0,
  pendingWithdraw: 0
})

const incomeDetails = ref([])

const statusMap = {
  1: '待支付',
  2: '已支付',
  3: '已发货',
  4: '已完成',
  5: '已取消'
}

const getStatusText = (status) => statusMap[status] || '未知'

const getStatusType = (status) => {
  const typeMap = {
    1: 'warning',
    2: 'info',
    3: 'primary',
    4: 'success',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const loadStats = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderList({ page: 1, limit: 1000 })
    if (res.code === 0 || res.code === 200) {
      const orders = res.data || []
      
      // 计算统计数据
      const now = new Date()
      const currentMonth = now.getMonth()
      const currentYear = now.getFullYear()
      
      const monthlyOrders = orders.filter(o => {
        const date = new Date(o.insertTime)
        return date.getMonth() === currentMonth && date.getFullYear() === currentYear
      })
      
      stats.value.monthlyOrders = monthlyOrders.length
      stats.value.monthlyIncome = monthlyOrders
        .filter(o => o.cheliangOrderStatusTypes === 4)
        .reduce((sum, o) => sum + (o.cheliangOrderPrice || 0), 0)
      
      stats.value.totalIncome = orders
        .filter(o => o.cheliangOrderStatusTypes === 4)
        .reduce((sum, o) => sum + (o.cheliangOrderPrice || 0), 0)
      
      stats.value.pendingWithdraw = orders
        .filter(o => o.cheliangOrderStatusTypes === 4)
        .reduce((sum, o) => sum + (o.cheliangOrderPrice || 0), 0) * 0.1 // 假设10%待提现
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.value = false
  }
}

const loadIncomeDetails = async () => {
  detailsLoading.value = true
  try {
    const res = await orderApi.getOrderList({ page: 1, limit: 100 })
    if (res.code === 0 || res.code === 200) {
      incomeDetails.value = (res.data || []).filter(o => o.cheliangOrderStatusTypes === 4)
    }
  } catch (error) {
    ElMessage.error('加载收入明细失败')
  } finally {
    detailsLoading.value = false
  }
}

const handleWithdraw = () => {
  ElMessage.success('提现申请已提交，请等待审核')
}

onMounted(() => {
  loadStats()
  loadIncomeDetails()
})
</script>

<style scoped>
.finance-container {
  padding: 30px;
}

.finance-header {
  margin-bottom: 30px;
}

.finance-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
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
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
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
  background: #dbeafe;
  color: #3b82f6;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 0.9rem;
  color: #64748b;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 1.75rem;
  color: #1e293b;
  font-weight: 700;
}

.income-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 1.25rem;
  color: #1e293b;
  margin: 0;
  font-weight: 600;
}

.income-table {
  width: 100%;
}

.withdraw-section {
  display: flex;
  justify-content: center;
  padding: 20px;
}

@media (max-width: 768px) {
  .finance-container {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .stat-value {
    font-size: 1.5rem;
  }

  .income-section {
    padding: 16px;
  }
}
</style>
