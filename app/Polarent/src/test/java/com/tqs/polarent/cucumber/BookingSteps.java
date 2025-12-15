package com.tqs.polarent.cucumber;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.enums.Status;
import com.tqs.polarent.repository.BookingRepository;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.RequestRepository;
import com.tqs.polarent.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private ResponseEntity<BookingResponseDTO> bookingResponse;
    private ResponseEntity<RenterDashboardDTO> dashboardResponse;
    private Long ownerId;
    private Long renterId;
    private Long listingId;
    private Long requestId;
    private Long bookingId;

    private void createOwner() {
        RegisterRequestDTO register = new RegisterRequestDTO();
        register.setFirstName("Owner");
        register.setLastName("Booking");
        register.setEmail("owner_book_" + System.currentTimeMillis() + "@test.com");
        register.setPassword("password123");
        restTemplate.postForEntity("/api/auth/register", register, Object.class);
        ownerId = userRepository.findByEmail(register.getEmail()).get().getId();
    }

    private void createRenter() {
        RegisterRequestDTO register = new RegisterRequestDTO();
        register.setFirstName("Renter");
        register.setLastName("Booking");
        register.setEmail("renter_book_" + System.currentTimeMillis() + "@test.com");
        register.setPassword("password123");
        restTemplate.postForEntity("/api/auth/register", register, Object.class);
        renterId = userRepository.findByEmail(register.getEmail()).get().getId();
    }

    private void createListingAndRequest(String title) {
        ListingRequestDTO listingDto = new ListingRequestDTO();
        listingDto.setOwnerId(ownerId);
        listingDto.setTitle(title);
        listingDto.setDailyRate(100.0);
        listingDto.setEnabled(true);
        listingDto.setCity("Aveiro");
        ResponseEntity<ListingResponseDTO> listingResp = restTemplate.postForEntity("/api/listings", listingDto, ListingResponseDTO.class);
        listingId = listingResp.getBody().getId();

        RequestRequestDTO requestDto = new RequestRequestDTO();
        requestDto.setListingId(listingId);
        requestDto.setRequesterId(renterId);
        requestDto.setInitialDate(20251220);
        requestDto.setDuration(3);
        ResponseEntity<RequestResponseDTO> requestResp = restTemplate.postForEntity("/api/requests", requestDto, RequestResponseDTO.class);
        requestId = requestResp.getBody().getId();
    }

    @Given("there is a pending request for my {string} listing")
    public void thereIsAPendingRequestForMyListing(String title) {
        createOwner();
        createRenter();
        createListingAndRequest(title);
    }

    @When("I accept the request")
    public void iAcceptTheRequest() {
        RequestResponseDTO requestDto = new RequestResponseDTO();
        requestDto.setId(requestId);
        requestDto.setListingId(listingId);
        requestDto.setRequesterId(renterId);
        requestDto.setDuration(3);
        bookingResponse = restTemplate.postForEntity("/api/requests/accept", requestDto, BookingResponseDTO.class);
    }

    @Then("a booking should be created with the calculated price")
    public void aBookingShouldBeCreatedWithTheCalculatedPrice() {
        assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookingResponse.getBody()).isNotNull();
        assertThat(bookingResponse.getBody().getPrice()).isEqualTo(300.0);
    }

    @Given("there is a pending booking for my equipment")
    public void thereIsAPendingBookingForMyEquipment() {
        createOwner();
        createRenter();
        createListingAndRequest("Test Equipment");
        RequestResponseDTO requestDto = new RequestResponseDTO();
        requestDto.setId(requestId);
        requestDto.setListingId(listingId);
        requestDto.setRequesterId(renterId);
        requestDto.setDuration(3);
        bookingResponse = restTemplate.postForEntity("/api/requests/accept", requestDto, BookingResponseDTO.class);
        bookingId = bookingResponse.getBody().getId();
    }

    @When("I decline the booking")
    public void iDeclineTheBooking() {
        bookingResponse = restTemplate.exchange("/api/bookings/" + bookingId + "/decline",
                org.springframework.http.HttpMethod.PATCH, null, BookingResponseDTO.class);
    }

    @Then("the booking status should be DECLINED")
    public void theBookingStatusShouldBeDeclined() {
        assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookingResponse.getBody().getStatus()).isEqualTo(Status.DECLINED);
    }

    @Given("I have a pending booking")
    public void iHaveAPendingBooking() {
        createOwner();
        createRenter();
        createListingAndRequest("My Rented Equipment");
        RequestResponseDTO requestDto = new RequestResponseDTO();
        requestDto.setId(requestId);
        requestDto.setListingId(listingId);
        requestDto.setRequesterId(renterId);
        requestDto.setDuration(3);
        bookingResponse = restTemplate.postForEntity("/api/requests/accept", requestDto, BookingResponseDTO.class);
        bookingId = bookingResponse.getBody().getId();
    }

    @When("I cancel my booking")
    public void iCancelMyBooking() {
        bookingResponse = restTemplate.exchange("/api/bookings/" + bookingId + "/cancel",
                org.springframework.http.HttpMethod.PATCH, null, BookingResponseDTO.class);
    }

    @Then("the booking status should be CANCELLED")
    public void theBookingStatusShouldBeCancelled() {
        assertThat(bookingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(bookingResponse.getBody().getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Given("I am a renter with bookings")
    public void iAmARenterWithBookings() {
        createOwner();
        createRenter();
        createListingAndRequest("Dashboard Equipment");
        RequestResponseDTO requestDto = new RequestResponseDTO();
        requestDto.setId(requestId);
        requestDto.setListingId(listingId);
        requestDto.setRequesterId(renterId);
        requestDto.setDuration(3);
        restTemplate.postForEntity("/api/requests/accept", requestDto, BookingResponseDTO.class);
    }

    @When("I view my dashboard")
    public void iViewMyDashboard() {
        dashboardResponse = restTemplate.getForEntity("/api/bookings/renter/" + renterId + "/dashboard",
                RenterDashboardDTO.class);
    }

    @Then("I should see my rental statistics")
    public void iShouldSeeMyRentalStatistics() {
        assertThat(dashboardResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dashboardResponse.getBody()).isNotNull();
    }
}
