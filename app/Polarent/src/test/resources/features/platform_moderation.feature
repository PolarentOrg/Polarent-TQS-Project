@REQ_TQS-28
Feature: Platform Moderation
  # Remove inappropriate listing, deactivate accounts, activate accounts

  Background:
    Given I am logged in as an admin
    And I am on the Admin panel

  @TEST_TQS-37 @TESTSET_TQS-46
  Scenario: Deactivate account
    Given an inactive user "test name" exists
    When I click "Deactivate" for the active user "test name"
    Then the user "test name" should be inactive

  @TEST_TQS-44 @REQ_TQS-17 @TESTSET_TQS-46
  Scenario: Activate account
    Given an inactive user "test name" exists
    When I click "Activate" for the inactive user "test name"
    Then the user "test name" should be active

  @TEST_TQS-47 @REQ_TQS-19 @TESTSET_TQS-46
  Scenario: Remove inappropriate listing
    Given a listing "New Camara" with status "ENABLED", id 2, and owner id 1 exists
    And I scrolled down to "Manage Listings"
    When I click "Remove Listing" for the listing with id 2
    And I confirm "ok" on the confirmation dialog
    Then the listing "New Camara" should no longer appear in the listing