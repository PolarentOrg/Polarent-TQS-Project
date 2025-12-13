package com.tqs.polarent;

import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.enums.Role;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EquipmentDetailsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Test
    void getEquipmentDetails_IntegrationTest() throws Exception {
        User owner = new User();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john@test.com");
        owner.setPassword("password");
        owner.setRole(Role.USER);
        owner = userRepository.save(owner);

        Listing listing = new Listing();
        listing.setOwnerId(owner.getId());
        listing.setTitle("Canon EOS R5");
        listing.setDescription("Professional camera");
        listing.setDailyRate(50.0);
        listing.setCity("Lisbon");
        listing.setDistrict("Centro");
        listing.setEnabled(true);
        listing = listingRepository.save(listing);

        mockMvc.perform(get("/api/listings/" + listing.getId() + "/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listing.getId()))
                .andExpect(jsonPath("$.title").value("Canon EOS R5"))
                .andExpect(jsonPath("$.ownerName").value("John Doe"))
                .andExpect(jsonPath("$.ownerEmail").value("john@test.com"))
                .andExpect(jsonPath("$.available").value(true));
    }
}
