package com.tqs.polarent.cucumber;

import com.tqs.polarent.dto.LoginRequestDTO;
import com.tqs.polarent.dto.LoginResponseDTO;
import com.tqs.polarent.dto.RegisterRequestDTO;
import com.tqs.polarent.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<LoginResponseDTO> response;
    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;

    @Given("I am a new user")
    public void iAmANewUser() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("newuser_" + System.currentTimeMillis() + "@test.com");
        registerRequest.setPassword("password123");
    }

    @When("I register with valid credentials")
    public void iRegisterWithValidCredentials() {
        response = restTemplate.postForEntity("/api/auth/register", registerRequest, LoginResponseDTO.class);
    }

    @Then("I should be registered successfully")
    public void iShouldBeRegisteredSuccessfully() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Given("I am a registered user")
    public void iAmARegisteredUser() {
        registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("testuser_" + System.currentTimeMillis() + "@test.com");
        registerRequest.setPassword("password123");
        restTemplate.postForEntity("/api/auth/register", registerRequest, LoginResponseDTO.class);

        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());
    }

    @When("I login with valid credentials")
    public void iLoginWithValidCredentials() {
        response = restTemplate.postForEntity("/api/auth/login", loginRequest, LoginResponseDTO.class);
    }

    @Then("I should receive a successful login response")
    public void iShouldReceiveASuccessfulLoginResponse() {
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @When("I login with invalid credentials")
    public void iLoginWithInvalidCredentials() {
        loginRequest.setPassword("wrongpassword");
        response = restTemplate.postForEntity("/api/auth/login", loginRequest, LoginResponseDTO.class);
    }

    @Then("I should receive an authentication error")
    public void iShouldReceiveAnAuthenticationError() {
        assertThat(response.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.BAD_REQUEST, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
