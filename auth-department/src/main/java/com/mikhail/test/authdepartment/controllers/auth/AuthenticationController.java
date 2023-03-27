package com.mikhail.test.authdepartment.controllers.auth;

import com.mikhail.test.authdepartment.config.exceptions.EmailTakenException;
import com.mikhail.test.authdepartment.model.Role;
import com.mikhail.test.authdepartment.service.auth.AuthenticationService;
import com.mikhail.test.authdepartment.templates.auth.AuthenticationRequest;
import com.mikhail.test.authdepartment.templates.auth.BadRequestResponse;
import com.mikhail.test.authdepartment.templates.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register/user")
    @Operation(
            description = "Api for ROLE_USER registry.",
            parameters = {
                    @Parameter(name = "Fullname",
                            description = "User's fullname as the String value. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Email",
                            description = "User's email as the String value. " +
                                    "Valid email format required. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Password",
                            description = "User's password as the String value. " +
                                    "Not empty. Null not allowed." +
                                    "Will be encrypted.")
            },
            responses = {
                    @ApiResponse(responseCode = "201",ref = "userCreatedOrAuthenticatedAPI"),
                    @ApiResponse(responseCode = "409",ref = "emailTakenAPI"),
                    @ApiResponse(responseCode = "406",ref = "notAcceptableAPI"),
            }
    )
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest request
    ) {
        try {
            return new ResponseEntity<>(service.register(request, Role.ROLE_USER), HttpStatus.CREATED);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        } catch (EmailTakenException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/register/courier")
    @Operation(
            description = "Api for ROLE_COURIER registry. Requires ROLE_ADMIN rights.",
            parameters = {
                    @Parameter(name = "Fullname",
                            description = "User's fullname as the String value. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Email",
                            description = "User's email as the String value. " +
                                    "Valid email format required. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Password",
                            description = "User's password as the String value. " +
                                    "Not empty. Null not allowed." +
                                    "Will be encrypted.")
            },
            responses = {
                    @ApiResponse(responseCode = "201",ref = "userCreatedOrAuthenticatedAPI"),
                    @ApiResponse(responseCode = "401",
                            description = "User that's trying to access this endpoint doesn't have Admin rights."),
                    @ApiResponse(responseCode = "409",ref = "emailTakenAPI"),
                    @ApiResponse(responseCode = "406",ref = "notAcceptableAPI"),
            }
    )
    public ResponseEntity<?> registerCourier(
            @RequestBody RegisterRequest request
    ) {
        try {
            return new ResponseEntity<>(service.register(request, Role.ROLE_COURIER), HttpStatus.CREATED);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        } catch (EmailTakenException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/register/admin")
    @Operation(
            description = "Api for ROLE_ADMIN registry. It available publicly just for test case.",
            parameters = {
                    @Parameter(name = "Fullname",
                            description = "User's fullname as the String value. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Email",
                            description = "User's email as the String value. " +
                                    "Valid email format required. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Password",
                            description = "User's password as the String value. " +
                                    "Not empty. Null not allowed." +
                                    "Will be encrypted.")
            },
            responses = {
                    @ApiResponse(responseCode = "201",ref = "userCreatedOrAuthenticatedAPI"),
                    @ApiResponse(responseCode = "409",ref = "emailTakenAPI"),
                    @ApiResponse(responseCode = "406",ref = "notAcceptableAPI"),
            }
    )
    public ResponseEntity<?> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        try {
            return new ResponseEntity<>(service.register(request, Role.ROLE_ADMIN), HttpStatus.CREATED);
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        } catch (EmailTakenException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/authenticate")
    @Operation(
            description = "Api for authentication.",
            parameters = {
                    @Parameter(name = "Email",
                            description = "User's email as the String value. " +
                                    "Valid email format required. " +
                                    "Not empty. Null not allowed."),
                    @Parameter(name = "Password",
                            description = "User's password as the String value. " +
                                    "Not empty. Null not allowed. " +
                                    "Will be encrypted and checked " +
                                    "against encrypted password in DB.")
            },
            responses = {
                    @ApiResponse(responseCode = "201",ref = "userCreatedOrAuthenticatedAPI"),
                    @ApiResponse(responseCode = "401",description = "No user with provided credentials found in DB."),
                    @ApiResponse(responseCode = "406",ref = "notAcceptableAPI")
            }
    )
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (AuthorizationException e) {
            return new ResponseEntity<>(new BadRequestResponse(e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
