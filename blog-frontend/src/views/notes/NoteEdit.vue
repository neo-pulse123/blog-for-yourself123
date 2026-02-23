<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNoteById, getCategories, createNote, updateNote } from '@/api/note'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { Category } from '@/types'

const route = useRoute()
const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const categories = ref<Category[]>([])

const isEdit = computed(() => !!route.params.id)
const noteId = computed(() => Number(route.params.id))

const form = reactive({
  title: '',
  content: '',
  summary: '',
  categoryId: undefined as number | undefined
})

const rules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过200', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' }
  ],
  summary: [
    { max: 500, message: '摘要长度不能超过500', trigger: 'blur' }
  ]
})

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res
}

const loadNote = async () => {
  if (!isEdit.value) return
  loading.value = true
  try {
    const res = await getNoteById(noteId.value)
    form.title = res.title
    form.content = res.content
    form.summary = res.summary || ''
    form.categoryId = res.categoryId
  } finally {
    loading.value = false
  }
}

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isEdit.value) {
          await updateNote(noteId.value, form)
          ElMessage.success('更新成功')
        } else {
          await createNote(form)
          ElMessage.success('创建成功')
        }
        router.push('/notes')
      } catch (e: any) {
        ElMessage.error(e.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadCategories()
  loadNote()
})
</script>

<template>
  <div class="note-edit">
    <div class="container">
      <div class="header">
        <h1>{{ isEdit ? '编辑笔记' : '创建笔记' }}</h1>
        <div class="actions">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSubmit(formRef)">
            {{ isEdit ? '保存' : '发布' }}
          </el-button>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        v-loading="loading"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请输入笔记标题"
            size="large"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="摘要" prop="summary">
          <el-input
            v-model="form.summary"
            type="textarea"
            placeholder="请输入笔记摘要（选填）"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="请输入笔记内容（支持 Markdown）"
            :rows="20"
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.note-edit {
  padding: 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.actions {
  display: flex;
  gap: 10px;
}
</style>
