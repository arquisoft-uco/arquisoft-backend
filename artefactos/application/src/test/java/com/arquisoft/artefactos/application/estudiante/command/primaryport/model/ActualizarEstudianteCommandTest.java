package com.arquisoft.artefactos.application.estudiante.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarEstudianteCommandTest {

    @Test
    void debeCrearElCommand_cuandoTodosLosDatosSonValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = ActualizarEstudianteCommand.crear(
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
        assertThatThrownBy(() -> ActualizarEstudianteCommand.crear(
                "no-es-un-uuid", " ", "Ana Perez", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.Estudiante.ID,
                                ArtefactosFields.Estudiante.IDENTIFICADOR,
                                ArtefactosFields.Estudiante.EMAIL,
                                ArtefactosFields.Estudiante.OCURRIDO_EN));
    }
}
