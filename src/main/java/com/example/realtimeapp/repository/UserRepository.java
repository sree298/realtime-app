package com.example.realtimeapp.repository;

import com.example.realtimeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
