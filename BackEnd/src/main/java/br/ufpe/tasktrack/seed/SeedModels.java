/* Registra os modelos auxiliares usados na leitura dos arquivos de seed. */
package br.ufpe.tasktrack.seed;

import java.util.List;

record SeedUser(String nome, String email, String senha) {
}

record SeedCourse(String name, String description, String startDate, String endDate, String creatorEmail) {
}

record SeedLesson(String courseName, String title, String status, String videoUrl) {
}

record RandomUserApiResponse(List<RandomUserItem> results) {
}

record RandomUserItem(RandomUserName name) {
}

record RandomUserName(String first, String last) {
}