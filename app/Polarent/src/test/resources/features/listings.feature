@REQ_TQS-24
Feature: Item Discovery & Catalog
  # Browser and search items, filter by location/price, view detailed item descriptions with photos.

  @TEST_TQS-NEW @REQ_TQS-26 @TESTSET_TQS-53
  Scenario: Create a new listing
    Given I am a logged in user
    When I create a listing with title "Canon EOS R5" and daily rate 75.0
    Then the listing should be created successfully

  @TEST_TQS-65 @REQ_TQS-3 @TESTSET_TQS-53
  Scenario: View all enabled listings
    Given there are enabled listings in the system
    When I request all enabled listings
    Then I should see a list of available listings

  @TEST_TQS-64 @REQ_TQS-22 @TESTSET_TQS-53
  Scenario: Search listings by keyword
    Given there are listings in the system
    When I search for listings with keyword "camera"
    Then I should see listings matching the search term

  @TEST_TQS-63 @REQ_TQS-23 @TESTSET_TQS-53
  Scenario: Filter listings by price range
    Given there are listings with various prices
    When I filter listings with min price 20.0 and max price 100.0
    Then I should see listings within that price range

  @TEST_TQS-62 @REQ_TQS-23 @TESTSET_TQS-53
  Scenario: Filter listings by city
    Given there are listings in different cities
    When I filter listings by city "Aveiro"
    Then I should see listings only from that city

  @TEST_TQS-54 @TESTSET_TQS-53
  Scenario: Delete a listing
    Given I own a listing
    When I delete my listing
    Then the listing should be removed