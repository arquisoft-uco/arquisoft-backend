package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarPuntajeEvaluacionCuantitativaJuradoCommandTest {

    @Test
    void debeCrearYNormalizarCommandValido() {
        // Arrange
        UUID evaluacionCuantitativaJurado = UUID.randomUUID();
        UUID jurado = UUID.randomUUID();

        // Act
        var command = CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(
                evaluacionCuantitativaJurado, 350, jurado);

        // Assert
        assertThat(command.evaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(command.nuevoPuntaje()).isEqualTo(350);
        assertThat(command.jurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoCamposRequeridosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.ID_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO));
    }

    @Test
    void debeAceptarPuntaje_cuandoLlegaComoDecimalSinParteFraccionaria() {
        // Act
        var command = CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(
                UUID.randomUUID(), 350.0, UUID.randomUUID());

        // Assert
        assertThat(command.nuevoPuntaje()).isEqualTo(350);
    }

    @Test
    void debeRechazarPuntaje_cuandoTieneParteDecimal() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(
                UUID.randomUUID(), 320.7, UUID.randomUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_NO_ENTERO));
    }

    @Test
    void debeRechazarPuntaje_cuandoDesbordaElRangoEntero() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarPuntajeEvaluacionCuantitativaJuradoCommand.crear(
                UUID.randomUUID(), 99_999_999_999L, UUID.randomUUID()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO));
    }
}
