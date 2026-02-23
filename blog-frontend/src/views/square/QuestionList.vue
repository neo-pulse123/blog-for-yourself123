<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getQuestionList } from '@/api/question'
import type { Question } from '@/types'

const router = useRouter()
const loading = ref(false)
const questions = ref<Question[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})
const keyword = ref('')

const loadQuestions = async () => {
  loading.value = true
  try {
    const res = await getQuestionList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: keyword.value || undefined
    })
    questions.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadQuestions()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadQuestions()
}

const goToDetail = (id: number) => {
  router.push(`/square/question/${id}`)
}

onMounted(() => {
  loadQuestions()
})
</script>

<template>
  <div class="question-list">
    <div class="container">
      <div class="header">
        <h1>公共广场</h1>
        <el-button type="primary" @click="router.push('/square/ask')">我要提问</el-button>
      </div>
      
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索问题..."
          clearable
          @change="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div v-loading="loading" class="question-items">
        <div
          v-for="question in questions"
          :key="question.id"
          class="question-item"
          @click="goToDetail(question.id)"
        >
          <div class="question-stats">
            <div class="stat-item">
              <span class="stat-value">{{ question.answerCount }}</span>
              <span class="stat-label">回答</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ question.viewCount }}</span>
              <span class="stat-label">浏览</span>
            </div>
          </div>
          <div class="question-content">
            <h3 class="question-title">{{ question.title }}</h3>
            <div class="question-meta">
              <span class="author">{{ question.user?.nickname || question.user?.username }}</span>
              <span class="date">{{ question.createTime }}</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && questions.length === 0" description="暂无问题" />

      <div class="pagination" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          :page-size="pagination.pageSize"
          :total="pagination.total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.question-list {
  padding: 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.search-bar {
  margin-bottom: 20px;
}

.search-bar .el-input {
  max-width: 400px;
}

.question-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.question-item {
  display: flex;
  background: white;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.question-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.question-stats {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 80px;
  margin-right: 20px;
  text-align: center;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.question-content {
  flex: 1;
}

.question-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px;
}

.question-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.question-meta .author {
  color: #409eff;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
