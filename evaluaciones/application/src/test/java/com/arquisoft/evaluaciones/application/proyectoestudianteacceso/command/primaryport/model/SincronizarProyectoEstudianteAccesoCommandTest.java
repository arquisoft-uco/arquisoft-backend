package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class SincronizarProyectoEstudianteAccesoCommandTest {

    @Test
    void debeCrearCommandConUuidsConvertidos_cuandoDatosValidos() {
        // Arrange
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        var command = SincronizarProyectoEstudianteAccesoCommand.crear(
                proyecto.toString(), estudiante.toString(), true, ocurridoEn);

        // Assert
        assertThat(command.proyecto()).isEqualTo(proyecto);
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.activo()).isTrue();
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoProyectoYEstudianteNoSonUuidValidos() {
        // Act & Assert
        assertThatThrownBy(() -> SincronizarProyectoEstudianteAccesoCommand.crear(
                "no-es-un-uuid", "otro-invalido", false, Instant.now()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(
                                                EvaluacionesFields.ProyectoEstudianteAcceso.PROYECTO,
                                                EvaluacionesCodes.ProyectoEstudianteAcceso.PROYECTO_REQUERIDO),
                                        tuple(
                                                EvaluacionesFields.ProyectoEstudianteAcceso.ESTUDIANTE,
                                                EvaluacionesCodes.ProyectoEstudianteAcceso.ESTUDIANTE_REQUERIDO)));
    }
}
