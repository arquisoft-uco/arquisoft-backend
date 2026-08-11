package com.arquisoft.usuarios.domain.usuario;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioDomainTest {

    @Test
    void debeCrearUsuario_cuandoDatosValidos() {
        // Arrange
        String email = "test@example.com";
        UsuarioRole rol = UsuarioRole.ESTUDIANTE;

        // Act
        UsuarioDomain usuario = UsuarioDomain.crear(email, rol);

        // Assert
        assertThat(usuario.getId()).isNotNull();
        assertThat(usuario.getEmail()).isEqualTo("test@example.com");
        assertThat(usuario.getRol()).isEqualTo(UsuarioRole.ESTUDIANTE);
    }

    @Test
    void debeReconstruirUsuario_cuandoIdExistente() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        UsuarioDomain usuario = UsuarioDomain.reconstruir(id, "test@example.com", UsuarioRole.COORDINADOR);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getEmail()).isEqualTo("test@example.com");
        assertThat(usuario.getRol()).isEqualTo(UsuarioRole.COORDINADOR);
    }

    @Test
    void debeLanzarExcepcion_cuandoEmailEsNulo() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> UsuarioDomain.crear(null, UsuarioRole.BIBLIOTECARIO))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("email");
    }

    @Test
    void debeLanzarExcepcion_cuandoEmailEsVacio() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> UsuarioDomain.crear("   ", UsuarioRole.ADMINISTRADOR))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("email");
    }

    @Test
    void debeLanzarExcepcion_cuandoRolEsNulo() {
        // Arrange / Act / Assert
        assertThatThrownBy(() -> UsuarioDomain.crear("test@example.com", null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("rol");
    }

    @Test
    void debeNormalizarEmail_cuandoCrearEsInvocado() {
        // Arrange / Act
        UsuarioDomain usuario = UsuarioDomain.crear("  Test@Example.COM  ", UsuarioRole.ASESOR);

        // Assert
        assertThat(usuario.getEmail()).isEqualTo("test@example.com");
    }
}
