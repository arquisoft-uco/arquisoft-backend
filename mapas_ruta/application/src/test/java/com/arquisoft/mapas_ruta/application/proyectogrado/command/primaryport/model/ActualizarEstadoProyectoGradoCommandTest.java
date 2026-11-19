package com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActualizarEstadoProyectoGradoCommandTest {

    @Test
    void debeConvertirElUuid_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-01T10:00:00Z");

        // Act
        var command = ActualizarEstadoProyectoGradoCommand.crear(proyecto.toString(), "ATRASADO", ocurridoEn);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.estadoNuevo()).isEqualTo("ATRASADO");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeLanzar400_cuandoDatoMalformado() {
        // Act & Assert
        assertThatThrownBy(() -> ActualizarEstadoProyectoGradoCommand.crear("no-uuid", " ", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        MapasRutaCodes.ProyectoGrado.ID_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO,
                                        MapasRutaCodes.ProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }
}
