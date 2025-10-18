package com.proyecto.spring_back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.proyecto.spring_back.models.UserSaveRequest;
import com.proyecto.spring_back.entities.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserSaveRequestMapper {
    // Mapea datos de creación desde el DTO hacia la entidad
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(UserSaveRequest userSaveRequest);
}
