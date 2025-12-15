Feature: User Authentication
  As a user
  I want to login to Polarent
  So that I can access the rental platform

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter email "test@example.com"
    And I enter password "password123"
    And I click the login button
    Then I should see the equipment listings page

  Scenario: Failed login with invalid credentials
    Given I am on the login page
    When I enter email "invalid@example.com"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should see an error message
