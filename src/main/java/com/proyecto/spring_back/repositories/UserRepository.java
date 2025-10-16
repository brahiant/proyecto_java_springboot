package com.proyecto.spring_back.repositories;

import org.springframework.stereotype.Repository;

import com.proyecto.spring_back.entities.User;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
    Page<User> findAll(Pageable pageable);
    Optional<User> findByUsername(String username);

}
