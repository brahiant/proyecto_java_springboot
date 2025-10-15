package com.proyecto.spring_back.Repositories;

import org.springframework.stereotype.Repository;

import com.proyecto.spring_back.entities.Role;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long>{
    Optional<Role> findByName(String name);

}
