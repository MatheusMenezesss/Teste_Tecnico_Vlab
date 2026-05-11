// Centraliza as chamadas da API relacionadas a cursos.
import api from './api'

export async function listCourses() {
  const res = await api.get('/courses')
  return res.data
}

export async function getCourse(id) {
  const res = await api.get(`/courses/${id}`)
  return res.data
}

export async function createCourse(payload) {
  const res = await api.post('/courses', payload)
  return res.data
}

export async function updateCourse(id, payload) {
  const res = await api.put(`/courses/${id}`, payload)
  return res.data
}

export async function deleteCourse(id) {
  await api.delete(`/courses/${id}`)
}