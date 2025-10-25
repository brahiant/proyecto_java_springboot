package com.proyecto.spring_back.repositories;

import com.proyecto.spring_back.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String username) {
        User u = new User();
        u.setName("Name");
        u.setLastname("Last");
        u.setUsername(username);
        u.setEmail(username + "@example.com");
        u.setPassword("pwd");
        return u;
    }

    @Test
    @DisplayName("findByUsername devuelve el usuario persistido")
    void findByUsername_returnsPersistedUser() {
        User saved = userRepository.save(buildUser("user_test"));

        Optional<User> found = userRepository.findByUsername("user_test");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getUsername()).isEqualTo(saved.getUsername());
    }

    @Test
    @DisplayName("findByUsername devuelve vacío cuando no existe")
    void findByUsername_returnsEmpty_whenNotExists() {
        Optional<User> found = userRepository.findByUsername("unknown");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findAll(Pageable) retorna página con tamaño y total esperados")
    void findAll_withPagination_returnsExpectedPage() {
        for (int i = 0; i < 7; i++) {
            userRepository.save(buildUser("user_" + i));
        }
        Page<User> page0 = userRepository.findAll(PageRequest.of(0, 4));
        Page<User> page1 = userRepository.findAll(PageRequest.of(1, 4));

        assertThat(page0.getContent().size()).isEqualTo(4);
        assertThat(page0.getTotalElements()).isEqualTo(7);
        assertThat(page0.getTotalPages()).isEqualTo(2);

        assertThat(page1.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("save y findById persisten y recuperan el usuario")
    void save_and_findById_work() {
        User saved = userRepository.save(buildUser("user_save"));
        assertThat(saved.getId()).isNotNull();

        Optional<User> byId = userRepository.findById(saved.getId());
        assertThat(byId).isPresent();
        assertThat(byId.get().getUsername()).isEqualTo("user_save");
    }

    @Test
    @DisplayName("update actualiza campos del usuario")
    void update_user_fields() {
        User saved = userRepository.save(buildUser("user_upd"));
        saved.setName("NewName");
        saved.setLastname("NewLast");
        User updated = userRepository.save(saved);

        Optional<User> byId = userRepository.findById(updated.getId());
        assertThat(byId).isPresent();
        assertThat(byId.get().getName()).isEqualTo("NewName");
        assertThat(byId.get().getLastname()).isEqualTo("NewLast");
    }

    @Test
    @DisplayName("deleteById elimina el usuario")
    void deleteById_removesUser() {
        User saved = userRepository.save(buildUser("user_del"));
        Long id = saved.getId();
        assertThat(userRepository.findById(id)).isPresent();

        userRepository.deleteById(id);
        assertThat(userRepository.findById(id)).isNotPresent();
    }

    @Test
    @DisplayName("existsById devuelve true/false correctamente")
    void existsById_true_false() {
        User saved = userRepository.save(buildUser("user_exists"));
        Long id = saved.getId();
        assertThat(userRepository.existsById(id)).isTrue();
        assertThat(userRepository.existsById(id + 999)).isFalse();
    }
}


