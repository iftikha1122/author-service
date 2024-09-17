package com.document.manager.authors.config;


import com.document.manager.authors.domain.Author;
import com.document.manager.authors.domain.Role;
import com.document.manager.authors.domain.Status;
import com.document.manager.authors.repositories.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {


    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initRoles(AuthorRepository authorRepository) {
        return args -> {

            if (Objects.isNull(authorRepository.findActiveByUserName("Admin"))) {

                Author adminUser = Author.builder()
                        .userName("Admin")
                        .password(passwordEncoder.encode("adminPassword"))
                        .status(Status.ACTIVE)
                        .role(Role.ROLE_ADMIN)
                        .build();
                authorRepository.save(adminUser);

            }
            ;
        }
                ;
    }

    ;
};



