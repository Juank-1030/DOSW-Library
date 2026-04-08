package edu.eci.dosw.DOSW_Library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Aplicación principal de DOSW-Library
 * 
 * <p>
 * <b>Configuración:</b>
 * </p>
 * <ul>
 * <li>@SpringBootApplication - Auto-configuración de Spring Boot</li>
 * <li>@EnableJpaRepositories - Habilita escaneo explícito de repositorios
 * JPA</li>
 * </ul>
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "edu.eci.dosw.DOSW_Library.core.repository")
public class DoswLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoswLibraryApplication.class, args);
	}

}
