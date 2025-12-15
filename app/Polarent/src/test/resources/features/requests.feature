Feature: Request Management
  As a user
  I want to make requests for photography equipment
  So that I can rent items for my projects

  Scenario: Create a rental request
    Given there is an available listing for a "Canon EOS R5"
    When I create a request for 3 days
    Then the request should be created successfully

  Scenario: View requests for my listing
    Given I own a listing with pending requests
    When I view requests for my listing
    Then I should see all pending requests

  Scenario: View my requests as a renter
    Given I have made requests for equipment
    When I view my requests
    Then I should see all my requests

  Scenario: Delete a request
    Given I have a pending request
    When I delete my request
    Then the request should be removed
