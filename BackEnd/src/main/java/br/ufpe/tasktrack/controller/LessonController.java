package br.ufpe.tasktrack.controller;

import br.ufpe.tasktrack.domain.Lesson;
import br.ufpe.tasktrack.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<Lesson>> listarAulasPorCurso(@PathVariable Integer courseId) {
        return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
    }

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<Lesson> criarAula(
            @PathVariable Integer courseId,
            @Valid @RequestBody Lesson lesson) {

        Lesson novaAula = lessonService.createLesson(courseId, lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAula);
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<Lesson> atualizarAula(
            @PathVariable Integer id,
            @Valid @RequestBody Lesson lesson) {
        try {
            return ResponseEntity.ok(lessonService.updateLesson(id, lesson));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/lessons/{id}")
    public ResponseEntity<Void> deletarAula(@PathVariable Integer id) {
        try {
            lessonService.deleteLesson(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}