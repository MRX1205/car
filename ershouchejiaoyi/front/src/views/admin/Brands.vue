<template>
  <div class="brands-container">
    <div class="brands-header">
      <h1>汽车品牌管理</h1>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增品牌
      </el-button>
    </div>

    <div class="brands-content" v-loading="loading">
      <div class="table-container">
        <el-table :data="brands" stripe style="width: 100%">
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="codeIndex" label="品牌编码" width="120" align="center" />
          <el-table-column prop="indexName" label="品牌名称" />
          <el-table-column prop="beizhu" label="备注" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleEdit(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog
      v-model="editDialogVisible"
      :title="editForm.id ? '编辑品牌' : '新增品牌'"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="品牌名称" prop="indexName">
          <el-input v-model="editForm.indexName" placeholder="请输入品牌名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.beizhu"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            maxlength="200"
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
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const brands = ref([])
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const submitLoading = ref(false)

const editForm = reactive({
  id: null,
  dicCode: 'cheliang_types',
  dicName: '汽车品牌',
  codeIndex: null,
  indexName: '',
  beizhu: ''
})

const formRules = {
  indexName: [
    { required: true, message: '请输入品牌名称', trigger: 'blur' },
    { min: 1, max: 50, message: '品牌名称长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

const loadBrands = async () => {
  loading.value = true
  try {
    const res = await request.get('/dictionary/page', {
      params: {
        dicCode: 'cheliang_types',
        limit: 100
      }
    })

    if (res.code === 0 || res.code === 200) {
      const pageData = res.data || {}
      brands.value = pageData.list || []
    } else {
      ElMessage.error(res.msg || '加载品牌列表失败')
    }
  } catch (error) {
    console.error('加载品牌列表失败:', error)
    ElMessage.error('加载品牌列表失败')
  } finally {
    loading.value = false
  }
}

const getMaxCodeIndex = async () => {
  try {
    const res = await request.post('/dictionary/maxCodeIndex', {
      dicCode: 'cheliang_types'
    })
    if (res.code === 0 || res.code === 200) {
      return res.data?.maxCodeIndex || res.maxCodeIndex || 1
    }
    return 1
  } catch (error) {
    console.error('获取最大编码失败:', error)
    return 1
  }
}

const handleAdd = async () => {
  resetForm()
  const maxCode = await getMaxCodeIndex()
  editForm.codeIndex = maxCode
  editDialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(editForm, {
    id: row.id,
    dicCode: row.dicCode,
    dicName: row.dicName,
    codeIndex: row.codeIndex,
    indexName: row.indexName,
    beizhu: row.beizhu || ''
  })
  editDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除品牌"${row.indexName}"吗？此操作不可恢复！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await request.post('/dictionary/delete', [row.id])

      if (res.code === 0 || res.code === 200) {
        ElMessage.success('删除成功')
        loadBrands()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const url = editForm.id ? '/dictionary/update' : '/dictionary/save'
      const data = { ...editForm }
      delete data.createTime

      const res = await request.post(url, data)

      if (res.code === 0 || res.code === 200) {
        ElMessage.success(editForm.id ? '修改成功' : '新增成功')
        editDialogVisible.value = false
        loadBrands()
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

const resetForm = () => {
  Object.assign(editForm, {
    id: null,
    dicCode: 'cheliang_types',
    dicName: '汽车品牌',
    codeIndex: null,
    indexName: '',
    beizhu: ''
  })
  if (editFormRef.value) {
    editFormRef.value.clearValidate()
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  loadBrands()
})
</script>

<style scoped>
.brands-container {
  min-height: 100%;
}

.brands-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.brands-header h1 {
  font-size: 1.75rem;
  color: #1e293b;
  margin: 0;
  font-weight: 700;
}

.brands-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.table-container {
  margin-top: 16px;
}

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
</style>
