<script lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { login } from '@/api/auth'

interface LoginForm {
  username: string
  password: string
}

export default defineComponent({
  name: 'Login',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const formRef = ref<FormInstance>()
    const loading = ref(false)

    const form = reactive<LoginForm>({
      username: '',
      password: ''
    })

    const rules = reactive<FormRules<LoginForm>>({
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
      ]
    })

    const handleLogin = async (formEl: FormInstance | undefined) => {
      if (!formEl) return
      
      await formEl.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            const res = await login(form)
            localStorage.setItem('token', res.token)
            localStorage.setItem('userInfo', JSON.stringify(res.user))
            ElMessage.success('登录成功')
            
            const redirect = route.query.redirect as string
            router.push(redirect || '/')
          } catch (error: any) {
            ElMessage.error(error.message || '登录失败')
          } finally {
            loading.value = false
          }
        }
      })
    }

    return {
      form,
      rules,
      formRef,
      loading,
      handleLogin
    }
  }
})
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">博客问答平台</h2>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        class="login-form"
        @submit.prevent="handleLogin(formRef)"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin(formRef)"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin(formRef)"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}

.login-form {
  margin-top: 20px;
}

.login-button {
  width: 100%;
}

.footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.footer a {
  color: #409eff;
  margin-left: 5px;
}
</style>
