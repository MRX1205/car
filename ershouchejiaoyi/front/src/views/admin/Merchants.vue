<template>
  <div class="merchants-container">
    <div class="merchants-header">
      <h1>商家管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商家名称或联系方式"
          clearable
          style="width: 300px"
          @clear="loadMerchants"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>
    </div>

    <div class="merchants-content" v-loading="loading">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon" style="background: #eff6ff;">
            <el-icon color="#3b82f6" :size="28"><Shop /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ total }}</div>
            <div class="stat-label">商家总数</div>
          </div>
        </div>
      </div>

      <!-- 商家列表 -->
      <div class="table-container">
        <el-table :data="merchants" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="username" label="账号" width="120" />
          <el-table-column prop="shangjiaName" label="商家名称" width="180" show-overflow-tooltip />
          <el-table-column prop="shangjiaPhone" label="联系方式" width="130" />
          <el-table-column prop="shangjiaEmail" label="邮箱" width="180" show-overflow-tooltip />
          <el-table-column prop="shangjiaContent" label="商家简介" show-overflow-tooltip />
          <el-table-column prop="createTime" label="注册时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" text size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadMerchants"
            @current-change="loadMerchants"
          />
        </div>
      </div>
    </div>

    <!-- 编辑商家对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="editForm.id ? '编辑商家' : '新增商家'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="账号" prop="username">
          <el-input v-model="editForm.username" :disabled="!!editForm.id" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!editForm.id">
          <el-input v-model="editForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="商家名称" prop="shangjiaName">
          <el-input v-model="editForm.shangjiaName" placeholder="请输入商家名称" />
        </el-form-item>
        <el-form-item label="联系方式" prop="shangjiaPhone">
          <el-input v-model="editForm.shangjiaPhone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="邮箱" prop="shangjiaEmail">
          <el-input v-model="editForm.shangjiaEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="商家简介">
          <el-input
            v-model="editForm.shangjiaContent"
            type="textarea"
            :rows="4"
            placeholder="请输入商家简介"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Shop, Edit, Delete } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const merchants = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const editDialogVisible = ref(false)
const editFormRef = ref(null)
const submitLoading = ref(false)

const editForm = reactive({
  id: null,
  username: '',
  password: '',
  shangjiaName: '',
  shangjiaPhone: '',
  shangjiaEmail: '',
  shangjiaContent: ''
})

const formRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  shangjiaName: [
    { required: true, message: '请输入商家名称', trigger: 'blur' }
  ],
  shangjiaPhone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  shangjiaEmail: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 加载商家列表
const loadMerchants = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      limit: pageSize.value
    }

    if (searchKeyword.value) {
      params.shangjiaName = searchKeyword.value
    }

    const res = await request.get('/shangjia/page', { params })

    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      merchants.value = pageData.list || []
      total.value = pageData.total || 0
    } else {
      ElMessage.error(res.msg || '加载商家列表失败')
    }
  } catch (error) {
    console.error('加载商家列表失败:', error)
    ElMessage.error('加载商家列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  loadMerchants()
}

// 编辑
const handleEdit = (row) => {
  Object.assign(editForm, {
    id: row.id,
    username: row.username,
    password: '',
    shangjiaName: row.shangjiaName,
    shangjiaPhone: row.shangjiaPhone,
    shangjiaEmail: row.shangjiaEmail,
    shangjiaContent: row.shangjiaContent
  })
  editDialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除商家"${row.shangjiaName}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/shangjia/delete', [row.id])

      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        loadMerchants()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const url = editForm.id ? '/shangjia/update' : '/shangjia/save'
      const data = { ...editForm }

      // 如果是编辑且密码为空，则不传密码字段
      if (editForm.id && !editForm.password) {
        delete data.password
      }

      const res = await request.post(url, data)

      if (res.code === 0 || res.code === 200) {
        ElMessage.success(editForm.id ? '修改成功' : '新增成功')
        editDialogVisible.value = false
        loadMerchants()
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  Object.assign(editForm, {
    id: null,
    username: '',
    password: '',
    shangjiaName: '',
    shangjiaPhone: '',
    shangjiaEmail: '',
    shangjiaContent: ''
  })
  if (editFormRef.value) {
    editFormRef.value.clearValidate()
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadMerchants()
})
</script>

<style scoped>
.merchants-container {
  min-height: 100%;
}

.merchants-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.merchants-header h1 {
  font-size: 1.75rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.merchants-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #64748b;
}

.table-container {
  margin-top: 16px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
  margin-top: 16px;
}

/* Table 样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header) {
  font-weight: 600;
}

:deep(.el-table th.el-table__cell) {
  background: #f8fafc;
  color: #475569;
}

:deep(.el-table .el-table__cell) {
  padding: 14px 0;
}

/* 对话框样式优化 */
:deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
}

/* 响应式 */
@media (max-width: 768px) {
  .merchants-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
  }

  .header-actions :deep(.el-input) {
    width: 100% !important;
  }

  .merchants-content {
    padding: 16px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }
}
</style>
