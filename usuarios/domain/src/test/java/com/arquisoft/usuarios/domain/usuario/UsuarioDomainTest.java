package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioDomainTest {

    @Test
    void debeCrearUsuarioActivo_cuandoSeLeAsignaUnaIdentidad() {
        // Arrange
        var id = UUID.randomUUID();
        var registro = RegistroUsuarioDomain.crear(
                "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233",
                "Ana", "Pérez", List.of("estudiante"));

        // Act
        var usuario = UsuarioDomain.crear(id, registro);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("usr001");
        assertThat(usuario.getNombre()).isEqualTo("Ana Pérez");
        assertThat(usuario.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(usuario.getContacto()).isEqualTo("573001112233");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var usuario = UsuarioDomain.reconstruir(
                id, "usr002", "Juan Pérez", "juan@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("usr002");
        assertThat(usuario.getNombre()).isEqualTo("Juan Pérez");
        assertThat(usuario.getEmail()).isEqualTo("juan@uco.edu.co");
        assertThat(usuario.getContacto()).isEqualTo("573001112233");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }
}
