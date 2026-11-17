package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarEstadoProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-02T10:00:00Z");

        // Act
        var command = ActualizarEstadoProyectoGradoCommand.crear(id.toString(), "FINALIZADO", ocurridoEn);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(id);
        assertThat(command.estadoNuevo()).isEqualTo("FINALIZADO");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoIdInvalidoEstadoEnBlancoYFechaNula() {
        // Act & Assert
        assertThatThrownBy(() -> ActualizarEstadoProyectoGradoCommand.crear("no-es-uuid", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.ProyectoGrado.ID,
                                EntregablesFields.ProyectoGrado.ESTADO_PROYECTO,
                                EntregablesFields.ProyectoGrado.OCURRIDO_EN));
    }
}
