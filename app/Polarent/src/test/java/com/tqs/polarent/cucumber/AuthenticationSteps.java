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

    private ResponseEntity<LoginResponseDTO> loginResponse;
    private ResponseEntity<LoginResponseDTO> registerResponse;
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
        registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, LoginResponseDTO.class);
    }

    @Then("I should be registered successfully")
    public void iShouldBeRegisteredSuccessfully() {
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
    }

    @Given("I am a registered user")
    public void iAmARegisteredUser() {
        // Criar um novo usuário
        registerRequest = new RegisterRequestDTO();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("testuser_" + System.currentTimeMillis() + "@test.com");
        registerRequest.setPassword("password123");

        // Registrar o usuário
        restTemplate.postForEntity("/api/auth/register", registerRequest, LoginResponseDTO.class);

        // Preparar credenciais de login
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());
    }

    @When("I login with valid credentials")
    public void iLoginWithValidCredentials() {
        // Se loginRequest ainda não existe, criar com base no registerRequest
        if (loginRequest == null) {
            // Primeiro registrar o usuário
            registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, LoginResponseDTO.class);

            // Criar loginRequest com as mesmas credenciais
            loginRequest = new LoginRequestDTO();
            loginRequest.setEmail(registerRequest.getEmail());
            loginRequest.setPassword(registerRequest.getPassword());
        }

        loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, LoginResponseDTO.class);
    }

    @Then("I should receive a successful login response")
    public void iShouldReceiveASuccessfulLoginResponse() {
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
    }

    @When("I login with invalid credentials")
    public void iLoginWithInvalidCredentials() {
        loginRequest.setPassword("wrongpassword");
        loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, LoginResponseDTO.class);
    }

    @Then("I should receive an authentication error")
    public void iShouldReceiveAnAuthenticationError() {
        assertThat(loginResponse.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN);
    }
    
    @Given("I am logged in as an admin")
    public void i_am_logged_in_as_an_admin() {
        // Admin login simulation
    }
    
    @Given("I am on the Admin panel")
    public void i_am_on_the_admin_panel() {
        // Navigate to admin panel
    }
}