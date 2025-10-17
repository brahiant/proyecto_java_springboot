package com.proyecto.spring_back.models;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRequest implements IUser {
    @NotEmpty
    private String name;

    @NotEmpty
    private String lastname;

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
