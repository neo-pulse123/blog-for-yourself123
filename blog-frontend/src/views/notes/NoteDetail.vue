<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoteById, likeNote, deleteNote } from '@/api/note'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Note } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const note = ref<Note | null>(null)
const loading = ref(false)

const noteId = computed(() => Number(route.params.id))

const isOwner = computed(() => {
  return userStore.userInfo?.id === note.value?.userId
})

const loadNote = async () => {
  loading.value = true
  try {
    const res = await getNoteById(noteId.value)
    note.value = res
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  try {
    await likeNote(noteId.value)
    note.value!.likeCount++
    ElMessage.success('点赞成功')
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleEdit = () => {
  router.push(`/notes/edit/${noteId.value}`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇笔记吗？', '提示', {
      type: 'warning'
    })
    await deleteNote(noteId.value)
    ElMessage.success('删除成功')
    router.push('/notes')
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

onMounted(() => {
  loadNote()
})
</script>

<template>
  <div class="note-detail" v-loading="loading">
    <div class="container" v-if="note">
      <div class="back">
        <el-button @click="router.back()">返回</el-button>
      </div>
      
      <article class="note-content">
        <header class="note-header">
          <div class="note-category" v-if="note.category">
            {{ note.category.name }}
          </div>
          <h1 class="title">{{ note.title }}</h1>
          <div class="meta">
            <div class="author">
              <el-avatar :size="32" :src="note.user?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
              <span>{{ note.user?.nickname || note.user?.username }}</span>
            </div>
            <div class="stats">
              <span><el-icon><View /></el-icon> {{ note.viewCount }}</span>
              <span><el-icon><Star /></el-icon> {{ note.likeCount }}</span>
              <span>{{ note.createTime }}</span>
            </div>
          </div>
          <div class="actions" v-if="isOwner">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </header>
        
        <div class="content" v-html="note.content"></div>
        
        <footer class="note-footer">
          <el-button type="primary" @click="handleLike">
            <el-icon><Star /></el-icon> 点赞 ({{ note.likeCount }})
          </el-button>
        </footer>
      </article>
    </div>
  </div>
</template>

<style scoped>
.note-detail {
  padding: 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.back {
  margin-bottom: 20px;
}

.note-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.note-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.note-category {
  display: inline-block;
  padding: 4px 12px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  font-size: 14px;
  margin-bottom: 15px;
}

.title {
  font-size: 28px;
  color: #333;
  margin: 0 0 20px;
  line-height: 1.4;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author span {
  font-size: 14px;
  color: #333;
}

.stats {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.actions {
  margin-top: 15px;
}

.content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.content :deep(h1),
.content :deep(h2),
.content :deep(h3) {
  margin-top: 1.5em;
  margin-bottom: 0.5em;
}

.content :deep(p) {
  margin-bottom: 1em;
}

.content :deep(code) {
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.content :deep(pre) {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  overflow-x: auto;
}

.content :deep(img) {
  max-width: 100%;
}

.note-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  text-align: center;
}
</style>
