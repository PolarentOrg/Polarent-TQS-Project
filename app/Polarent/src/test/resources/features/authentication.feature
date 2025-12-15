Feature: User Authentication
  As a user
  I want to register and login
  So that I can access the platform

  Scenario: Successful user registration
    Given I am a new user
    When I register with valid credentials
    Then I should be registered successfully

  Scenario: Successful login
    Given I am a registered user
    When I login with valid credentials
    Then I should receive a successful login response

  Scenario: Login with invalid credentials
    Given I am a registered user
    When I login with invalid credentials
    Then I should receive an authentication error
