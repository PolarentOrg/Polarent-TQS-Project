package com.tqs.polarent.mapper;

import com.tqs.polarent.dto.ListingRequestDTO;
import com.tqs.polarent.dto.ListingResponseDTO;
import com.tqs.polarent.entity.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ListingMapper {
    ListingMapper INSTANCE = Mappers.getMapper(ListingMapper.class);

    // Convert Entity to Response DTO
    ListingResponseDTO toDto(Listing listing);

    // Convert Request DTO to Entity (ignore auto-generated fields)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Listing toEntity(ListingRequestDTO listingRequestDTO);
}