package com.arquisoft.fichas.application.coordinador.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarCoordinadorCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = AgregarCoordinadorCommand.crear(
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
        assertThatThrownBy(() -> AgregarCoordinadorCommand.crear(
                "no-es-un-uuid", "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now()))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeAcumularErrores_cuandoIdentificadorYEmailFaltan() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarCoordinadorCommand.crear(
                UUID.randomUUID().toString(), " ", "Ana Perez", " ", Instant.now()))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
