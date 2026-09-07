package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class SincronizarEntregableProyectoAccesoCommandTest {

    @Test
    void debeCrearCommandConUuidsConvertidos_cuandoDatosValidos() {
        // Arrange
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();
        Instant ocurridoEn = Instant.now();

        // Act
        var command = SincronizarEntregableProyectoAccesoCommand.crear(
                entregable.toString(), proyecto.toString(), 5, ocurridoEn);

        // Assert
        assertThat(command.entregable()).isEqualTo(entregable);
        assertThat(command.proyecto()).isEqualTo(proyecto);
        assertThat(command.versionEntregable()).isEqualTo(5);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoEntregableEstaEnBlancoYProyectoNoEsUuidValido() {
        // Act & Assert
        assertThatThrownBy(() -> SincronizarEntregableProyectoAccesoCommand.crear(
                "   ", "no-es-un-uuid", 1, Instant.now()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(
                                                EvaluacionesFields.EntregableProyectoAcceso.ENTREGABLE,
                                                EvaluacionesCodes.EntregableProyectoAcceso.ENTREGABLE_REQUERIDO),
                                        tuple(
                                                EvaluacionesFields.EntregableProyectoAcceso.PROYECTO,
                                                EvaluacionesCodes.EntregableProyectoAcceso.PROYECTO_REQUERIDO)));
    }
}
