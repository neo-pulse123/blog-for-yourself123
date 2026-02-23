<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getQuestionById, getAnswersByQuestionId, createAnswer, likeAnswer, acceptAnswer, deleteQuestion } from '@/api/question'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Question, Answer } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const question = ref<Question | null>(null)
const answers = ref<Answer[]>([])
const loading = ref(false)
const answerLoading = ref(false)
const answerContent = ref('')

const questionId = computed(() => Number(route.params.id))

const isOwner = computed(() => {
  return userStore.userInfo?.id === question.value?.userId
})

const loadQuestion = async () => {
  loading.value = true
  try {
    const res = await getQuestionById(questionId.value)
    question.value = res
    answers.value = res.answers || []
  } finally {
    loading.value = false
  }
}

const handleSubmitAnswer = async () => {
  if (!answerContent.value.trim()) {
    ElMessage.warning('请输入回答内容')
    return
  }
  
  answerLoading.value = true
  try {
    await createAnswer(questionId.value, { content: answerContent.value })
    ElMessage.success('回答成功')
    answerContent.value = ''
    await loadQuestion()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    answerLoading.value = false
  }
}

const handleLikeAnswer = async (answerId: number) => {
  try {
    await likeAnswer(answerId)
    const answer = answers.value.find(a => a.id === answerId)
    if (answer) answer.likeCount++
    ElMessage.success('点赞成功')
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleAcceptAnswer = async (answerId: number) => {
  try {
    await acceptAnswer(answerId)
    ElMessage.success('采纳成功')
    await loadQuestion()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleDeleteQuestion = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这个问题吗？', '提示', { type: 'warning' })
    await deleteQuestion(questionId.value)
    ElMessage.success('删除成功')
    router.push('/square')
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

onMounted(() => {
  loadQuestion()
})
</script>

<template>
  <div class="question-detail" v-loading="loading">
    <div class="container" v-if="question">
      <div class="back">
        <el-button @click="router.back()">返回</el-button>
      </div>
      
      <div class="question-section">
        <div class="question-header">
          <h1 class="title">{{ question.title }}</h1>
          <div class="meta">
            <div class="author">
              <el-avatar :size="32" :src="question.user?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
              <span>{{ question.user?.nickname || question.user?.username }}</span>
            </div>
            <div class="stats">
              <span>{{ question.createTime }}</span>
              <span><el-icon><View /></el-icon> {{ question.viewCount }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ question.answerCount }}</span>
            </div>
          </div>
          <div class="actions" v-if="isOwner">
            <el-button type="danger" @click="handleDeleteQuestion">删除问题</el-button>
          </div>
        </div>
        
        <div class="question-content" v-html="question.content"></div>
      </div>
      
      <div class="answers-section">
        <h2 class="answers-title">回答 ({{ answers.length }})</h2>
        
        <div class="answer-list">
          <div
            v-for="answer in answers"
            :key="answer.id"
            class="answer-item"
            :class="{ accepted: answer.isAccepted }"
          >
            <div class="answer-accepted" v-if="answer.isAccepted">
              <el-icon><Check /></el-icon> 已采纳
            </div>
            <div class="answer-content" v-html="answer.content"></div>
            <div class="answer-footer">
              <div class="answer-author">
                <el-avatar :size="24" :src="answer.user?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
                <span>{{ answer.user?.nickname || answer.user?.username }}</span>
                <span class="date">{{ answer.createTime }}</span>
              </div>
              <div class="answer-actions">
                <el-button type="text" @click="handleLikeAnswer(answer.id)">
                  <el-icon><Star /></el-icon> {{ answer.likeCount }}
                </el-button>
                <el-button
                  type="text"
                  v-if="isOwner && !answer.isAccepted"
                  @click="handleAcceptAnswer(answer.id)"
                >
                  采纳
                </el-button>
              </div>
            </div>
          </div>
        </div>
        
        <el-empty v-if="answers.length === 0" description="暂无回答" />
        
        <div class="answer-form" v-if="userStore.isLoggedIn()">
          <h3>写下你的回答</h3>
          <el-input
            v-model="answerContent"
            type="textarea"
            placeholder="请输入回答内容..."
            :rows="5"
          />
          <el-button
            type="primary"
            :loading="answerLoading"
            @click="handleSubmitAnswer"
          >
            提交回答
          </el-button>
        </div>
        <div v-else class="login-tip">
          <el-button type="primary" @click="router.push('/login')">登录后回答</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.question-detail {
  padding: 20px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

.back {
  margin-bottom: 20px;
}

.question-section,
.answers-section {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
}

.question-header {
  margin-bottom: 20px;
}

.title {
  font-size: 24px;
  color: #333;
  margin: 0 0 15px;
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
  gap: 15px;
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

.question-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
}

.answers-title {
  font-size: 20px;
  margin: 0 0 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.answer-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
}

.answer-item.accepted {
  border-color: #67c23a;
  background: #f6ffed;
}

.answer-accepted {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #67c23a;
  margin-bottom: 10px;
  font-weight: bold;
}

.answer-content {
  font-size: 15px;
  line-height: 1.8;
  color: #333;
}

.answer-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.answer-author {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.answer-author .date {
  color: #999;
}

.answer-actions {
  display: flex;
  gap: 10px;
}

.answer-form {
  margin-top: 30px;
}

.answer-form h3 {
  margin: 0 0 15px;
  font-size: 16px;
}

.answer-form .el-button {
  margin-top: 15px;
}

.login-tip {
  margin-top: 30px;
  text-align: center;
}
</style>
