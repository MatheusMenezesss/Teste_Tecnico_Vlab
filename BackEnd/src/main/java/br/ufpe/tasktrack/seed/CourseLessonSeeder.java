package br.ufpe.tasktrack.seed;

import br.ufpe.tasktrack.domain.Course;
import br.ufpe.tasktrack.domain.Lesson;
import br.ufpe.tasktrack.domain.Usuario;
import br.ufpe.tasktrack.repository.CourseRepository;
import br.ufpe.tasktrack.repository.LessonRepository;
import br.ufpe.tasktrack.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class CourseLessonSeeder {

    @Bean
    CommandLineRunner seedCoursesAndLessons(
            UsuarioRepository usuarioRepo,
            CourseRepository courseRepo,
            LessonRepository lessonRepo
    ) {
        return args -> seed(usuarioRepo, courseRepo, lessonRepo);
    }

    @Transactional
    void seed(UsuarioRepository usuarioRepo, CourseRepository courseRepo, LessonRepository lessonRepo) {
        // 1) Precisamos de usuários para serem "creator" dos cursos
        List<Usuario> users = usuarioRepo.findAll();
        if (users.isEmpty()) return;

        Usuario ana = usuarioRepo.findByEmail("ana@email.com").orElse(users.get(0));
        Usuario carlos = usuarioRepo.findByEmail("carlos@email.com").orElse(users.get(0));
        Usuario bia = usuarioRepo.findByEmail("bia@email.com").orElse(users.get(0));

        List<Course> savedCourses = new ArrayList<>();

        savedCourses.add(
                courseRepo.findByNameIgnoreCase("Java + Spring Boot (API)")
                        .orElseGet(() -> courseRepo.save(buildCourse(
                                "Java + Spring Boot (API)",
                                "Curso focado em APIs REST com Spring Boot, JPA e autenticação JWT.",
                                LocalDate.now().minusDays(10),
                                LocalDate.now().plusDays(20),
                                ana
                        )))
        );

        savedCourses.add(
                courseRepo.findByNameIgnoreCase("React (Front-End)")
                        .orElseGet(() -> courseRepo.save(buildCourse(
                                "React (Front-End)",
                                "Componentes, estado, rotas privadas e integração com backend via Axios.",
                                LocalDate.now().minusDays(5),
                                LocalDate.now().plusDays(15),
                                carlos
                        )))
        );

        savedCourses.add(
                courseRepo.findByNameIgnoreCase("Banco de Dados (PostgreSQL)")
                        .orElseGet(() -> courseRepo.save(buildCourse(
                                "Banco de Dados (PostgreSQL)",
                                "Modelagem, relacionamentos, consultas e boas práticas.",
                                LocalDate.now().minusDays(2),
                                LocalDate.now().plusDays(25),
                                bia
                        )))
        );

        // 2) Só cria lessons se ainda não houver nenhuma (evita duplicar)
        if (lessonRepo.count() > 0) return;

        List<Lesson> lessonsToSave = new ArrayList<>();
        for (Course c : savedCourses) {
            if (c.getName().toLowerCase().contains("spring")) {
                lessonsToSave.add(buildLesson(c, "Introdução ao Spring Boot", Lesson.Status.DRAFT, "https://example.com/spring-1"));
                lessonsToSave.add(buildLesson(c, "Controllers + DTOs", Lesson.Status.PUBLISHED, "https://example.com/spring-2"));
                lessonsToSave.add(buildLesson(c, "JPA: entidades e relacionamentos", Lesson.Status.PUBLISHED, "https://example.com/spring-3"));
            } else if (c.getName().toLowerCase().contains("react")) {
                lessonsToSave.add(buildLesson(c, "JSX e componentes", Lesson.Status.PUBLISHED, "https://example.com/react-1"));
                lessonsToSave.add(buildLesson(c, "useState e useEffect", Lesson.Status.PUBLISHED, "https://example.com/react-2"));
                lessonsToSave.add(buildLesson(c, "Rotas privadas + consumo de API", Lesson.Status.DRAFT, "https://example.com/react-3"));
            } else {
                lessonsToSave.add(buildLesson(c, "Modelagem entidade-relacionamento", Lesson.Status.PUBLISHED, "https://example.com/db-1"));
                lessonsToSave.add(buildLesson(c, "SQL SELECT: filtros e joins", Lesson.Status.PUBLISHED, "https://example.com/db-2"));
                lessonsToSave.add(buildLesson(c, "Índices e performance", Lesson.Status.DRAFT, "https://example.com/db-3"));
            }
        }

        lessonRepo.saveAll(lessonsToSave);
    }
    // Cria um Course já com campos essenciais
    private Course buildCourse(String name, String description, LocalDate start, LocalDate end, Usuario creator) {
        Course c = new Course();
        c.setName(name);
        c.setDescription(description);
        c.setStartDate(start);
        c.setEndDate(end);
        c.setCreator(creator);
        return c;
    }

    // Cria uma Lesson vinculada ao Course (associação course_id)
    private Lesson buildLesson(Course course, String title, Lesson.Status status, String videoUrl) {
        Lesson l = new Lesson();
        l.setCourse(course);
        l.setTitle(title);
        l.setStatus(status);
        l.setVideoUrl(videoUrl);
        return l;
    }
}