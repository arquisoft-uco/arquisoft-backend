package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class OmitirEvaluacionesCualitativasJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID().toString();
        var segunda = UUID.randomUUID().toString();

        // Act
        var command = OmitirEvaluacionesCualitativasJuradoCommand.crear(evaluacionJurado, List.of(primera, segunda));

        // Assert
        assertThat(command.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(command.evaluaciones())
                .containsExactly(UUID.fromString(primera), UUID.fromString(segunda));
    }

    @Test
    void debeRechazarEvaluacionJurado_cuandoEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasJuradoCommand.crear(
                null, List.of(UUID.randomUUID().toString())))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoLaListaDeEvaluacionesEstaVacia() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasJuradoCommand.crear(
                UUID.randomUUID(), List.of()))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.LOTE_VACIO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoLaListaDeEvaluacionesEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasJuradoCommand.crear(
                UUID.randomUUID(), null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.LOTE_VACIO)));
    }

    @Test
    void debeAcumularErroresIndexados_cuandoHayIdsEnBlancoOInvalidosYEvaluacionJuradoNula() {
        // Arrange
        var idValido = UUID.randomUUID().toString();
        var evaluaciones = List.of(" ", idValido, "no-es-un-uuid");

        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasJuradoCommand.crear(null, evaluaciones))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple("evaluaciones[0]",
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado
                                                        .EVALUACION_REQUERIDA),
                                        tuple("evaluaciones[2]",
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado
                                                        .EVALUACION_INVALIDA)));
    }
}
