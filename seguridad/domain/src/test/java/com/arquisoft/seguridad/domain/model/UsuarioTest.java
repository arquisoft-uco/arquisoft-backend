package com.arquisoft.seguridad.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para la entidad de dominio {@link Usuario}.
 *
 * <p>Capa: domain — Java puro, sin Spring, sin Mockito.
 * El contexto {@code seguridad} NO usa AggregateRoot; {@code Usuario} es una
 * entidad plana. Esta HU es de solo lectura, por lo que solo se testea
 * {@code rebuild(...)}.
 */
class UsuarioTest {

    @Test
    void debeReconstruirUsuario_cuandoDatosValidos() {
        // Arrange
        UUID id              = UUID.randomUUID();
        UUID keycloakUserId  = UUID.randomUUID();
        String nombre        = "Juan";
        String apellido      = "Pérez";
        String email         = "juan.perez@universidad.edu.co";
        String identificador = "1234567890";
        EstadoUsuario estado  = EstadoUsuario.ACTIVO;
        List<UsuarioRole> roles = List.of(UsuarioRole.ESTUDIANTE, UsuarioRole.ASESOR);

        // Act
        Usuario usuario = Usuario.rebuild(
                id, keycloakUserId, nombre, apellido, email, identificador, estado, roles);

        // Assert — todos los campos asignados correctamente
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getKeycloakUserId()).isEqualTo(keycloakUserId);
        assertThat(usuario.getNombre()).isEqualTo(nombre);
        assertThat(usuario.getApellido()).isEqualTo(apellido);
        assertThat(usuario.getEmail()).isEqualTo(email);
        assertThat(usuario.getIdentificador()).isEqualTo(identificador);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(usuario.getRoles()).containsExactly(UsuarioRole.ESTUDIANTE, UsuarioRole.ASESOR);
    }
}
