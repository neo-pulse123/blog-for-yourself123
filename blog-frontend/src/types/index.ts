export interface User {
  id: number
  username: string
  nickname: string
  email: string
  avatar: string
  status: number
  createTime: string
  updateTime: string
}

export interface Note {
  id: number
  userId: number
  title: string
  content: string
  summary: string
  categoryId: number
  categoryName?: string
  viewCount: number
  likeCount: number
  status: number
  createTime: string
  updateTime: string
  user?: User
}

export interface Category {
  id: number
  name: string
  createTime: string
  updateTime: string
}

export interface Question {
  id: number
  userId: number
  title: string
  content: string
  viewCount: number
  answerCount: number
  status: number
  createTime: string
  updateTime: string
  user?: User
  answers?: Answer[]
}

export interface Answer {
  id: number
  questionId: number
  userId: number
  content: string
  likeCount: number
  isAccepted: boolean
  createTime: string
  updateTime: string
  user?: User
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  email: string
  nickname: string
}

export interface NoteForm {
  title: string
  content: string
  summary: string
  categoryId: number
}

export interface QuestionForm {
  title: string
  content: string
}

export interface AnswerForm {
  content: string
}

export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  user: {
    id: number
    username: string
    nickname: string
    email: string
    avatar: string | null
    role: string
  }
}
