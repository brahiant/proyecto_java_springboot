package com.proyecto.spring_back.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Transient;
import com.proyecto.spring_back.models.IUser;

import lombok.Data;


import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "users")
@Data
public class User implements IUser{


    public User() {
        this.roles = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotEmpty
    private String password;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean admin;

    @Override
    public boolean isAdmin() {
        return admin;
    }

    // Esta anotación evita problemas de serialización JSON con Hibernate
    // Ignora propiedades técnicas de Hibernate que no queremos mostrar en JSON
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    
    // Define una relación muchos-a-muchos: Un usuario puede tener varios roles
    // y un rol puede pertenecer a varios usuarios
    @ManyToMany(fetch = FetchType.LAZY) // LAZY = Los roles se cargan solo cuando se necesitan (optimización)
    
    // Configura la tabla intermedia que conecta usuarios con roles
    @JoinTable(
        name = "users_roles",                              // Nombre de la tabla intermedia
        joinColumns = @JoinColumn(name = "user_id"),       // Columna que referencia al usuario
        inverseJoinColumns = @JoinColumn(name = "role_id"), // Columna que referencia al rol
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}) // Evita duplicados: un usuario no puede tener el mismo rol dos veces
    )
    private List<Role> roles; // Lista de roles que tiene este usuario
}
