package com.tqs.polarent.repository;

import com.tqs.polarent.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
