package com.mikhail.test.authdepartment.service.auth;

import com.mikhail.test.authdepartment.config.exceptions.EmailTakenException;
import com.mikhail.test.authdepartment.config.auth.JwtTokenUtil;
import com.mikhail.test.authdepartment.model.Role;
import com.mikhail.test.authdepartment.model.User;
import com.mikhail.test.authdepartment.repository.UserRepository;
import com.mikhail.test.authdepartment.service.kafka.AuthProducer;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationRequest;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationResponse;
import com.mikhail.test.authdepartment.templates.auth.RegisterRequest;
import com.mikhail.test.authdepartment.templates.kafka.NewCourierEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final AuthProducer producer;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthenticationResponse register(RegisterRequest request, Role role) throws AuthorizationException, EmailTakenException {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new EmailTakenException("Email is already in use.");
        }

        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
        request.getFullName() == null || request.getFullName().isEmpty()) {
            throw new AuthorizationException("All fields should be filled.");
        }


        User user = User.builder()
                .fullname(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepo.save(user);

        if (role.equals(Role.ROLE_COURIER)) {
            producer.sendMessage(NewCourierEvent.builder()
                    .id(user.getId())
                    .fullname(user.getFullname())
                    .build());
        }

        String token = jwtTokenUtil.generateToken(user, user.getId());


        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthorizationException {

        if (request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new AuthorizationException("All fields should be filled.");
        }

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
        User user = userRepo.findByEmail(userDetails.getUsername()).orElseThrow();
        final String token = jwtTokenUtil.generateToken(userDetails, user.getId());

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
}
