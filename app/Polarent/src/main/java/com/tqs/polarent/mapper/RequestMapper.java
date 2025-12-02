package com.tqs.polarent.mapper;

import com.tqs.polarent.dto.RequestRequestDTO;
import com.tqs.polarent.dto.RequestResponseDTO;
import com.tqs.polarent.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    // Convert Entity to Response DTO
    RequestResponseDTO toDto(Request request);

    // Convert Request DTO to Entity (ignore auto-generated fields)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Request toEntity(RequestRequestDTO requestRequestDTO);
}