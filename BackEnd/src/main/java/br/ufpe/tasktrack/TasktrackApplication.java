/* Ponto de entrada do backend Spring Boot; sobe a aplicacao e inicializa o contexto. */
package br.ufpe.tasktrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TasktrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasktrackApplication.class, args);
	}

}
