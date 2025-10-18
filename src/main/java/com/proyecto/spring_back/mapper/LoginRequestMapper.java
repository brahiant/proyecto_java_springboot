package com.proyecto.spring_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.proyecto.spring_back.models.LoginRequest;
import com.proyecto.spring_back.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginRequestMapper {
    // No exponemos password al mapear desde entidad a DTO
    @Mapping(target = "password", ignore = true)
    LoginRequest toLoginRequest(User user);

    // Solo establecemos username y password desde el DTO de login hacia la entidad
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "lastname", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "admin", ignore = true)
    User toUser(LoginRequest loginRequest);

}
