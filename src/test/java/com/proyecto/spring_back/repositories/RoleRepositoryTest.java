package com.proyecto.spring_back.repositories;

import com.proyecto.spring_back.entities.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Role buildRole(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }

    @Test
    @DisplayName("findByName devuelve el rol persistido")
    void findByName_returnsRole() {
        Role saved = roleRepository.save(buildRole("ROLE_TEST"));
        Optional<Role> found = roleRepository.findByName("ROLE_TEST");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getName()).isEqualTo(saved.getName());
    }

    @Test
    @DisplayName("findByName vacío si no existe")
    void findByName_emptyWhenNotExists() {
        Optional<Role> found = roleRepository.findByName("ROLE_UNKNOWN");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("No permite nombre duplicado (unique constraint)")
    void unique_name_constraint() {
        roleRepository.save(buildRole("ROLE_DUP"));
        assertThatThrownBy(() -> roleRepository.save(buildRole("ROLE_DUP")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("deleteById elimina rol y existsById responde en consecuencia")
    void delete_and_existsById() {
        Role saved = roleRepository.save(buildRole("ROLE_DELETE"));
        Long id = saved.getId();
        assertThat(roleRepository.existsById(id)).isTrue();
        roleRepository.deleteById(id);
        assertThat(roleRepository.existsById(id)).isFalse();
    }
}


