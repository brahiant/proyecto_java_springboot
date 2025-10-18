package com.proyecto.spring_back.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UserSaveRequest implements IUser {
    @NotEmpty
    private String name;

    @NotEmpty
    private String lastname;

    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank
    @Size(min = 4, max = 12)
    private String username;

    @NotEmpty
    @Email
    private String email;

    private boolean admin;

    @Override
    public boolean isAdmin() {
        return admin;
    }
}