package edu.eci.dosw.DOSW_Library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Aplicación principal de DOSW-Library
 * 
 * <p>
 * <b>Configuración:</b>
 * </p>
 * <ul>
 * <li>@SpringBootApplication - Auto-configuración de Spring Boot</li>
 * <li>@EnableMongoRepositories - Habilita escaneo de repositorios MongoDB</li>
 * </ul>
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "edu.eci.dosw.DOSW_Library.persistence.mongodb.repository")
public class DoswLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoswLibraryApplication.class, args);
	}

}
