package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarCoordinadorProyectoGradoCommandTest {

    @Test
    void debeAcumularTodosLosErroresDeEntrada_cuandoLosDatosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarCoordinadorProyectoGradoCommand.crear("no-uuid", null, null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesFields.CoordinadorProyectoGrado.PROYECTO_GRADO,
                                        EvaluacionesFields.CoordinadorProyectoGrado.COORDINADOR,
                                        EvaluacionesFields.CoordinadorProyectoGrado.NOMBRE,
                                        EvaluacionesFields.CoordinadorProyectoGrado.EMAIL,
                                        EvaluacionesFields.CoordinadorProyectoGrado.OCURRIDO_EN));
    }
}
