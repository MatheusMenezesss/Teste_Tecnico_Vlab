/* Carrega usuarios, cursos e aulas iniciais a partir dos arquivos JSON de seed. */
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Order(2)
public class CourseLessonSeeder {
    private static final Logger log = LoggerFactory.getLogger(CourseLessonSeeder.class);

    private final GeraUsersSeeder geraUsersSeeder;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${seed.directory:seed-data}")
    private String seedDirectory;

    public CourseLessonSeeder(GeraUsersSeeder geraUsersSeeder, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.geraUsersSeeder = geraUsersSeeder;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }

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
        try {
            geraUsersSeeder.ensureUsersJson();

            List<SeedUser> seedUsers = readSeedList(resolveSeedFile("users.json"), SeedUser.class);
            List<SeedCourse> seedCourses = readSeedList(resolveSeedFile("courses.json"), SeedCourse.class);
            List<SeedLesson> seedLessons = readSeedList(resolveSeedFile("lessons.json"), SeedLesson.class);

            if (seedUsers.isEmpty()) {
                log.warn("users.json está vazio. Nenhum usuário será criado.");
            }

            Map<String, Usuario> usuariosPorEmail = seedUsers.stream()
                    .map(seedUser -> seedUserToUsuario(usuarioRepo, seedUser))
                    .collect(Collectors.toMap(
                            usuario -> normalize(usuario.getEmail()),
                            Function.identity(),
                            (first, ignored) -> first,
                            LinkedHashMap::new
                    ));

            Map<String, Course> cursosPorNome = new LinkedHashMap<>();
            for (SeedCourse seedCourse : seedCourses) {
                Usuario creator = usuariosPorEmail.get(normalize(seedCourse.creatorEmail()));
                if (creator == null) {
                    log.warn("Curso '{}' ignorado porque creatorEmail '{}' não existe em users.json", seedCourse.name(), seedCourse.creatorEmail());
                    continue;
                }

                Course course = courseRepo.findByNameIgnoreCase(seedCourse.name())
                        .orElseGet(() -> courseRepo.save(buildCourse(seedCourse, creator)));
                cursosPorNome.put(normalize(course.getName()), course);
            }

            for (SeedLesson seedLesson : seedLessons) {
                Course course = cursosPorNome.get(normalize(seedLesson.courseName()));
                if (course == null) {
                    course = courseRepo.findByNameIgnoreCase(seedLesson.courseName()).orElse(null);
                }

                if (course == null) {
                    log.warn("Lesson '{}' ignorada porque courseName '{}' não foi encontrado", seedLesson.title(), seedLesson.courseName());
                    continue;
                }

                boolean lessonAlreadyExists = lessonRepo.findByCourseId(course.getIdCourse()).stream()
                        .anyMatch(lesson -> lesson.getTitle() != null && lesson.getTitle().equalsIgnoreCase(seedLesson.title()));

                if (lessonAlreadyExists) {
                    continue;
                }

                lessonRepo.save(buildLesson(course, seedLesson));
            }

            log.info("Seed carregado com {} usuários, {} cursos e {} lessons", usuariosPorEmail.size(), cursosPorNome.size(), seedLessons.size());
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao ler arquivos de seed", e);
        }
    }

    private Usuario seedUserToUsuario(UsuarioRepository usuarioRepo, SeedUser seedUser) {
        return usuarioRepo.findByEmail(seedUser.email())
                .orElseGet(() -> usuarioRepo.save(buildUsuario(seedUser)));
    }

    private Usuario buildUsuario(SeedUser seedUser) {
        Usuario usuario = new Usuario();
        usuario.setNome(seedUser.nome());
        usuario.setEmail(seedUser.email());
        usuario.setSenha(passwordEncoder.encode(seedUser.senha()));
        return usuario;
    }

    private Course buildCourse(SeedCourse seedCourse, Usuario creator) {
        Course course = new Course();
        course.setName(seedCourse.name());
        course.setDescription(seedCourse.description());
        course.setStartDate(LocalDate.parse(seedCourse.startDate()));
        course.setEndDate(LocalDate.parse(seedCourse.endDate()));
        course.setCreator(creator);
        return course;
    }

    private Lesson buildLesson(Course course, SeedLesson seedLesson) {
        Lesson lesson = new Lesson();
        lesson.setCourse(course);
        lesson.setTitle(seedLesson.title());
        lesson.setStatus(parseStatus(seedLesson.status()));
        lesson.setVideoUrl(seedLesson.videoUrl());
        return lesson;
    }

    private Lesson.Status parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return Lesson.Status.DRAFT;
        }

        try {
            return Lesson.Status.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return Lesson.Status.DRAFT;
        }
    }

    private <T> List<T> readSeedList(Path file, Class<T> type) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }

        return objectMapper.readValue(
                file.toFile(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, type)
        );
    }

    private Path resolveSeedFile(String fileName) {
        return Paths.get(seedDirectory, fileName);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}