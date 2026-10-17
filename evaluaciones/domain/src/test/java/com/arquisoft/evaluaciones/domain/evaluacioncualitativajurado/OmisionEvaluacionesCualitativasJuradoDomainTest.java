package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class OmisionEvaluacionesCualitativasJuradoDomainTest {

    @Test
    void debeCrearOmision_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();

        // Act
        var omision = OmisionEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, List.of(primera, segunda));

        // Assert
        assertThat(omision.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(omision.getEvaluaciones()).containsExactlyInAnyOrder(primera, segunda);
    }

    @Test
    void debeRechazarEvaluacionJurado_cuandoEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasJuradoDomain.crear(
                null, List.of(UUID.randomUUID())))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoLaListaEstaVacia() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasJuradoDomain.crear(UUID.randomUUID(), List.of()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.LOTE_VACIO)));
    }

    @Test
    void debeRechazarEvaluacionesRepetidas_cuandoUnIdApareceDosVeces() {
        // Arrange
        var repetida = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasJuradoDomain.crear(
                UUID.randomUUID(), List.of(repetida, UUID.randomUUID(), repetida)))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado
                                                        .EVALUACIONES_REPETIDAS)));
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionJuradoEsNulaYLaListaEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasJuradoDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCualitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasJurado.LOTE_VACIO)));
    }
}
