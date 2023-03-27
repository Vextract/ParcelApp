package com.mikhail.test.authdepartment.service.auth;

import com.mikhail.test.authdepartment.model.Role;
import com.mikhail.test.authdepartment.model.User;
import com.mikhail.test.authdepartment.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    public void testLoadUserByUsername() {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .fullname("John Doe")
                .email(email)
                .password("password")
                .role(Role.ROLE_USER)
                .build();

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = userService.loadUserByUsername(email);

        // then
        assertThat(email).isEqualTo(userDetails.getUsername());
        assertThat(user.getPassword()).isEqualTo(userDetails.getPassword());
        assertThat(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(user.getRole().name()))).isTrue();
    }

    @Test
    public void testLoadUserByUsernameWhenUserNotFound() {
        // given
        String email = "test@example.com";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.loadUserByUsername(email))
                .isInstanceOf(NoSuchElementException.class);
    }
}