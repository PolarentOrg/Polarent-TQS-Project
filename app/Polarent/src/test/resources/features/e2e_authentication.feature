@e2e
Feature: User Authentication E2E
  As a user
  I want to login via the web interface
  So that I can access the rental platform

  Scenario: Successful login via browser
    Given I open the Polarent homepage
    When I enter email "test@example.com" in the login form
    And I enter password "password123" in the login form
    And I click the login button
    Then I should see the equipment listings

  Scenario: Failed login with invalid credentials via browser
    Given I open the Polarent homepage
    When I enter email "invalid@example.com" in the login form
    And I enter password "wrongpassword" in the login form
    And I click the login button
    Then I should see a login error
