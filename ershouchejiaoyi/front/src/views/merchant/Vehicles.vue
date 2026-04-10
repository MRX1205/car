<template>
  <div class="vehicles-container">
    <div class="vehicles-header">
      <h1>车辆管理</h1>
      <el-button type="primary" @click="goToAddVehicle">+ 新增车辆</el-button>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filter">
      <el-input
        v-model="searchForm.keyword"
        placeholder="搜索车辆名称..."
        clearable
        @input="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="searchForm.status"
        placeholder="选择状态"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="在售" value="1" />
        <el-option label="已下架" value="2" />
      </el-select>
    </div>

    <!-- 车辆列表 -->
    <div class="vehicles-list" v-loading="loading">
      <el-table :data="vehicles" stripe class="vehicles-table">
        <el-table-column prop="cheliangPhoto" label="图片" width="100">
          <template #default="{ row }">
            <img :src="row.cheliangPhoto" :alt="row.cheliangName" class="vehicle-image" />
          </template>
        </el-table-column>

        <el-table-column prop="cheliangName" label="车辆名称" width="150" />

        <el-table-column prop="cheliangNewMoney" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.cheliangNewMoney }} 万
          </template>
        </el-table-column>

        <el-table-column prop="cheliangClicknum" label="浏览次数" width="100" />

        <el-table-column prop="zanNumber" label="点赞" width="80" />

        <el-table-column prop="shangxiaTypes" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.shangxiaTypes === 1 ? 'success' : 'danger'">
              {{ row.shangxiaTypes === 1 ? '在售' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" plain round size="small" @click="editVehicle(row.id)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button
              :type="row.shangxiaTypes === 1 ? 'warning' : 'success'"
              plain
              round
              size="small"
              @click="togglePublish(row.id, row.shangxiaTypes)"
            >
              <el-icon><component :is="row.shangxiaTypes === 1 ? Bottom : Top" /></el-icon>
              {{ row.shangxiaTypes === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" plain round size="small" @click="deleteVehicle(row.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="vehicles.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @change="loadVehicles"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Edit, Delete, Top, Bottom } from '@element-plus/icons-vue'
import * as cheliangApi from '../../api/cheliang'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const vehicles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  keyword: '',
  status: ''
})

const loadVehicles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value
    }

    // 只添加有值的参数
    if (searchForm.value.keyword) {
      params.cheliangName = searchForm.value.keyword
    }
    if (searchForm.value.status) {
      params.shangxiaTypes = searchForm.value.status
    }

    const res = await cheliangApi.getCheliangPage(params)
    if (res.code === 0 || res.code === 200) {
      // 后端分页数据结构: res.data = { list: [], total, pageSize, currPage }
      const pageData = res.data || {}
      vehicles.value = pageData.list || []
      total.value = pageData.total || 0
    } else {
      console.error('加载车辆列表失败:', res)
      ElMessage.error(res.msg || '加载车辆列表失败')
    }
  } catch (error) {
    console.error('加载车辆列表失败:', error)
    ElMessage.error('加载车辆列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadVehicles()
}

const goToAddVehicle = () => {
  router.push('/merchant/vehicles/add')
}

const editVehicle = (vehicleId) => {
  router.push(`/merchant/vehicles/edit/${vehicleId}`)
}

const togglePublish = async (vehicleId, currentStatus) => {
  const action = currentStatus === 1 ? '下架' : '上架'
  ElMessageBox.confirm(
    `确定要${action}此车辆吗?`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      let res
      if (currentStatus === 1) {
        res = await cheliangApi.unpublishCheliang(vehicleId)
      } else {
        res = await cheliangApi.publishCheliang(vehicleId)
      }
      
      if (res.code === 0 || res.code === 200) {
        ElMessage.success(`已${action}`)
        loadVehicles()
      } else {
        ElMessage.error(res.msg || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

const deleteVehicle = async (vehicleId) => {
  ElMessageBox.confirm(
    '确定要删除此车辆吗? 此操作不可恢复',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error',
    }
  ).then(async () => {
    try {
      const res = await cheliangApi.deleteCheliang(vehicleId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('已删除')
        loadVehicles()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

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
.vehicles-container {
  padding: 30px;
}

.vehicles-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.vehicles-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.search-filter {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.filter-select {
  min-width: 150px;
}

.vehicles-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.vehicles-table {
  width: 100%;
}

.vehicle-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .vehicles-container {
    padding: 16px;
  }

  .vehicles-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .vehicles-header h1 {
    font-size: 1.5rem;
  }

  .search-filter {
    flex-direction: column;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }

  .vehicles-list {
    padding: 12px;
    overflow-x: auto;
  }
}
</style>
