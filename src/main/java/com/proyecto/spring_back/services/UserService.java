package com.proyecto.spring_back.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.proyecto.spring_back.entities.User;
import com.proyecto.spring_back.models.UserRequest;
import com.proyecto.spring_back.models.UserSaveRequest;

@Service
public interface UserService {

    List<User> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);
    Optional<User> getUserById(Long id);
    User createUser(UserSaveRequest userSaveRequest);
    Optional<User> updateUser(UserRequest userRequest, Long id);
    void deleteById(Long id);

}
