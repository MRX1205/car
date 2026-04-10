<template>
  <div class="news-container">
    <div class="news-header">
      <h1>公告管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索公告标题"
          clearable
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button type="success" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>
    </div>

    <div class="news-content">
      <el-table :data="newsList" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="公告图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.newsPhoto"
              :src="getImageUrl(row.newsPhoto)"
              :preview-src-list="[getImageUrl(row.newsPhoto)]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 6px;"
            />
            <span v-else style="color: #94a3b8;">无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="newsName" label="公告标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="newsTypes" label="公告类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag>{{ getNewsTypeName(row.newsTypes) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="newsContent" label="公告内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ stripHtml(row.newsContent) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="insertTime" label="发布时间" width="180" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="warning" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getNewsList"
          @current-change="getNewsList"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="newsName">
          <el-input v-model="form.newsName" placeholder="请输入公告标题" maxlength="100" />
        </el-form-item>
        <el-form-item label="公告类型" prop="newsTypes">
          <el-select v-model="form.newsTypes" placeholder="请选择公告类型" style="width: 100%">
            <el-option
              v-for="item in newsTypes"
              :key="item.codeIndex"
              :label="item.indexName"
              :value="item.codeIndex"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="公告图片">
          <el-upload
            class="uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            accept="image/*"
          >
            <img v-if="form.newsPhoto" :src="getImageUrl(form.newsPhoto)" class="preview-img" />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="公告内容" prop="newsContent">
          <el-input
            v-model="form.newsContent"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="公告详情" width="600px">
      <div class="view-content" v-if="currentNews">
        <div class="view-item">
          <span class="label">公告标题：</span>
          <span>{{ currentNews.newsName }}</span>
        </div>
        <div class="view-item">
          <span class="label">公告类型：</span>
          <el-tag>{{ getNewsTypeName(currentNews.newsTypes) }}</el-tag>
        </div>
        <div class="view-item" v-if="currentNews.newsPhoto">
          <span class="label">公告图片：</span>
          <el-image
            :src="getImageUrl(currentNews.newsPhoto)"
            :preview-src-list="[getImageUrl(currentNews.newsPhoto)]"
            fit="cover"
            style="width: 200px; height: 120px; border-radius: 8px;"
          />
        </div>
        <div class="view-item">
          <span class="label">公告内容：</span>
          <div class="content-box">{{ currentNews.newsContent }}</div>
        </div>
        <div class="view-item">
          <span class="label">发布时间：</span>
          <span>{{ currentNews.insertTime }}</span>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import request from '../../utils/request'

const loading = ref(false)
const submitLoading = ref(false)
const newsList = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)
const searchKeyword = ref('')

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentNews = ref(null)
const formRef = ref(null)
const newsTypes = ref([])

const uploadUrl = '/ershouchejiaoyi/file/upload'

const form = reactive({
  id: null,
  newsName: '',
  newsTypes: null,
  newsPhoto: '',
  newsContent: ''
})

const rules = {
  newsName: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  newsTypes: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  newsContent: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const dialogTitle = computed(() => form.id ? '编辑公告' : '发布公告')

const getImageUrl = (photo) => {
  if (!photo) return ''
  if (photo.startsWith('http')) return photo
  if (photo.startsWith('/')) return photo
  return `/ershouchejiaoyi/upload/${photo}`
}

const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').substring(0, 50) + (html.length > 50 ? '...' : '')
}

const getNewsTypeName = (type) => {
  const item = newsTypes.value.find(t => t.codeIndex === type)
  return item ? item.indexName : '未知'
}

const getNewsTypes = async () => {
  try {
    const res = await request.get('/dictionary/page', {
      params: { dicCode: 'news_types', limit: 100 }
    })
    if (res.code === 0) {
      newsTypes.value = res.data?.list || []
    }
  } catch (error) {
    console.error('获取公告类型失败:', error)
  }
}

const getNewsList = async () => {
  loading.value = true
  try {
    const params = { page: page.value, limit: limit.value }
    if (searchKeyword.value) {
      params.newsName = searchKeyword.value
    }
    const res = await request.get('/news/page', { params })
    if (res.code === 0) {
      newsList.value = res.data?.list || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  getNewsList()
}

const resetForm = () => {
  form.id = null
  form.newsName = ''
  form.newsTypes = null
  form.newsPhoto = ''
  form.newsContent = ''
  formRef.value?.clearValidate()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  form.id = row.id
  form.newsName = row.newsName
  form.newsTypes = row.newsTypes
  form.newsPhoto = row.newsPhoto
  form.newsContent = row.newsContent
  dialogVisible.value = true
}

const handleView = (row) => {
  currentNews.value = row
  viewDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除公告"${row.newsName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.post('/news/delete', [row.id])
      if (res.code === 0) {
        ElMessage.success('删除成功')
        getNewsList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response) => {
  if (response.code === 0) {
    form.newsPhoto = response.file || response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const url = form.id ? '/news/update' : '/news/save'
      const data = {
        id: form.id,
        newsName: form.newsName,
        newsTypes: form.newsTypes,
        newsPhoto: form.newsPhoto,
        newsContent: form.newsContent
      }
      const res = await request.post(url, data)
      if (res.code === 0) {
        ElMessage.success(form.id ? '修改成功' : '发布成功')
        dialogVisible.value = false
        getNewsList()
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  getNewsTypes()
  getNewsList()
})
</script>

<style scoped>
.news-container {
  padding: 20px;
}

.news-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.news-header h1 {
  font-size: 1.5rem;
  color: #1e293b;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.news-content {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.uploader:hover {
  border-color: #409eff;
}

.preview-img {
  width: 200px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}

.uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.view-content {
  padding: 10px 0;
}

.view-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.view-item .label {
  font-weight: 600;
  color: #64748b;
  min-width: 80px;
}

.content-box {
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
  white-space: pre-wrap;
  word-break: break-word;
  flex: 1;
}
</style>
