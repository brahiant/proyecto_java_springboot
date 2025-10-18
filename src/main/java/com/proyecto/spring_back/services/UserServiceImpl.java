package com.proyecto.spring_back.services;

import org.springframework.stereotype.Service;

import com.proyecto.spring_back.entities.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.proyecto.spring_back.entities.Role;
import com.proyecto.spring_back.models.IUser;
import com.proyecto.spring_back.repositories.RoleRepository;
import com.proyecto.spring_back.repositories.UserRepository;
import com.proyecto.spring_back.mapper.UserSaveRequestMapper;
import com.proyecto.spring_back.mapper.UserRequestMapper;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import com.proyecto.spring_back.models.UserRequest;
import com.proyecto.spring_back.models.UserSaveRequest;
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserSaveRequestMapper userSaveRequestMapper;

    @Autowired
    private UserRequestMapper userRequestMapper;

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
    public User createUser(UserSaveRequest userSaveRequest) {
        List<Role> roles = setUserRoles(userSaveRequest);
        User user = userSaveRequestMapper.toUser(userSaveRequest);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(userSaveRequest.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(UserRequest userRequest, Long id) {
        Optional<User> existingUser = getUserById(id);
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            // Actualización parcial con MapStruct (nulos ignorados, campos sensibles protegidos)
            userRequestMapper.updateUserFromRequest(userRequest, userToUpdate);
            List<Role> roles = setUserRoles(userRequest);
            userToUpdate.setRoles(roles);
            return Optional.of(userRepository.save(userToUpdate));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
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
