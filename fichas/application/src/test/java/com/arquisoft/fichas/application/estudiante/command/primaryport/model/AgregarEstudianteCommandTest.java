package com.arquisoft.fichas.application.estudiante.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstudianteCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = AgregarEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzarValidacion_cuandoElIdDelPayloadNoEsUuid() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudianteCommand.crear(
                "no-es-un-uuid", "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now()))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
