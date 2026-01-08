package com.example.schedulemanagementsystem.user.repository;

import com.example.schedulemanagementsystem.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
