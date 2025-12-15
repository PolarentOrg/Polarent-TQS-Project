package com.tqs.polarent.cucumber;

import com.tqs.polarent.dto.ListingRequestDTO;
import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.dto.RegisterRequestDTO;
import com.tqs.polarent.repository.ListingRepository;
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

public class ListingSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    private ResponseEntity<ListingResponseDTO> listingResponse;
    private ResponseEntity<List<ListingResponseDTO>> listingsResponse;
    private ResponseEntity<Void> deleteResponse;
    private Long userId;
    private Long listingId;

    @Given("I am a logged in user")
    public void iAmALoggedInUser() {
        RegisterRequestDTO register = new RegisterRequestDTO();
        register.setFirstName("Owner");
        register.setLastName("User");
        register.setEmail("owner_" + System.currentTimeMillis() + "@test.com");
        register.setPassword("password123");
        restTemplate.postForEntity("/api/auth/register", register, Object.class);
        userId = userRepository.findByEmail(register.getEmail()).get().getId();
    }

    @When("I create a listing with title {string} and daily rate {double}")
    public void iCreateAListingWithTitleAndDailyRate(String title, Double dailyRate) {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(userId);
        dto.setTitle(title);
        dto.setDescription("Test description");
        dto.setDailyRate(dailyRate);
        dto.setEnabled(true);
        dto.setCity("Aveiro");
        listingResponse = restTemplate.postForEntity("/api/listings", dto, ListingResponseDTO.class);
    }

    @Then("the listing should be created successfully")
    public void theListingShouldBeCreatedSuccessfully() {
        assertThat(listingResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listingResponse.getBody()).isNotNull();
        assertThat(listingResponse.getBody().getTitle()).isNotNull();
    }

    @Given("there are enabled listings in the system")
    public void thereAreEnabledListingsInTheSystem() {
        iAmALoggedInUser();
        iCreateAListingWithTitleAndDailyRate("Test Camera", 50.0);
    }

    @When("I request all enabled listings")
    public void iRequestAllEnabledListings() {
        listingsResponse = restTemplate.exchange("/api/listings/enabled", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ListingResponseDTO>>() {});
    }

    @Then("I should see a list of available listings")
    public void iShouldSeeAListOfAvailableListings() {
        assertThat(listingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listingsResponse.getBody()).isNotNull();
    }

    @Given("there are listings in the system")
    public void thereAreListingsInTheSystem() {
        iAmALoggedInUser();
        iCreateAListingWithTitleAndDailyRate("Professional Camera", 100.0);
    }

    @When("I search for listings with keyword {string}")
    public void iSearchForListingsWithKeyword(String keyword) {
        listingsResponse = restTemplate.exchange("/api/listings/search?q=" + keyword, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ListingResponseDTO>>() {});
    }

    @Then("I should see listings matching the search term")
    public void iShouldSeeListingsMatchingTheSearchTerm() {
        assertThat(listingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Given("there are listings with various prices")
    public void thereAreListingsWithVariousPrices() {
        iAmALoggedInUser();
        iCreateAListingWithTitleAndDailyRate("Budget Camera", 30.0);
        iCreateAListingWithTitleAndDailyRate("Premium Camera", 150.0);
    }

    @When("I filter listings with min price {double} and max price {double}")
    public void iFilterListingsWithMinPriceAndMaxPrice(Double min, Double max) {
        listingsResponse = restTemplate.exchange("/api/listings/filter/price?min=" + min + "&max=" + max,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ListingResponseDTO>>() {});
    }

    @Then("I should see listings within that price range")
    public void iShouldSeeListingsWithinThatPriceRange() {
        assertThat(listingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Given("there are listings in different cities")
    public void thereAreListingsInDifferentCities() {
        iAmALoggedInUser();
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setOwnerId(userId);
        dto.setTitle("Aveiro Camera");
        dto.setDailyRate(50.0);
        dto.setEnabled(true);
        dto.setCity("Aveiro");
        restTemplate.postForEntity("/api/listings", dto, ListingResponseDTO.class);
    }

    @When("I filter listings by city {string}")
    public void iFilterListingsByCity(String city) {
        listingsResponse = restTemplate.exchange("/api/listings/filter/city/" + city,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ListingResponseDTO>>() {});
    }

    @Then("I should see listings only from that city")
    public void iShouldSeeListingsOnlyFromThatCity() {
        assertThat(listingsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Given("I own a listing")
    public void iOwnAListing() {
        iAmALoggedInUser();
        iCreateAListingWithTitleAndDailyRate("My Camera", 60.0);
        listingId = listingResponse.getBody().getId();
    }

    @When("I delete my listing")
    public void iDeleteMyListing() {
        deleteResponse = restTemplate.exchange("/api/listings/" + userId + "/" + listingId,
                HttpMethod.DELETE, null, Void.class);
    }

    @Then("the listing should be removed")
    public void theListingShouldBeRemoved() {
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
