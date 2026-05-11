package br.ufpe.tasktrack.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@Order(1)
public class GeraUsersSeeder {
    private static final Logger log = LoggerFactory.getLogger(GeraUsersSeeder.class);
    private static final String DEFAULT_PASSWORD = "123456";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${seed.directory:seed-data}")
    private String seedDirectory;

    public GeraUsersSeeder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    CommandLineRunner gerarUsersJson() {
        return args -> ensureUsersJson();
    }

    void ensureUsersJson() {
        Path usersFile = resolveSeedFile("users.json");

        try {
            Files.createDirectories(usersFile.getParent());

            List<SeedUser> existingUsers = readUsers(usersFile);
            if (!existingUsers.isEmpty()) {
                log.info("users.json já possui {} usuários", existingUsers.size());
                return;
            }

            List<SeedUser> generatedUsers = fetchRandomUsers(50);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(usersFile.toFile(), generatedUsers);
            log.info("users.json gerado com {} usuários em {}", generatedUsers.size(), usersFile.toAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao criar users.json", e);
        }
    }

    private List<SeedUser> fetchRandomUsers(int quantity) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://randomuser.me/api/?results=" + quantity + "&nat=br&inc=name"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                RandomUserApiResponse apiResponse = objectMapper.readValue(response.body(), RandomUserApiResponse.class);
                if (apiResponse != null && apiResponse.results() != null && !apiResponse.results().isEmpty()) {
                    return mapApiUsers(apiResponse.results(), quantity);
                }
            }

            log.warn("Random User API respondeu com status {}. Usando fallback local.", response.statusCode());
        } catch (Exception e) {
            log.warn("Random User API indisponível. Usando fallback local.", e);
        }

        return fallbackUsers(quantity);
    }

    private List<SeedUser> mapApiUsers(List<RandomUserItem> apiUsers, int quantity) {
        List<SeedUser> users = new ArrayList<>();

        for (RandomUserItem item : apiUsers) {
            if (users.size() >= quantity) {
                break;
            }

            String displayName = buildDisplayName(item.name());
            String stableEmail = buildStableEmail(users.size() + 1);
            users.add(new SeedUser(displayName, stableEmail, DEFAULT_PASSWORD));
        }

        while (users.size() < quantity) {
            users.add(new SeedUser(
                    "Usuário " + String.format(Locale.ROOT, "%02d", users.size() + 1),
                    buildStableEmail(users.size() + 1),
                    DEFAULT_PASSWORD
            ));
        }

        return users;
    }

    private List<SeedUser> fallbackUsers(int quantity) {
        List<SeedUser> users = new ArrayList<>();
        for (int index = 1; index <= quantity; index++) {
            users.add(new SeedUser(
                    "Usuário " + String.format(Locale.ROOT, "%02d", index),
                    buildStableEmail(index),
                    DEFAULT_PASSWORD
            ));
        }
        return users;
    }

    private String buildDisplayName(RandomUserName name) {
        if (name == null) {
            return "Usuário";
        }

        return capitalize(name.first()) + " " + capitalize(name.last());
    }

    private String buildStableEmail(int index) {
        return String.format(Locale.ROOT, "usuario%02d@vlab.local", index);
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "Usuario";
        }

        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        return trimmed.substring(0, 1).toUpperCase(Locale.ROOT) + trimmed.substring(1);
    }

    private List<SeedUser> readUsers(Path file) throws IOException {
        if (!Files.exists(file)) {
            return List.of();
        }

        return objectMapper.readValue(
                file.toFile(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, SeedUser.class)
        );
    }

    private Path resolveSeedFile(String fileName) {
        return Paths.get(seedDirectory, fileName);
    }
}