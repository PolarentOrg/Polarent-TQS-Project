Feature: Booking Management
  As an equipment owner
  I want to manage bookings
  So that I can track rentals of my photography equipment

  Scenario: Booking is created when request is accepted
    Given there is a pending request for my "Sony A7IV" listing
    When I accept the request
    Then a booking should be created with the calculated price

  Scenario: Owner declines a booking
    Given there is a pending booking for my equipment
    When I decline the booking
    Then the booking status should be DECLINED

  Scenario: Renter cancels a booking
    Given I have a pending booking
    When I cancel my booking
    Then the booking status should be CANCELLED

  Scenario: View renter dashboard
    Given I am a renter with bookings
    When I view my dashboard
    Then I should see my rental statistics
