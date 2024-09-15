package com.document.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.document.manager.authors.repositories","com.document.manager.documents.repositories"})

public class AuthorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorServiceApplication.class, args);
	}

}
