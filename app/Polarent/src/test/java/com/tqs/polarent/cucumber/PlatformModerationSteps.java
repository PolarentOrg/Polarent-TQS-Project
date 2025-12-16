package com.tqs.polarent.cucumber;

import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import com.tqs.polarent.services.ListingService;
import com.tqs.polarent.services.UserService;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.*;

public class PlatformModerationSteps {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ListingService listingService;
    
    @Autowired
    private ListingRepository listingRepository;
    
    private User testUser;
    private Listing testListing;

    @Given("an inactive user {string} exists")
    public void an_inactive_user_exists(String userName) {
        String emailSafe = userName.toLowerCase().replaceAll("\\s+", ".");

        testUser = userRepository.findByEmail(emailSafe + "@test.com")
                .orElseGet(() -> {
                    User user = User.builder()
                            .firstName(userName)
                            .lastName("Test")
                            .email(emailSafe + "@test.com")
                            .password("password123")
                            .role(com.tqs.polarent.enums.Role.ADMIN)
                            .active(false)
                            .build();
                    return userRepository.save(user);
                });
    }
    
    @When("I click {string} for the active user {string}")
    public void i_click_for_the_active_user(String action, String userName) {
        if ("Deactivate".equals(action)) {
            userService.deactivateUser(testUser.getId());
        }
    }
    
    @When("I click {string} for the inactive user {string}")
    public void i_click_for_the_inactive_user(String action, String userName) {
        if ("Activate".equals(action)) {
            userService.activateUser(testUser.getId());
        }
    }
    
    @Then("the user {string} should be inactive")
    public void the_user_should_be_inactive(String userName) {
        User user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getActive()).isFalse();
    }
    
    @Then("the user {string} should be active")
    public void the_user_should_be_active(String userName) {
        User user = userRepository.findById(testUser.getId()).orElseThrow();
        assertThat(user.getActive()).isTrue();
    }
    
    @Given("a listing {string} with status {string}, id {int}, and owner id {int} exists")
    public void a_listing_exists(String title, String status, Integer listingId, Integer ownerId) {
        testListing = listingRepository.findById(listingId.longValue())
            .orElseGet(() -> {
                Listing listing = new Listing();
                listing.setTitle(title);
                listing.setEnabled("ENABLED".equals(status));
                listing.setOwnerId(ownerId.longValue());
                listing.setDailyRate(50.0);
                return listingRepository.save(listing);
            });
    }
    
    @Given("I scrolled down to {string}")
    public void i_scrolled_down_to(String section) {
        // UI scroll simulation
    }
    
    @When("I click {string} for the listing with id {int}")
    public void i_click_for_the_listing(String action, Integer listingId) {
        if ("Remove Listing".equals(action)) {
            listingService.removeInappropriateListing(listingId.longValue());
        }
    }
    
    @When("I confirm {string} on the confirmation dialog")
    public void i_confirm_on_dialog(String confirmation) {
        // Confirmation dialog simulation
    }
    
    @Then("the listing {string} should no longer appear in the listing")
    public void the_listing_should_not_appear(String title) {
        assertThat(listingRepository.findById(testListing.getId())).isEmpty();
    }
}
