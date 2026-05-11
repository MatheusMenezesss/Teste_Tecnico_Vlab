/*
package br.ufpe.tasktrack.service;

import br.ufpe.tasktrack.domain.Lesson;
import br.ufpe.tasktrack.repository.LessonRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LessonServiceImplementation implements LessonService {
    
    private static final Logger logger = LoggerFactory.getLogger(LessonServiceImplementation.class);
    private final LessonRepository lessonRepository;
    
    public LessonServiceImplementation(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }
    
    @Override
    public List<Lesson> getLessonsByCourseId(Integer courseId) {
        logger.info("Buscando aulas para o curso com ID: " + courseId);
        return lessonRepository.findByCourseId(courseId);
    }
    
    @Override
    public List<Lesson> getAllLessons() {
        logger.info("Buscando todas as aulas");
        return lessonRepository.findAll();
    }
    
    @Override
    public Optional<Lesson> getLessonById(Integer id) {
        logger.info("Buscando aula com ID: " + id);
        return lessonRepository.findById(id);
    }
    
    @Override
    public Lesson createLesson(Lesson lesson) {
        logger.info("Criando nova aula: " + lesson.getTitle());
        if (lesson.getStatus() == null) {
            lesson.setStatus(Lesson.Status.DRAFT);
        }
        return lessonRepository.save(lesson);
    }
    
    @Override
    public Lesson updateLesson(Integer id, Lesson lesson) {
        logger.info("Atualizando aula com ID: " + id);
        Lesson lessonExistente = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula não encontrada com ID: " + id));
        
        lessonExistente.atualizarDados(lesson.getTitle(), lesson.getStatus(), lesson.getVideoUrl());
        return lessonRepository.save(lessonExistente);
    }
    
    @Override
    public void deleteLesson(Integer id) {
        logger.info("Deletando aula com ID: " + id);
        lessonRepository.deleteById(id);
    }
    
}*/
