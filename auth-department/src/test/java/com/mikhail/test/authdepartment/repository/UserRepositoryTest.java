package com.mikhail.test.authdepartment.repository;

import com.mikhail.test.authdepartment.model.Role;
import com.mikhail.test.authdepartment.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void shouldFindUserByEmail() {
        // Arrange
        User user = User.builder()
                .fullname("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();
        testEntityManager.persist(user);
        // Act
        Optional<User> result = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(user).isEqualTo(result.get());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenEmailNotFound() {
        // Act
        Optional<User> result = userRepository.findByEmail("notfound@example.com");

        // Assert
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void shouldReturnTrueWhenEmailExists() {
        // Arrange
        User user = User.builder()
                .fullname("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // Act
        boolean result = userRepository.existsByEmail("john.doe@example.com");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenEmailDoesNotExist() {
        // Act
        boolean result = userRepository.existsByEmail("notfound@example.com");

        // Assert
        assertThat(result).isFalse();
    }
}