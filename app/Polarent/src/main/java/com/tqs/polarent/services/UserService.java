package com.tqs.polarent.services;

import com.tqs.polarent.dto.*;
import com.tqs.polarent.entity.User;
import com.tqs.polarent.mapper.UserMapper;
import com.tqs.polarent.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = userMapper.toEntity(dto);
        user.setActive(true);
        return userMapper.toDto(userRepository.save(user));
    }

    public UserResponseDTO getUserById(Long id) {
        return userMapper.toDto(findUserById(id));
    }

    public UserResponseDTO getUserByEmail(String email) {
        return userMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = findUserById(id);
        userMapper.updateUserFromDto(dto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO activateUser(Long id) {
        User user = findUserById(id);
        user.setActive(true);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO deactivateUser(Long id) {
        User user = findUserById(id);
        user.setActive(false);
        return userMapper.toDto(userRepository.save(user));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}