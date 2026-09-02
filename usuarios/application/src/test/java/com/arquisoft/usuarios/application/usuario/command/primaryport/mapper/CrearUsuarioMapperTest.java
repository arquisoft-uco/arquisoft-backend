package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CrearUsuarioMapperTest {

    @Test
    void debeConstruirElUsuario_cuandoElComandoEsValido() {
        // Arrange
        var command = new CrearUsuarioCommand("test@example.com", UsuarioRole.ESTUDIANTE);

        // Act
        var usuario = CrearUsuarioMapper.toDomain(command);

        // Assert
        assertThat(usuario.getEmail()).isEqualTo("test@example.com");
        assertThat(usuario.getRol()).isEqualTo(UsuarioRole.ESTUDIANTE);
        assertThat(usuario.getId()).isNotNull();
    }

    @Test
    void debeLanzarExcepcion_cuandoEmailEsNulo() {
        // Arrange
        var command = new CrearUsuarioCommand(null, UsuarioRole.COORDINADOR);

        // Act & Assert
        assertThatThrownBy(() -> CrearUsuarioMapper.toDomain(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("email");
    }

    @Test
    void debeLanzarExcepcion_cuandoRolEsNulo() {
        // Arrange
        var command = new CrearUsuarioCommand("test@example.com", null);

        // Act & Assert
        assertThatThrownBy(() -> CrearUsuarioMapper.toDomain(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("rol");
    }
}
