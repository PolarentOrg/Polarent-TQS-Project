package com.tqs.polarent.cucumber;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.RequestRepository;
import com.tqs.polarent.repository.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private RequestRepository requestRepository;

    private ResponseEntity<RequestResponseDTO> requestResponse;
    private ResponseEntity<List<RequestResponseDTO>> requestsResponse;
    private ResponseEntity<Void> deleteResponse;
    private Long ownerId;
    private Long renterId;
    private Long listingId;
    private Long requestId;

    private void createOwner() {
        RegisterRequestDTO register = new RegisterRequestDTO();
        register.setFirstName("Owner");
        register.setLastName("Test");
        register.setEmail("owner_req_" + System.currentTimeMillis() + "@test.com");
        register.setPassword("password123");
        restTemplate.postForEntity("/api/auth/register", register, Object.class);
        ownerId = userRepository.findByEmail(register.getEmail()).get().getId();
    }

    private void createRenter() {
        RegisterRequestDTO register = new RegisterRequestDTO();
        register.setFirstName("Renter");
        register.setLastName("Test");
        register.setEmail("renter_req_" + System.currentTimeMillis() + "@test.com");
        register.setPassword("password123");
        restTemplate.postForEntity("/api/auth/register", register, Object.class);
        renterId = userRepository.findByEmail(register.getEmail()).get().getId();
    }

    private void createListing() {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(ownerId);
        dto.setTitle("Canon EOS R5");
        dto.setDescription("Professional camera");
        dto.setDailyRate(100.0);
        dto.setEnabled(true);
        dto.setCity("Aveiro");
        ResponseEntity<ListingResponseDTO> response = restTemplate.postForEntity("/api/listings", dto, ListingResponseDTO.class);
        listingId = response.getBody().getId();
    }

    @Given("there is an available listing for a {string}")
    public void thereIsAnAvailableListingForA(String title) {
        createOwner();
        createRenter();
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(ownerId);
        dto.setTitle(title);
        dto.setDailyRate(100.0);
        dto.setEnabled(true);
        dto.setCity("Aveiro");
        ResponseEntity<ListingResponseDTO> response = restTemplate.postForEntity("/api/listings", dto, ListingResponseDTO.class);
        listingId = response.getBody().getId();
    }

    @When("I create a request for {int} days")
    public void iCreateARequestForDays(int days) {
        RequestRequestDTO dto = new RequestRequestDTO();
        dto.setListingId(listingId);
        dto.setRequesterId(renterId);
        dto.setInitialDate(20251220);
        dto.setDuration(days);
        dto.setNote("Test request");
        requestResponse = restTemplate.postForEntity("/api/requests", dto, RequestResponseDTO.class);
    }

    @Then("the request should be created successfully")
    public void theRequestShouldBeCreatedSuccessfully() {
        assertThat(requestResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(requestResponse.getBody()).isNotNull();
    }

    @Given("I own a listing with pending requests")
    public void iOwnAListingWithPendingRequests() {
        createOwner();
        createRenter();
        createListing();
        RequestRequestDTO dto = new RequestRequestDTO();
        dto.setListingId(listingId);
        dto.setRequesterId(renterId);
        dto.setInitialDate(20251220);
        dto.setDuration(3);
        requestResponse = restTemplate.postForEntity("/api/requests", dto, RequestResponseDTO.class);
    }

    @When("I view requests for my listing")
    public void iViewRequestsForMyListing() {
        requestsResponse = restTemplate.exchange("/api/requests/listing/" + listingId,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestResponseDTO>>() {});
    }

    @Then("I should see all pending requests")
    public void iShouldSeeAllPendingRequests() {
        assertThat(requestsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requestsResponse.getBody()).isNotNull();
    }

    @Given("I have made requests for equipment")
    public void iHaveMadeRequestsForEquipment() {
        createOwner();
        createRenter();
        createListing();
        RequestRequestDTO dto = new RequestRequestDTO();
        dto.setListingId(listingId);
        dto.setRequesterId(renterId);
        dto.setInitialDate(20251220);
        dto.setDuration(2);
        restTemplate.postForEntity("/api/requests", dto, RequestResponseDTO.class);
    }

    @When("I view my requests")
    public void iViewMyRequests() {
        requestsResponse = restTemplate.exchange("/api/requests/requester/" + renterId,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<RequestResponseDTO>>() {});
    }

    @Then("I should see all my requests")
    public void iShouldSeeAllMyRequests() {
        assertThat(requestsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requestsResponse.getBody()).isNotNull();
    }

    @Given("I have a pending request")
    public void iHaveAPendingRequest() {
        createOwner();
        createRenter();
        createListing();
        RequestRequestDTO dto = new RequestRequestDTO();
        dto.setListingId(listingId);
        dto.setRequesterId(renterId);
        dto.setInitialDate(20251220);
        dto.setDuration(2);
        requestResponse = restTemplate.postForEntity("/api/requests", dto, RequestResponseDTO.class);
        requestId = requestResponse.getBody().getId();
    }

    @When("I delete my request")
    public void iDeleteMyRequest() {
        deleteResponse = restTemplate.exchange("/api/requests/" + requestId,
                HttpMethod.DELETE, null, Void.class);
    }

    @Then("the request should be removed")
    public void theRequestShouldBeRemoved() {
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
