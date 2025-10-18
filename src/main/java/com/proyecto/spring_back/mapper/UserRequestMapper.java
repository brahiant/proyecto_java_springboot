package com.proyecto.spring_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.proyecto.spring_back.models.UserRequest;
import com.proyecto.spring_back.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRequestMapper {

    // Mapeo directo de entidad a DTO; MapStruct usará coincidencia por nombre
    UserRequest toUserRequest(User user);

    // Evitamos sobreescribir campos controlados por el sistema/seguridad en User
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserRequest userRequest);

    // Actualización parcial: ignora nulos del DTO para no pisar valores existentes
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user);
}
