import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    if (res.code === 200 || res.code === undefined) {
      return res
    } else if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      router.push({ name: 'Login' })
      return Promise.reject(new Error(res.message || '未授权'))
    } else {
      ElMessage.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      router.push({ name: 'Login' })
    } else {
      ElMessage.error(error.message || '网络请求失败')
    }
    return Promise.reject(error)
  }
)

export default service

export function get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return service.get(url, config).then((res) => res.data)
}

export function post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return service.post(url, data, config).then((res) => res.data)
}

export function put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
  return service.put(url, data, config).then((res) => res.data)
}

export function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return service.delete(url, config).then((res) => res.data)
}
