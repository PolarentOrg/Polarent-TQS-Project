@e2e
Feature: User Authentication E2E
  As a user
  I want to login via the web interface
  So that I can access the rental platform

  Scenario: Successful registration and login via browser
    Given I open the Polarent homepage
    When I click on the register tab
    And I enter first name "Test"
    And I enter last name "User"
    And I enter registration email "e2etest@example.com"
    And I enter registration password "password123"
    And I click the register button
    Then I should see the equipment listings

  Scenario: Failed login with invalid credentials via browser
    Given I open the Polarent homepage
    When I enter email "invalid@example.com" in the login form
    And I enter password "wrongpassword" in the login form
    And I click the login button
    Then I should remain on the login page
