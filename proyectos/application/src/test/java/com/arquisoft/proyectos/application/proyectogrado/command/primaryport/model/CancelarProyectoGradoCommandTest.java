package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelarProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = CancelarProyectoGradoCommand.crear(proyectoGrado, coordinador, "Falta de avance");

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.motivo()).isEqualTo("Falta de avance");
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoDatosInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> CancelarProyectoGradoCommand.crear(null, null, " "))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.MOTIVO_REQUERIDO));
        var motivoLargo = "a".repeat(ProyectosLimits.ProyectoGrado.MOTIVO_MAX + 1);
        assertThatThrownBy(() -> CancelarProyectoGradoCommand.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), motivoLargo))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.MOTIVO_LONGITUD_MAXIMA)));
    }
}
