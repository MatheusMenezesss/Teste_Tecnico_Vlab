package br.ufpe.tasktrack.repository;

//import java.util.List;

import java.util.Optional;


import br.ufpe.tasktrack.domain.Course;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByNameIgnoreCase(String name);
}
