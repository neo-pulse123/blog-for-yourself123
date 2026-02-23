<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createQuestion } from '@/api/question'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  title: '',
  content: ''
})

const rules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { max: 200, message: '标题长度不能超过200', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' }
  ]
})

const handleSubmit = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  
  await formEl.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await createQuestion(form)
        ElMessage.success('发布成功')
        router.push('/square')
      } catch (e: any) {
        ElMessage.error(e.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<template>
  <div class="question-edit">
    <div class="container">
      <div class="header">
        <h1>提问</h1>
        <div class="actions">
          <el-button @click="router.back()">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleSubmit(formRef)">发布</el-button>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="问题标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="请简要描述你的问题"
            size="large"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="问题详情" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            placeholder="请详细描述你的问题，包括你尝试过的方法..."
            :rows="15"
          />
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.question-edit {
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
