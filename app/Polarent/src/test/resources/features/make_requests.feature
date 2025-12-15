Feature: Make Requests
  As a renter
  I want to make requests
  So that I can book equipment

  Scenario: Successful single request
    Given the system is available
    And the listing is enabled
    When I make a single request
    Then I should receive a successful response

  Scenario: Successful multiple requests
    Given the system is available
    And the listings are available
    When I make a request to each listing
    Then I should receive a successful response

  Scenario:  Unsuccessful multiple requests
    Given the system is available
    And the listings are available
    When I make 2 requests to the same listing
    Then I should receive an error message

