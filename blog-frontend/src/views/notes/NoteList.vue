<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { getNoteList, getCategories } from '@/api/note'
import type { Note, Category } from '@/types'

const router = useRouter()
const loading = ref(false)
const notes = ref<Note[]>([])
const categories = ref<Category[]>([])
const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})
const filters = reactive({
  categoryId: undefined as number | undefined,
  keyword: ''
})

const loadNotes = async () => {
  loading.value = true
  try {
    const res = await getNoteList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      categoryId: filters.categoryId,
      keyword: filters.keyword
    })
    notes.value = res.records
    pagination.total = res.total
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res
}

const handleSearch = () => {
  pagination.page = 1
  loadNotes()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadNotes()
}

const goToDetail = (id: number) => {
  router.push(`/notes/${id}`)
}

onMounted(() => {
  loadNotes()
  loadCategories()
})
</script>

<template>
  <div class="note-list">
    <div class="container">
      <div class="header">
        <h1>学习笔记</h1>
        <el-button type="primary" @click="router.push('/notes/create')">发布笔记</el-button>
      </div>
      
      <div class="filters">
        <el-select
          v-model="filters.categoryId"
          placeholder="选择分类"
          clearable
          @change="handleSearch"
        >
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
        <el-input
          v-model="filters.keyword"
          placeholder="搜索笔记..."
          clearable
          @change="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div v-loading="loading" class="note-grid">
        <div
          v-for="note in notes"
          :key="note.id"
          class="note-card"
          @click="goToDetail(note.id)"
        >
          <div class="note-category" v-if="note.category">
            {{ note.category.name }}
          </div>
          <h3 class="note-title">{{ note.title }}</h3>
          <p class="note-summary">{{ note.summary || '暂无摘要' }}</p>
          <div class="note-meta">
            <span class="author">{{ note.user?.nickname || note.user?.username }}</span>
            <span class="views"><el-icon><View /></el-icon> {{ note.viewCount }}</span>
            <span class="likes"><el-icon><Star /></el-icon> {{ note.likeCount }}</span>
            <span class="date">{{ note.createTime }}</span>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && notes.length === 0" description="暂无笔记内容" />

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
.note-list {
  padding: 20px;
}

.container {
  max-width: 1200px;
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

.filters {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.filters .el-select {
  width: 150px;
}

.filters .el-input {
  width: 300px;
}

.note-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.note-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.note-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.note-category {
  display: inline-block;
  padding: 4px 12px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 12px;
  margin-bottom: 10px;
}

.note-title {
  font-size: 18px;
  color: #333;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.note-summary {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.note-meta {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.note-meta .author {
  color: #409eff;
}

.note-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
