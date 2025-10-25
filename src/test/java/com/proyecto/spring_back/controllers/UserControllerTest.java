package com.proyecto.spring_back.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.spring_back.entities.User;
import com.proyecto.spring_back.models.UserRequest;
import com.proyecto.spring_back.models.UserSaveRequest;
import com.proyecto.spring_back.services.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("John");
        user.setLastname("Doe");
        user.setUsername("johnd");
        user.setEmail("john.doe@example.com");
        user.setPassword("secret");
        return user;
    }

    @Test
    @DisplayName("GET /api/users devuelve lista de usuarios")
    void getAllUsers_returnsList() throws Exception {
        List<User> users = Arrays.asList(buildUser(1L), buildUser(2L));
        Mockito.when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/users/page/{page} devuelve usuarios paginados")
    void getAllUsersPaginated_returnsPage() throws Exception {
        List<User> pageContent = Arrays.asList(buildUser(1L), buildUser(2L), buildUser(3L), buildUser(4L));
        Page<User> page = new PageImpl<>(pageContent, PageRequest.of(0, 4), 8);
        Mockito.when(userService.getAllUsers(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users/page/{page}", 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.totalElements", is(8)));
    }

    @Test
    @DisplayName("GET /api/users/{id} devuelve 200 cuando existe")
    void getUserById_returnsOk_whenExists() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(buildUser(1L)));

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("GET /api/users/{id} devuelve 404 cuando no existe")
    void getUserById_returnsNotFound_whenDoesNotExist() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("User not found")));
    }

    @Test
    @DisplayName("POST /api/users devuelve 201 al crear usuario válido")
    void createUser_returnsCreated_onValidPayload() throws Exception {
        String json = "{" +
                "\"name\":\"Jane\"," +
                "\"lastname\":\"Roe\"," +
                "\"username\":\"janer\"," +
                "\"email\":\"jane.roe@example.com\"," +
                "\"password\":\"topsecret\"" +
                "}";

        User created = buildUser(10L);
        created.setName("Jane");
        created.setLastname("Roe");
        created.setUsername("janer");
        created.setEmail("jane.roe@example.com");

        Mockito.when(userService.createUser(any(UserSaveRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)));
    }

    @Test
    @DisplayName("POST /api/users devuelve 400 al payload inválido")
    void createUser_returnsBadRequest_onInvalidPayload() throws Exception {
        UserSaveRequest invalid = new UserSaveRequest();
        // Falta name, lastname, username, email, password

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/users/{id} devuelve 200 al actualizar existente")
    void updateUser_returnsOk_whenExists() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John Updated");
        request.setLastname("Doe");
        request.setUsername("johnd");
        request.setEmail("john.doe@example.com");

        User updated = buildUser(1L);
        updated.setName(request.getName());

        Mockito.when(userService.updateUser(any(UserRequest.class), eq(1L))).thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Updated")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} devuelve 404 si no existe")
    void updateUser_returnsNotFound_whenDoesNotExist() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setLastname("Doe");
        request.setUsername("johnd");
        request.setEmail("john.doe@example.com");

        Mockito.when(userService.updateUser(any(UserRequest.class), eq(99L))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} devuelve 204 cuando elimina")
    void deleteUser_returnsNoContent_whenDeleted() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(buildUser(1L)));
        Mockito.doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} devuelve 404 cuando no existe")
    void deleteUser_returnsNotFound_whenDoesNotExist() throws Exception {
        Mockito.when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/{id}", 99L))
                .andExpect(status().isNotFound());
    }
}


