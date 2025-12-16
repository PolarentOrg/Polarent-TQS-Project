@REQ_TQS-26
Feature: Advertisement Listing Management
  # Rent out equipment, edit listing, disable listing, enable listing and stop renting equipment

  @TEST_TQS-60 @REQ_TQS-36 @TESTSET_TQS-57
  Scenario: Successful user registration
    Given I am a new user
    When I register with valid credentials
    Then I should be registered successfully

  @TEST_TQS-56 @REQ_TQS-36 @TESTSET_TQS-57
  Scenario: Successful login
    Given I am a new user
    When I login with valid credentials
    Then I should receive a successful login response

  @TEST_TQS-61 @REQ_TQS-36 @TESTSET_TQS-57
  Scenario: Login with invalid credentials
    Given I am a registered user
    When I login with invalid credentials
    Then I should receive an authentication error