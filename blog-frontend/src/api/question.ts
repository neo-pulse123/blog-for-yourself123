import { get, post, put, del } from '@/utils/request'
import type { Question, QuestionForm, Answer, AnswerForm, PageResult } from '@/types'

export const getQuestionList = (params: {
  page?: number
  pageSize?: number
  keyword?: string
}) => {
  return get<{ records: Question[], total: number }>('/questions', { params })
}

export const getQuestionById = (id: number) => {
  return get<Question>(`/questions/${id}`)
}

export const getMyQuestions = (params: {
  page?: number
  pageSize?: number
}) => {
  return get<{ records: Question[], total: number }>('/questions/my', { params })
}

export const createQuestion = (data: QuestionForm) => {
  return post<Question>('/questions', data)
}

export const updateQuestion = (id: number, data: QuestionForm) => {
  return put<Question>(`/questions/${id}`, data)
}

export const deleteQuestion = (id: number) => {
  return del<void>(`/questions/${id}`)
}

export const getAnswersByQuestionId = (questionId: number) => {
  return get<Answer[]>(`/answers/question/${questionId}`)
}

export const createAnswer = (questionId: number, data: AnswerForm) => {
  return post<Answer>(`/answers?questionId=${questionId}`, data)
}

export const deleteAnswer = (id: number) => {
  return del<void>(`/answers/${id}`)
}

export const likeAnswer = (id: number) => {
  return post<void>(`/answers/${id}/like`)
}

export const acceptAnswer = (id: number) => {
  return post<void>(`/answers/${id}/accept`)
}
