import api from './api'

export async function listLessonsByCourse(courseId) {
  const res = await api.get(`/courses/${courseId}/lessons`)
  return res.data
}

export async function createLesson(courseId, payload) {
  const res = await api.post(`/courses/${courseId}/lessons`, payload)
  return res.data
}

export async function updateLesson(id, payload) {
  const res = await api.put(`/lessons/${id}`, payload)
  return res.data
}

export async function deleteLesson(id) {
  await api.delete(`/lessons/${id}`)
}