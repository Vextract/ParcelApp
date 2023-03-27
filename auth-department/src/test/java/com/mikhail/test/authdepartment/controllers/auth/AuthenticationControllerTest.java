package com.mikhail.test.authdepartment.controllers.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mikhail.test.authdepartment.config.auth.JwtAuthorizationTokenFilter;
import com.mikhail.test.authdepartment.config.auth.JwtTokenUtil;
import com.mikhail.test.authdepartment.model.Role;
import com.mikhail.test.authdepartment.model.User;
import com.mikhail.test.authdepartment.service.auth.AuthenticationService;
import com.mikhail.test.authdepartment.service.auth.UserService;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationRequest;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationResponse;
import com.mikhail.test.authdepartment.templates.auth.RegisterRequest;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.EntityResponse;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private UserService mockUserService;

    @Test
    void givenValidRequest_whenRegisterUser_ShouldBeCreated() throws Exception {
        RegisterRequest request = new RegisterRequest("John Smith", "john.smith@example.com", "password");

        AuthenticationResponse response = new AuthenticationResponse("token");
        given(authService.register(request, Role.ROLE_USER)).willReturn(response);

        mockMvc.perform(post("/api/auth/register/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token"));

        verify(authService, times(1)).register(request, Role.ROLE_USER);
    }

    @Test
    public void givenValidRequestAndToken_whenRegisterCourier_ShouldBeCreated() throws Exception {

        User admin = User.builder()
                .id(1L)
                .fullname("admin")
                .email("admin@example.com")
                .password("admin")
                .role(Role.ROLE_ADMIN)
                .build();

        String accessToken = jwtTokenUtil.generateToken(admin, 1L);

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFullName("Test Courier");
        registerRequest.setEmail("test-courier@example.com");
        registerRequest.setPassword("testpassword");

        User createdCourier = User.builder()
                .id(2L)
                .fullname(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .role(Role.ROLE_COURIER)
                .build();

        String responseToken = jwtTokenUtil.generateToken(createdCourier, 2L);

        AuthenticationResponse serviceResponse = AuthenticationResponse.builder()
                .token(responseToken)
                .build();


        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(registerRequest);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/auth/register/courier")
                .content(requestJson)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON);

        when(authService.register(registerRequest, Role.ROLE_COURIER)).thenReturn(
                serviceResponse);
        // Mock Service method used in JwtRequestFilter
        when(mockUserService.loadUserByUsername(eq(admin.getEmail()))).thenReturn(admin);

        mockMvc.perform(request)
                .andExpectAll(
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token").value(responseToken)
                ).andReturn();
    }

    @Test
    void givenValidRequest_whenRegisterAdmin_ShouldBeCreated() throws Exception {
        RegisterRequest request = new RegisterRequest("John Smith", "john.smith@example.com", "password");

        AuthenticationResponse response = new AuthenticationResponse("token");
        given(authService.register(request, Role.ROLE_ADMIN)).willReturn(response);

        mockMvc.perform(post("/api/auth/register/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token"));

        verify(authService, times(1)).register(request, Role.ROLE_ADMIN);
    }

    @Test
    void givenValidCredentials_whenAuthenticateUser_shouldReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john.smith@example.com", "password");

        AuthenticationResponse response = new AuthenticationResponse("token");
        given(authService.authenticate(request)).willReturn(response);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));

        verify(authService, times(1)).authenticate(request);
    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        return objectMapper.writeValueAsString(object);
    }
}