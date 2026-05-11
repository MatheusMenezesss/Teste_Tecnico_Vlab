/* Repositorio JPA para aulas, incluindo a consulta por curso. */
package br.ufpe.tasktrack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import br.ufpe.tasktrack.domain.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    @Query("SELECT l FROM Lesson l WHERE l.course.idCourse = :courseId")
    List<Lesson> findByCourseId(Integer courseId);


}
