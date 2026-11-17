package com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarEvaluacionCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var command = GenerarEvaluacionCommand.crear(
                entregable.toString(), proyectoGrado.toString(), "Sistema de riego", 2);

        // Assert
        assertThat(command.entregable().entregable()).isEqualTo(entregable);
        assertThat(command.entregable().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.entregable().proyecto()).isEqualTo("Sistema de riego");
        assertThat(command.entregable().version()).isEqualTo(2);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoDatosInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> GenerarEvaluacionCommand.crear("no-uuid", "tampoco", "   ", null))
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
