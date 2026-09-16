package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionItemJuradoCommandTest {

    @Test
    void debeCrearYNormalizarCommandValido_cuandoDatosValidos() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var jurado = UUID.randomUUID();

        // Act
        var command = RegistrarObservacionItemJuradoCommand.crear(
                evaluacionCuantitativaJurado, "  Sustenta el puntaje otorgado  ", jurado.toString());

        // Assert
        assertThat(command.evaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(command.descripcion()).isEqualTo("Sustenta el puntaje otorgado");
        assertThat(command.jurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoCamposRequeridosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionItemJuradoCommand.crear(null, " ", " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemJurado
                                                .EVALUACION_CUANTITATIVA_JURADO_REQUERIDA,
                                        EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_REQUERIDA,
                                        EvaluacionesCodes.ObservacionItemJurado.JURADO_REQUERIDO));
    }

    @Test
    void debeAcumularErrorJurado_cuandoSubjectNoEsUuidValido() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionItemJuradoCommand.crear(
                UUID.randomUUID(), "Sustenta el puntaje otorgado", "no-es-un-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemJurado.JURADO_REQUERIDO));
    }

    @Test
    void debeAcumularErrorDescripcionDemasiadoLarga_cuandoExcedeQuinientosCaracteres() {
        // Arrange
        var descripcionLarga = "a".repeat(501);

        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionItemJuradoCommand.crear(
                UUID.randomUUID(), descripcionLarga, UUID.randomUUID().toString()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemJurado.DESCRIPCION_DEMASIADO_LARGA));
    }
}
