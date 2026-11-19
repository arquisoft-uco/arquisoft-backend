package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReanudarProyectoGradoCommandTest {

    @Test
    void debeConvertirElUuid_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var fechaFin = LocalDate.of(2026, 12, 1);
        var hoy = LocalDate.of(2026, 11, 19);

        // Act
        var command = ReanudarProyectoGradoCommand.crear(proyecto.toString(), fechaFin, hoy);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.fechaFin()).isEqualTo(fechaFin);
        assertThat(command.hoy()).isEqualTo(hoy);
    }

    @Test
    void debeLanzar400_cuandoDatoMalformado() {
        // Act & Assert
        assertThatThrownBy(() -> ReanudarProyectoGradoCommand.crear("no-uuid", null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactlyInAnyOrder(
                                        ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                        ProyectosCodes.ProyectoGrado.FECHA_FIN_REQUERIDA,
                                        ProyectosCodes.ProyectoGrado.HOY_REQUERIDA));
    }
}
