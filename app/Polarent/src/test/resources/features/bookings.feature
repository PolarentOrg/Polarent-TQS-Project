@REQ_TQS-25
Feature: Booking & Scheduling
  As an equipment owner and renter
  I want to manage bookings
  So that I can track rentals of my photography equipment

  @TEST_TQS-72 @REQ_TQS-9 @TESTSET_TQS-70
  Scenario: Booking is created when request is accepted
    Given there is a pending request for my "Sony A7IV" listing
    When I accept the request
    Then a booking should be created with the calculated price

  @TEST_TQS-73 @REQ_TQS-7 @TESTSET_TQS-70
  Scenario: Owner declines a booking
    Given there is a pending booking for my equipment
    When I decline the booking
    Then the booking status should be DECLINED

  @TEST_TQS-74 @REQ_TQS-12 @TESTSET_TQS-70
  Scenario: Renter cancels a booking
    Given I have a pending booking
    When I cancel my booking
    Then the booking status should be CANCELLED

  @TEST_TQS-75 @REQ_TQS-15 @TESTSET_TQS-70
  Scenario: View renter dashboard
    Given I am a renter with bookings
    When I view my dashboard
    Then I should see my rental statistics

  @TEST_TQS-80 @REQ_TQS-13 @TESTSET_TQS-70
  Scenario: View owner dashboard
    Given I am a owner with bookings
    When I view my dashboard
    Then I should see my rental statistics

