package com.arquisoft.proyectos.application.asesor.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarAsesorCommandTest {

    @Test
    void debeCrearElCommand_cuandoTodosLosDatosSonValidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = ActualizarAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzar400_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ActualizarAsesorCommand.crear(
                "no-es-un-uuid", " ", "Ana Perez", " ", null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
