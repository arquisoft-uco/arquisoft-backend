package com.arquisoft.solicitudes.application.usuario.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarUsuarioCommandTest {

    @Test
    void debeCrearElCommand_cuandoTodosLosDatosSonValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = ActualizarUsuarioCommand.crear(
                id.toString(), "EST-001", "Ana Estudiante", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("EST-001");
        assertThat(command.nombre()).isEqualTo("Ana Estudiante");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzar400_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ActualizarUsuarioCommand.crear(
                "no-es-un-uuid", " ", "Ana Estudiante", " ", null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
