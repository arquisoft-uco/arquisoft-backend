package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestituirEstudianteProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");

        // Act
        var command = DestituirEstudianteProyectoGradoCommand.crear(
                estudiante.toString(), proyecto.toString(), ocurridoEn);

        // Assert
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoTodoEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> DestituirEstudianteProyectoGradoCommand.crear("no-es-uuid", null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo)
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTE,
                                ArtefactosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                                ArtefactosFields.EstudianteProyectoGrado.OCURRIDO_EN));
    }
}
