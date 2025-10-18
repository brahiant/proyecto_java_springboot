package com.proyecto.spring_back.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.proyecto.spring_back.entities.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import java.util.HashMap;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import com.proyecto.spring_back.services.UserServiceImpl;
import com.proyecto.spring_back.models.UserRequest;
import com.proyecto.spring_back.models.UserSaveRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones CRUD de usuarios")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Listar usuarios paginados", description = "Obtiene usuarios paginados de 4 en 4")
    public Page<User> getAllUsers(@PathVariable Integer page) {
        return userService.getAllUsers(PageRequest.of(page, 4));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de usuario por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserSaveRequest userSaveRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> {
                errors.put(err.getField(), err.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        User newUser = userService.createUser(userSaveRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserRequest userRequest, BindingResult bindingResult, @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> {
                errors.put(err.getField(), err.getDefaultMessage());
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Optional<User> updatedUser = userService.updateUser(userRequest, id);
        if (updatedUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario por id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> existingUser = userService.getUserById(id);
        if (existingUser.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Usuario no encontrado"));
        }
    }


}
