import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/notes'
  },
  {
    path: '/notes',
    name: 'Notes',
    component: () => import('@/views/notes/NoteList.vue'),
    meta: { title: '学习笔记' }
  },
  {
    path: '/notes/:id',
    name: 'NoteDetail',
    component: () => import('@/views/notes/NoteDetail.vue'),
    meta: { title: '笔记详情' }
  },
  {
    path: '/notes/create',
    name: 'NoteCreate',
    component: () => import('@/views/notes/NoteEdit.vue'),
    meta: { title: '创建笔记', requiresAuth: true }
  },
  {
    path: '/notes/edit/:id',
    name: 'NoteEdit',
    component: () => import('@/views/notes/NoteEdit.vue'),
    meta: { title: '编辑笔记', requiresAuth: true }
  },
  {
    path: '/square',
    name: 'Square',
    component: () => import('@/views/square/QuestionList.vue'),
    meta: { title: '公共广场' }
  },
  {
    path: '/square/question/:id',
    name: 'QuestionDetail',
    component: () => import('@/views/square/QuestionDetail.vue'),
    meta: { title: '问题详情' }
  },
  {
    path: '/square/ask',
    name: 'AskQuestion',
    component: () => import('@/views/square/QuestionEdit.vue'),
    meta: { title: '提问', requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
