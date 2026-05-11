package br.ufpe.tasktrack.service;

import br.ufpe.tasktrack.domain.Course;
import br.ufpe.tasktrack.domain.Lesson;
import br.ufpe.tasktrack.repository.CourseRepository;
import br.ufpe.tasktrack.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.List;


@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Lesson> getLessonsByCourseId(Integer courseId) {
        return lessonRepository.findByCourseId(courseId);
    }

    public Lesson createLesson(Integer courseId, Lesson lesson) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        lesson.setCourse(course);
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Integer id, Lesson lesson) {
        Lesson existing = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula não encontrada"));

        existing.setTitle(lesson.getTitle());
        existing.setStatus(lesson.getStatus());
        existing.setVideoUrl(lesson.getVideoUrl());
        // não permitir trocar course aqui
        return lessonRepository.save(existing);
    }

    public void deleteLesson(Integer id) {
        if (!lessonRepository.existsById(id)) throw new RuntimeException("Aula não encontrada");
        lessonRepository.deleteById(id);
    }
}