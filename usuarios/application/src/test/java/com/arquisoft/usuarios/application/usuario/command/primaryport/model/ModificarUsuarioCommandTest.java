package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarUsuarioCommandTest {

    @Test
    void debeLanzar400_cuandoElUsuarioNoEsUnUuidValido() {
        // Arrange
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, "Nombre Nuevo", null, null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> ModificarUsuarioCommand.crear("no-es-un-uuid", datos, List.of()))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeLanzar400_cuandoNoHayNingunDatoNiRol() {
        // Arrange
        var usuario = UUID.randomUUID().toString();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, null, null, null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> ModificarUsuarioCommand.crear(usuario, datos, List.of()))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeLanzar400_cuandoElRolNoEsConocido() {
        // Arrange
        var usuario = UUID.randomUUID().toString();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, null, null, null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> ModificarUsuarioCommand.crear(usuario, datos, List.of("rol-inventado")))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeLanzar400_cuandoElRolLlegaEnBlanco() {
        // Arrange
        var usuario = UUID.randomUUID().toString();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, null, null, null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> ModificarUsuarioCommand.crear(usuario, datos, List.of("  ")))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeCrearElCommand_cuandoSoloVieneUnDatoValido() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, " Nombre Nuevo ", null, null, null, null);

        // Act
        var command = ModificarUsuarioCommand.crear(usuarioId.toString(), datos, List.of());

        // Assert
        assertThat(command.usuario()).isEqualTo(usuarioId);
        assertThat(command.nombre()).isEqualTo("Nombre Nuevo");
        assertThat(command.identificador()).isNull();
        assertThat(command.roles()).isEmpty();
    }

    @Test
    void debeCrearElCommand_cuandoSoloVienenRoles() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                null, null, null, null, null, null);

        // Act
        var command = ModificarUsuarioCommand.crear(usuarioId.toString(), datos, List.of("estudiante"));

        // Assert
        assertThat(command.usuario()).isEqualTo(usuarioId);
        assertThat(command.roles()).containsExactly("estudiante");
    }
}
