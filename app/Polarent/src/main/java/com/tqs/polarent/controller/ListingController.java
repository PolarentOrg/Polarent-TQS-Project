package com.tqs.polarent.controller;
import com.tqs.polarent.dto.*;
import com.tqs.polarent.services.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tqs.polarent.entity.Listing;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @GetMapping
    public ResponseEntity<List<ListingResponseDTO>> getAllListings() {
        return ResponseEntity.ok(listingService.getAllListings());
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<ListingResponseDTO>> getEnabledListings() {
        return ResponseEntity.ok(listingService.getEnabledListings());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ListingResponseDTO>> getListingsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(listingService.getListingsByOwner(ownerId));
    }

    @PostMapping
    public ListingResponseDTO createListing(@RequestBody @Valid
    ListingRequestDTO dto) {
        return listingService.createListing(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> updateListing(@PathVariable Long id, @RequestBody ListingResponseDTO dto) {
        return ResponseEntity.ok(listingService.updateListing(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> patchListing(@PathVariable Long id, @RequestBody ListingResponseDTO dto) {
        return ResponseEntity.ok(listingService.patchListing(id, dto));
    }
    @DeleteMapping("/{userId}/{listingId}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long userId,
            @PathVariable Long listingId) {
        listingService.deleteListing(userId, listingId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<ListingResponseDTO>> filterByPriceRange(
            @RequestParam(value = "min", required = false) Double minPrice,
            @RequestParam(value = "max", required = false) Double maxPrice) {

        return ResponseEntity.ok(listingService.filterByPriceRange(minPrice, maxPrice));
    }

    // por preço máximo
    @GetMapping("/filter/max-price/{maxPrice}")
    public ResponseEntity<List<ListingResponseDTO>> filterByMaxPrice(
            @PathVariable Double maxPrice) {

        return ResponseEntity.ok(listingService.filterByMaxPrice(maxPrice));
    }

    // por preço mínimo
    @GetMapping("/filter/min-price/{minPrice}")
    public ResponseEntity<List<ListingResponseDTO>> filterByMinPrice(
            @PathVariable Double minPrice) {

        return ResponseEntity.ok(listingService.filterByMinPrice(minPrice));
    }

    // por cidade (exact match)
    @GetMapping("/filter/city/{city}")
    public ResponseEntity<List<ListingResponseDTO>> filterByCity(@PathVariable String city) {
        return ResponseEntity.ok(listingService.filterByCity(city));
    }

    // por cidade (partial match)
    @GetMapping("/search/city")
    public ResponseEntity<List<ListingResponseDTO>> searchByCity(
            @RequestParam(value = "city", required = false) String city) {
        return ResponseEntity.ok(listingService.searchByCity(city));
    }

    // por distrito
    @GetMapping("/filter/district/{district}")
    public ResponseEntity<List<ListingResponseDTO>> filterByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(listingService.filterByDistrict(district));
    }

    // preço + cidade
    @GetMapping("/filter/price-city")
    public ResponseEntity<List<ListingResponseDTO>> filterByPriceAndCity(
            @RequestParam(value = "min", required = false) Double minPrice,
            @RequestParam(value = "max", required = false) Double maxPrice,
            @RequestParam(value = "city", required = false) String city) {
        return ResponseEntity.ok(listingService.filterByPriceAndCity(minPrice, maxPrice, city));
    }

    // Filtro avançado
    @GetMapping("/filter/advanced")
    public ResponseEntity<List<ListingResponseDTO>> filterAdvanced(
            @RequestParam(value = "min", required = false) Double minPrice,
            @RequestParam(value = "max", required = false) Double maxPrice,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "district", required = false) String district) {
        return ResponseEntity.ok(listingService.filterAdvanced(minPrice, maxPrice, city, district));
    }

    // cidades disponíveis
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        return ResponseEntity.ok(listingService.getAllCities());
    }

    // distritos disponíveis
    @GetMapping("/districts")
    public ResponseEntity<List<String>> getAllDistricts() {
        return ResponseEntity.ok(listingService.getAllDistricts());
    }
}
