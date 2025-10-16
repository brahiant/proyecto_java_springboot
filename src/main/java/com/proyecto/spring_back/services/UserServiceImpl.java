package com.proyecto.spring_back.services;

import org.springframework.stereotype.Service;

import com.proyecto.spring_back.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.spring_back.entities.Role;
import com.proyecto.spring_back.models.IUser;
import com.proyecto.spring_back.repositories.RoleRepository;
import com.proyecto.spring_back.repositories.UserRepository;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        List<Role> roles = setUserRoles(user);
        user.setRoles(roles);
        user.setPassword(user.getPassword());
        return userRepository.save(user);
    }

    private List<Role> setUserRoles(IUser user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");
        optionalRole.ifPresent(roles::add);
        if (user.isAdmin()) {
            Optional<Role> optionalAdminRole = roleRepository.findByName("ROLE_ADMIN");
            optionalAdminRole.ifPresent(roles::add);
        }
        return roles;
    }


}
