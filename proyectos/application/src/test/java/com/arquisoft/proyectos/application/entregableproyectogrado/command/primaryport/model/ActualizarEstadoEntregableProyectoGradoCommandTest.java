package com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarEstadoEntregableProyectoGradoCommandTest {

    @Test
    void debeCrearCommandConUuidsConvertidos_cuandoDatosValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var command = ActualizarEstadoEntregableProyectoGradoCommand.crear(
                entregable.toString(), proyectoGrado.toString(), 2, "CERRADO", ocurridoEn);

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.version()).isEqualTo(2);
        assertThat(command.estadoEntregable()).isEqualTo("CERRADO");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosDatosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ActualizarEstadoEntregableProyectoGradoCommand.crear(
                "no-es-uuid", null, 0, " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .contains(
                                ProyectosCodes.EntregableProyectoGrado.VERSION_INVALIDA,
                                ProyectosCodes.EntregableProyectoGrado.ESTADO_ENTREGABLE_REQUERIDO,
                                ProyectosCodes.EntregableProyectoGrado.OCURRIDO_EN_REQUERIDO)
                        .hasSize(5));
    }
}
