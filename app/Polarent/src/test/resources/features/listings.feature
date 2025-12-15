Feature: Listing Management
  As a user
  I want to manage rental listings
  So that I can rent out my photography equipment

  Scenario: Create a new listing
    Given I am a logged in user
    When I create a listing with title "Canon EOS R5" and daily rate 75.0
    Then the listing should be created successfully

  Scenario: View all enabled listings
    Given there are enabled listings in the system
    When I request all enabled listings
    Then I should see a list of available listings

  Scenario: Search listings by keyword
    Given there are listings in the system
    When I search for listings with keyword "camera"
    Then I should see listings matching the search term

  Scenario: Filter listings by price range
    Given there are listings with various prices
    When I filter listings with min price 20.0 and max price 100.0
    Then I should see listings within that price range

  Scenario: Filter listings by city
    Given there are listings in different cities
    When I filter listings by city "Aveiro"
    Then I should see listings only from that city

  Scenario: Delete a listing
    Given I own a listing
    When I delete my listing
    Then the listing should be removed
