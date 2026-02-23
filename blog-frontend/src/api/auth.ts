import { post } from '@/utils/request'
import type { LoginForm, RegisterForm, AuthResponse } from '@/types'

export const login = (data: LoginForm) => {
  return post<AuthResponse>('/auth/login', data)
}

export const register = (data: RegisterForm) => {
  return post<AuthResponse>('/auth/register', data)
}

export const getCurrentUser = () => {
  return post<AuthResponse.UserInfo>('/auth/me')
}
