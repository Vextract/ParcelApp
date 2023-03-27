package com.mikhail.test.authdepartment.service.auth;

import com.mikhail.test.authdepartment.config.auth.JwtTokenUtil;
import com.mikhail.test.authdepartment.config.exceptions.EmailTakenException;
import com.mikhail.test.authdepartment.model.Role;
import com.mikhail.test.authdepartment.model.User;
import com.mikhail.test.authdepartment.repository.UserRepository;
import com.mikhail.test.authdepartment.service.kafka.AuthProducer;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationRequest;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationResponse;
import com.mikhail.test.authdepartment.templates.auth.RegisterRequest;
import org.apache.kafka.common.errors.AuthorizationException;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthProducer authProducer;

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                authProducer,
                userService,
                jwtTokenUtil
        );
    }

    @Test
    public void registerNewUserTest() throws EmailTakenException {
        // Given
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "testpassword");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encryptedPassword");

        User user = User.builder()
                .fullname(request.getFullName())
                .email(request.getEmail())
                .password("encryptedPassword")
                .role(Role.ROLE_USER)
                .build();

        when(userRepository.save(user)).thenReturn(user);

        when(jwtTokenUtil.generateToken(user, user.getId())).thenReturn("generatedToken");

        // When
        AuthenticationResponse response = authenticationService.register(request, Role.ROLE_USER);

        // Then
        assertThat(response.getToken()).isNotNull();
        assertThat(response.getToken()).isEqualTo("generatedToken");
    }

    @Test
    @DisplayName("If email already taken")
    public void ifEmailAlreadyTakenShouldThrow() {
        // Given
        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "testpassword");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> authenticationService.register(request, Role.ROLE_USER))
                .isInstanceOf(AuthorizationException.class)
                .hasMessage("Error: Email is already in use!");

        verify(userRepository, never()).save(any());
    }

    @Test
    public void authenticateTest() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "testpassword");

        when(userService.loadUserByUsername(request.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtTokenUtil.generateToken( any(UserDetails.class), any(Long.TYPE))).thenReturn("generatedToken");

        // When
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Then
        assertThat(response.getToken()).isNotNull();
        assertThat("generatedToken").isEqualTo(response.getToken());
    }

    @Test
    @WithMockUser()
    public void authenticateInvalidUserTest() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "invalid");
        // When
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Then
        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(AuthenticationException.class);
    }

}