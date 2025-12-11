package com.tqs.polarent.repository;

import com.tqs.polarent.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByEnabledTrue();
    List<Listing> findByOwnerId(Long ownerId);

    // Search
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Listing> searchByTerm(@Param("searchTerm") String searchTerm);

    // Filter by price
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(:minPrice IS NULL OR l.dailyRate >= :minPrice) AND " +
            "(:maxPrice IS NULL OR l.dailyRate <= :maxPrice)")
    List<Listing> filterByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    List<Listing> findByDailyRateLessThanEqualAndEnabledTrue(Double maxPrice);
    List<Listing> findByDailyRateGreaterThanEqualAndEnabledTrue(Double minPrice);

    // Filter by location
    List<Listing> findByCityAndEnabledTrue(String city);
    List<Listing> findByDistrictAndEnabledTrue(String district);

    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<Listing> findByCityContainingIgnoreCase(@Param("city") String city);

    // Filter by price + location
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(:minPrice IS NULL OR l.dailyRate >= :minPrice) AND " +
            "(:maxPrice IS NULL OR l.dailyRate <= :maxPrice) AND " +
            "(:city IS NULL OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%')))")
    List<Listing> filterByPriceAndCity(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("city") String city);

    // Advanced filter
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(:minPrice IS NULL OR l.dailyRate >= :minPrice) AND " +
            "(:maxPrice IS NULL OR l.dailyRate <= :maxPrice) AND " +
            "(:city IS NULL OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:district IS NULL OR LOWER(l.district) LIKE LOWER(CONCAT('%', :district, '%')))")
    List<Listing> filterAdvanced(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("city") String city,
            @Param("district") String district);
}
