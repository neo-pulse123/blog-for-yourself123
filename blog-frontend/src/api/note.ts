import { get, post, put, del } from '@/utils/request'
import type { Note, NoteForm, Category, PageResult } from '@/types'

interface NotePageResult {
  records: Note[]
  total: number
  pages: number
  current: number
  size: number
}

export const getNoteList = (params: {
  page?: number
  pageSize?: number
  categoryId?: number
  keyword?: string
}) => {
  return get<NotePageResult>('/notes', { params })
}

export const getNoteById = (id: number) => {
  return get<Note>(`/notes/${id}`)
}

export const getMyNotes = (params: {
  page?: number
  pageSize?: number
}) => {
  return get<NotePageResult>('/notes/my', { params })
}

export const createNote = (data: NoteForm) => {
  return post<Note>('/notes', data)
}

export const updateNote = (id: number, data: NoteForm) => {
  return put<Note>(`/notes/${id}`, data)
}

export const deleteNote = (id: number) => {
  return del<void>(`/notes/${id}`)
}

export const likeNote = (id: number) => {
  return post<void>(`/notes/${id}/like`)
}

export const getCategories = () => {
  return get<Category[]>('/notes/categories')
}
