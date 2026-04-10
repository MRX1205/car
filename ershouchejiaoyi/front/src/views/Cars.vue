<template>
  <div class="cars-container">
    <div class="cars-header">
      <h1>二手车辆</h1>
      <p>精选优质二手车，透明价格，放心购买</p>
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
        v-model="searchForm.type"
        placeholder="选择品牌"
        clearable
        @change="handleSearch"
        class="filter-select"
      >
        <el-option
          v-for="item in cheliangTypes"
          :key="item.codeIndex"
          :label="item.indexName"
          :value="item.codeIndex"
        />
      </el-select>

      <el-select
        v-model="searchForm.sortBy"
        placeholder="排序方式"
        @change="handleSearch"
        class="filter-select"
      >
        <el-option label="最新发布" value="create_time" />
        <el-option label="价格最低" value="price_asc" />
        <el-option label="价格最高" value="price_desc" />
        <el-option label="热度最高" value="click_num" />
      </el-select>
    </div>

    <!-- 车辆列表 -->
    <div class="cars-grid" v-loading="loading">
      <div v-if="carsList.length === 0" class="empty-state">
        <el-empty description="暂无车辆信息" />
      </div>

      <div v-for="car in carsList" :key="car.id" class="car-card" @click="goToDetail(car.id)" style="cursor: pointer;">
        <div class="car-image">
          <img :src="car.cheliangPhoto" :alt="car.cheliangName" />
          <div class="car-badge">
            <span v-if="car.shangxiaTypes === 1" class="badge-on-sale">在售</span>
            <span v-else class="badge-off-sale">已下架</span>
          </div>
          <div class="car-actions">
            <el-button
              type="primary"
              text
              :icon="isCollected(car.id) ? StarFilled : Star"
              @click.stop="toggleCollection(car.id)"
              class="action-btn"
            />
          </div>
        </div>

        <div class="car-info">
          <h3 class="car-name">{{ car.cheliangName }}</h3>
          <p class="car-desc">{{ car.cheliangContent }}</p>

          <div class="car-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ car.cheliangClicknum || 0 }}
            </span>
            <span class="stat-item">
              <el-icon><StarFilled /></el-icon>
              {{ car.zanNumber || 0 }}
            </span>
          </div>

          <div class="car-footer">
            <div class="car-price">
              <span class="price-label">¥</span>
              <span class="price-value">{{ car.cheliangNewMoney }} 万</span>
            </div>
            <el-button type="primary" round class="view-detail-btn" @click.stop="goToDetail(car.id)">
              查看详情 <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="carsList.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[9, 18, 27]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, View, StarFilled, Star, ArrowRight } from '@element-plus/icons-vue'
import * as cheliangApi from '../api/cheliang'
import * as cartApi from '../api/cart'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const carsList = ref([])
const cheliangTypes = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)
const collectedIds = ref(new Set())

const searchForm = ref({
  keyword: '',
  type: null,
  sortBy: 'create_time'
})

const loadCars = async () => {
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
    if (searchForm.value.type) {
      params.cheliangTypes = searchForm.value.type
    }

    const res = await cheliangApi.getCheliangList(params)
    if (res.code === 0 || res.code === 200) {
      // 后端分页数据结构: res.data = { list: [], total, pageSize, currPage }
      const pageData = res.data || {}
      carsList.value = pageData.list || []
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

const loadCheliangTypes = async () => {
  try {
    const res = await cheliangApi.getCheliangTypes()
    if (res.code === 0 || res.code === 200) {
      cheliangTypes.value = res.data?.list || res.data || []
      if (cheliangTypes.value.length === 0) {
        console.warn('车辆类型列表为空')
      }
    } else {
      console.error('加载车辆品牌失败:', res)
    }
  } catch (error) {
    console.error('加载车辆品牌失败:', error)
    // 不显示错误提示，品牌加载失败不影响主功能
  }
}

const loadCollections = async () => {
  if (!authStore.isLoggedIn) return

  try {
    const res = await cartApi.getCollectionList({ page: 1, limit: 1000 })
    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      collectedIds.value = new Set((pageData.list || []).map(item => item.cheliangId))
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
  }
}

const isCollected = (carId) => {
  return collectedIds.value.has(carId)
}

const toggleCollection = async (carId) => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (isCollected(carId)) {
      // 移除收藏
      const collectionId = carsList.value.find(c => c.id === carId)?.collectionId
      if (collectionId) {
        await cartApi.removeCollection(collectionId)
        collectedIds.value.delete(carId)
        ElMessage.success('已取消收藏')
      }
    } else {
      // 添加收藏
      await cartApi.addCollection(carId)
      collectedIds.value.add(carId)
      ElMessage.success('已收藏')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goToDetail = (carId) => {
  router.push(`/cars/detail/${carId}`)
}

const handleSearch = () => {
  currentPage.value = 1
  loadCars()
}

const handlePageChange = () => {
  loadCars()
}

onMounted(() => {
  loadCheliangTypes()
  loadCars()
  loadCollections()
})
</script>

<style scoped>
.cars-container {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  padding: 40px 20px;
}

.cars-header {
  text-align: center;
  margin-bottom: 40px;
}

.cars-header h1 {
  font-size: 2.5rem;
  color: #1e293b;
  margin-bottom: 10px;
  font-weight: 700;
}

.cars-header p {
  font-size: 1.1rem;
  color: #64748b;
}

.search-filter {
  display: flex;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
}

.filter-select {
  min-width: 150px;
}

.cars-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.car-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.car-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.car-image {
  position: relative;
  width: 100%;
  aspect-ratio: 3 / 2;
  overflow: hidden;
  background: #f1f5f9;
}

.car-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.car-card:hover .car-image img {
  transform: scale(1.05);
}

.car-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
}

.badge-on-sale,
.badge-off-sale {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.badge-on-sale {
  background: #10b981;
  color: white;
}

.badge-off-sale {
  background: #ef4444;
  color: white;
}

.car-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.9);
  color: #ef4444;
  font-size: 20px;
}

.car-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.car-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.car-desc {
  font-size: 0.9rem;
  color: #64748b;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.car-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
  color: #94a3b8;
}

.car-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.car-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-label {
  font-size: 0.9rem;
  color: #ef4444;
  font-weight: 600;
}

.price-value {
  font-size: 1.5rem;
  color: #ef4444;
  font-weight: 700;
}

.empty-state {
  grid-column: 1 / -1;
  padding: 60px 20px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .cars-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }

  .search-filter {
    flex-direction: column;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }

  .cars-header h1 {
    font-size: 1.8rem;
  }
}

@media (max-width: 480px) {
  .cars-container {
    padding: 20px 12px;
  }

  .cars-grid {
    grid-template-columns: 1fr;
  }

  .cars-header h1 {
    font-size: 1.5rem;
  }

  .cars-header p {
    font-size: 0.95rem;
  }
}
</style>
