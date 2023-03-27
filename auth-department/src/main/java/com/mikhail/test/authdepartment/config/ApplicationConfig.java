package com.mikhail.test.authdepartment.config;

import com.mikhail.test.authdepartment.config.utility.ReadJsonFileToJsonObject;
import com.mikhail.test.authdepartment.repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

@Configuration
@OpenAPIDefinition
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepo;

    @Bean
    public OpenAPI baseOpenAPI() throws JSONException, IOException {

        ReadJsonFileToJsonObject readJsonFileToJsonObject = new ReadJsonFileToJsonObject();

        ApiResponse userCreatedOrAuthenticatedAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("userCreatedOrAuthenticatedResponse").toString())))
        ).description("Successful registration/authentication. Token provided in return.");

        ApiResponse emailTakenAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("emailTakenResponse").toString())))
        ).description("Trying to register user with occupied email.");

        ApiResponse notAcceptableAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("notAcceptableResponse").toString())))
        ).description("Some field either left blank or null.");

        Components components = new Components();
        components.addResponses("userCreatedOrAuthenticatedAPI",userCreatedOrAuthenticatedAPI);
        components.addResponses("emailTakenAPI",emailTakenAPI);
        components.addResponses("notAcceptableAPI",notAcceptableAPI);

        return new OpenAPI()
                .components(components)
                .info(new Info()
                        .title("Auth Doc.")
                        .version("v1")
                        .description("Doc. for Auth. Dept."));
    }



    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
