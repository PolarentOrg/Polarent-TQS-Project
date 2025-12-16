package com.tqs.polarent.services;

import com.tqs.polarent.dto.EquipmentDetailsDTO;
import com.tqs.polarent.entity.Listing;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.mapper.ListingMapper;
import com.tqs.polarent.repository.ListingRepository;
import com.tqs.polarent.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipmentDetailsServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ListingMapper listingMapper;

    @InjectMocks
    private ListingService listingService;

    @Test
    void getEquipmentDetails_Success() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setOwnerId(1L);
        listing.setTitle("Canon EOS R5");
        listing.setDescription("Professional camera");
        listing.setDailyRate(50.0);
        listing.setCity("Lisbon");
        listing.setDistrict("Centro");
        listing.setEnabled(true);
        listing.setCreatedAt(LocalDateTime.now());

        User owner = new User();
        owner.setId(1L);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john@example.com");

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        EquipmentDetailsDTO result = listingService.getEquipmentDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Canon EOS R5", result.getTitle());
        assertEquals("John Doe", result.getOwnerName());
        assertEquals("john@example.com", result.getOwnerEmail());
        assertTrue(result.getAvailable());
    }

    @Test
    void getEquipmentDetails_ListingNotFound() {
        when(listingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> listingService.getEquipmentDetails(999L));
    }

    @Test
    void getEquipmentDetails_ListingDisabled() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setEnabled(false);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        assertThrows(IllegalArgumentException.class, 
            () -> listingService.getEquipmentDetails(1L));
    }

    @Test
    void getEquipmentDetails_OwnerNotFound() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setOwnerId(1L);
        listing.setEnabled(true);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
            () -> listingService.getEquipmentDetails(1L));
    }
}
