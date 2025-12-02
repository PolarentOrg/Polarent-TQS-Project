package com.tqs.polarent.mapper;

import com.tqs.polarent.dto.UserRequestDTO;
import com.tqs.polarent.dto.UserResponseDTO;
import com.tqs.polarent.dto.UserUpdateDTO;
import com.tqs.polarent.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // Convert Entity to Response DTO
    UserResponseDTO toDto(User user);

    // Convert Request DTO to Entity (ignore auto-generated fields)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDTO userRequestDTO);

    // Update entity from UpdateDTO (ignore fields that shouldn't be updated)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true) // Typically role shouldn't be updated via regular update
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}