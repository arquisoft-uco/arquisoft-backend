package com.arquisoft.evaluaciones.application.entregable.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEntregableCommandTest {

    @Test
    void debeCrearRecortandoElProyecto_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarEntregableCommand.crear(
                entregable.toString(), proyectoGrado.toString(), "  Sistema de riego ", 3);

        // Assert
        assertThat(command.entregable()).isEqualTo(entregable);
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.proyecto()).isEqualTo("Sistema de riego");
        assertThat(command.version()).isEqualTo(3);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanOSonInvalidosLosDatos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEntregableCommand.crear("no-uuid", "tampoco", "   ", null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("codigoError")
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.Entregable.ENTREGABLE_REQUERIDO,
                                        EvaluacionesCodes.Entregable.PROYECTO_GRADO_REQUERIDO,
                                        EvaluacionesCodes.Entregable.PROYECTO_REQUERIDO,
                                        EvaluacionesCodes.Entregable.VERSION_REQUERIDA));
    }
}
