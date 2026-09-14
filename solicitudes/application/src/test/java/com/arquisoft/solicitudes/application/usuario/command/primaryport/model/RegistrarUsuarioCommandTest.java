package com.arquisoft.solicitudes.application.usuario.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarUsuarioCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = RegistrarUsuarioCommand.crear(
                id.toString(), "EST-9", "Nombre Completo", "n@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.usuarioId()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("EST-9");
        assertThat(command.nombre()).isEqualTo("Nombre Completo");
        assertThat(command.email()).isEqualTo("n@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzarValidacion_cuandoElIdDelPayloadNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarUsuarioCommand.crear(
                "no-es-un-uuid", "EST-9", "Nombre Completo", "n@uco.edu.co", Instant.now()))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
