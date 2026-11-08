package com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizarProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var command = FinalizarProyectoGradoCommand.crear(id.toString(), ocurridoEn);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(id);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzar400_cuandoUuidInvalidoEInstanteNulo() {
        // Act & Assert
        assertThatThrownBy(() -> FinalizarProyectoGradoCommand.crear("no-es-uuid", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.ProyectoGrado.ID, ArtefactosFields.ProyectoGrado.FINALIZADO_EN));
    }
}
