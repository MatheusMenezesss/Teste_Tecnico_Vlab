package br.ufpe.tasktrack.controller;

import br.ufpe.tasktrack.domain.Course;
import br.ufpe.tasktrack.domain.Usuario;
import br.ufpe.tasktrack.repository.CourseRepository;
import br.ufpe.tasktrack.repository.UsuarioRepository;
import br.ufpe.tasktrack.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.ufpe.tasktrack.DTO.CourseCreateDTO;
import br.ufpe.tasktrack.DTO.CourseResponseDTO;

import java.util.List;



@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> listarTodos() {
        List<CourseResponseDTO> out = courseRepository.findAll().stream()
                .map(c -> new CourseResponseDTO(
                        c.getIdCourse(),
                        c.getName(),
                        c.getDescription(),
                        c.getStartDate(),
                        c.getEndDate(),
                        c.getCreator() != null ? c.getCreator().getIdUsuario() : null,
                        c.getCreator() != null ? c.getCreator().getEmail() : null
                ))
                .toList();

        return ResponseEntity.ok(out);    
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> obterPorId(@PathVariable Integer id) {
        return courseRepository.findById(id)
                .map(c ->{
                    CourseResponseDTO out = new CourseResponseDTO(
                        c.getIdCourse(),
                        c.getName(),
                        c.getDescription(),
                        c.getStartDate(),
                        c.getEndDate(),
                        c.getCreator() != null ? c.getCreator().getIdUsuario() : null,
                        c.getCreator() != null ? c.getCreator().getEmail() : null
                );
                return ResponseEntity.ok(out);
            })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseResponseDTO> criar(@Valid @RequestBody CourseCreateDTO dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<CourseResponseDTO>build();

        Usuario creator = usuarioRepository.findByEmail(email)
                .orElse(null);
        if (creator == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<CourseResponseDTO>build();

        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setStartDate(dto.getStartDate());
        course.setEndDate(dto.getEndDate());
        course.setCreator(creator);

        Course saved = courseRepository.save(course);

        CourseResponseDTO out = new CourseResponseDTO(
                saved.getIdCourse(),
                saved.getName(),
                saved.getDescription(),
                saved.getStartDate(),
                saved.getEndDate(),
                creator.getIdUsuario(),
                creator.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody CourseCreateDTO dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<CourseResponseDTO>build();

        return courseRepository.findById(id)
                .map(existing -> {
                    if (existing.getCreator() == null || existing.getCreator().getEmail() == null ||
                            !existing.getCreator().getEmail().equalsIgnoreCase(email)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<CourseResponseDTO>build();
                    }

                    existing.setName(dto.getName());
                    existing.setDescription(dto.getDescription());
                    existing.setStartDate(dto.getStartDate());
                    existing.setEndDate(dto.getEndDate());

                    Course saved = courseRepository.save(existing);

                    CourseResponseDTO out = new CourseResponseDTO(
                            saved.getIdCourse(),
                            saved.getName(),
                            saved.getDescription(),
                            saved.getStartDate(),
                            saved.getEndDate(),
                            saved.getCreator() != null ? saved.getCreator().getIdUsuario() : null,
                            saved.getCreator() != null ? saved.getCreator().getEmail() : null
                    );

                    return ResponseEntity.ok(out);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).<Void>build();

        return courseRepository.findById(id)
                .<ResponseEntity<Void>>map(existing -> {
                    if (existing.getCreator() == null || existing.getCreator().getEmail() == null ||
                            !existing.getCreator().getEmail().equalsIgnoreCase(email)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
                    }

                    courseRepository.delete(existing);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}