package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverEstudianteProyectoGradoCommandTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Test
    void debeCrearCommandConUuidsConvertidos_cuandoElPayloadEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverEstudianteProyectoGradoCommand.crear(
                proyectoGrado.toString(), estudiante.toString(), OCURRIDO_EN);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.ocurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosTresCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverEstudianteProyectoGradoCommand.crear("no-uuid", "tampoco", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                                EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTE,
                                EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeRechazar_cuandoElProyectoEsElUuidDeCeros() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> RemoverEstudianteProyectoGradoCommand.crear(
                UtilUUID.obtenerPorDefectoComoTexto(), estudiante, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO));
    }
}
