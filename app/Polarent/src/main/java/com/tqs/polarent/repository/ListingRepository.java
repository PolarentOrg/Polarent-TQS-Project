package com.tqs.polarent.repository;

import com.tqs.polarent.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByEnabledTrue();
    List<Listing> findByOwnerId(Long ownerId);

    // por preço
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(:minPrice IS NULL OR l.dailyRate >= :minPrice) AND " +
            "(:maxPrice IS NULL OR l.dailyRate <= :maxPrice)")
    List<Listing> filterByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice);

    List<Listing> findByDailyRateLessThanEqualAndEnabledTrue(Double maxPrice);
    List<Listing> findByDailyRateGreaterThanEqualAndEnabledTrue(Double minPrice);

    // por localização
    List<Listing> findByCityAndEnabledTrue(String city);
    List<Listing> findByDistrictAndEnabledTrue(String district);

    // por cidade (case insensitive, partial match)
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    List<Listing> findByCityContainingIgnoreCase(@Param("city") String city);

    // preço + localização
    @Query("SELECT l FROM Listing l WHERE l.enabled = true AND " +
            "(:minPrice IS NULL OR l.dailyRate >= :minPrice) AND " +
            "(:maxPrice IS NULL OR l.dailyRate <= :maxPrice) AND " +
            "(:city IS NULL OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%')))")
    List<Listing> filterByPriceAndCity(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("city") String city);

    // com todos os parâmetros
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
