<template>
  <div class="collections-container">
    <div class="collections-header">
      <h1>我的收藏</h1>
    </div>

    <div class="collections-content" v-loading="loading">
      <div v-if="collections.length === 0" class="empty-collections">
        <el-empty description="暂无收藏">
          <template #default>
            <el-button type="primary" @click="$router.push('/cars')">去浏览车辆</el-button>
          </template>
        </el-empty>
      </div>

      <div v-else>
        <!-- 收藏列表 -->
        <div class="collections-grid">
          <div class="collection-card" v-for="item in collections" :key="item.id">
            <div class="card-image">
              <img :src="item.cheliangPhoto" :alt="item.cheliangName" />
              <div class="card-badge">
                <span v-if="item.shangxiaTypes === 1" class="badge-on-sale">在售</span>
                <span v-else class="badge-off-sale">已下架</span>
              </div>
              <div class="card-actions">
                <el-button
                  type="danger"
                  text
                  icon="Delete"
                  @click="removeCollection(item.id)"
                  class="action-btn"
                />
              </div>
            </div>

            <div class="card-info">
              <h3 class="card-name">{{ item.cheliangName }}</h3>
              <p class="card-desc">{{ item.cheliangContent }}</p>

              <div class="card-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ item.cheliangClicknum || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><StarFilled /></el-icon>
                  {{ item.zanNumber || 0 }}
                </span>
              </div>

              <div class="card-footer">
                <div class="card-price">
                  <span class="price-label">¥</span>
                  <span class="price-value">{{ item.cheliangNewMoney }} 万</span>
                </div>
                <el-button type="primary" @click="goToDetail(item.cheliangId)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="collections.length > 0">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @change="loadCollections"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { View, StarFilled } from '@element-plus/icons-vue'
import * as cartApi from '../../api/cart'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(false)
const collections = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadCollections = async () => {
  loading.value = true
  try {
    const res = await cartApi.getCollectionList({
      page: currentPage.value,
      limit: pageSize.value
    })
    if (res.code === 0 || res.code === 200) {
      // 后端分页数据结构: res.data = { list: [], total, pageSize, currPage }
      const pageData = res.data || {}
      collections.value = pageData.list || []
      total.value = pageData.total || 0
    } else {
      ElMessage.error(res.msg || '加载收藏失败')
    }
  } catch (error) {
    console.error('加载收藏失败:', error)
    ElMessage.error('加载收藏失败')
  } finally {
    loading.value = false
  }
}

const removeCollection = async (collectionId) => {
  ElMessageBox.confirm(
    '确定要移除此收藏吗?',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await cartApi.removeCollection(collectionId)
      if (res.code === 0 || res.code === 200) {
        ElMessage.success('已移除收藏')
        loadCollections()
      } else {
        ElMessage.error(res.msg || '移除失败')
      }
    } catch (error) {
      console.error('移除失败:', error)
      ElMessage.error('移除失败')
    }
  }).catch(() => {})
}

const goToDetail = (carId) => {
  router.push(`/cars/detail/${carId}`)
}

onMounted(() => {
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadCollections()
})
</script>

<style scoped>
.collections-container {
  max-width: 1600px;
  width: 96%;
  margin: 0 auto;
  padding: 40px 20px;
}

.collections-header {
  margin-bottom: 30px;
}

.collections-header h1 {
  font-size: 2rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.collections-content {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-collections {
  padding: 60px 20px;
  text-align: center;
}

.collections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.collection-card {
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
}

.collection-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-image {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f1f5f9;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.collection-card:hover .card-image img {
  transform: scale(1.05);
}

.card-badge {
  position: absolute;
  top: 8px;
  left: 8px;
}

.badge-on-sale,
.badge-off-sale {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 16px;
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
}

.badge-on-sale {
  background: #10b981;
}

.badge-off-sale {
  background: #ef4444;
}

.card-actions {
  position: absolute;
  top: 8px;
  right: 8px;
}

.action-btn {
  background: rgba(255, 255, 255, 0.9);
  color: #ef4444;
  font-size: 18px;
}

.card-info {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-name {
  font-size: 1rem;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 6px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 0.8rem;
  color: #64748b;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 0.75rem;
  color: #94a3b8;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  gap: 8px;
}

.card-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-label {
  font-size: 0.8rem;
  color: #ef4444;
  font-weight: 600;
}

.price-value {
  font-size: 1.25rem;
  color: #ef4444;
  font-weight: 700;
}

.card-footer :deep(.el-button) {
  flex: 1;
  padding: 0;
  height: 32px;
  font-size: 0.9rem;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .collections-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 16px;
  }

  .card-image {
    height: 140px;
  }
}

@media (max-width: 480px) {
  .collections-container {
    padding: 20px 12px;
  }

  .collections-content {
    padding: 16px;
  }

  .collections-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .card-image {
    height: 120px;
  }

  .card-name {
    font-size: 0.9rem;
  }

  .price-value {
    font-size: 1.1rem;
  }
}
</style>
