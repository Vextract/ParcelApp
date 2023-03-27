package com.mikhail.test.courierdepartment.config;

import com.mikhail.test.courierdepartment.config.kafka.utility.ReadJsonFileToJsonObject;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.io.IOException;

@OpenAPIDefinition
@Configuration
public class AppConfig {

    @Bean
    public OpenAPI baseOpenAPI() throws JSONException, IOException {

        ReadJsonFileToJsonObject readJsonFileToJsonObject = new ReadJsonFileToJsonObject();

        ApiResponse okayResponseAPI = new ApiResponse().content(
                new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
                        new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
                                new Example().value(readJsonFileToJsonObject.read().get("okayResponse").toString())))
        ).description("Successful fetching of couriers.");


        Components components = new Components();
        components.addResponses("okayResponseAPI",okayResponseAPI);

        return new OpenAPI()
                .components(components)
                .info(new Info()
                        .title("Courier Doc.")
                        .version("v1")
                        .description("Doc. for Couriers Dept."));
    }
}
