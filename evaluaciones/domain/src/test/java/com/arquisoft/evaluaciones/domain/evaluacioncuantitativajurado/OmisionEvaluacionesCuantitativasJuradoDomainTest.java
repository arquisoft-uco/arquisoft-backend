package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class OmisionEvaluacionesCuantitativasJuradoDomainTest {

    @Test
    void debeCrearOmision_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var primera = UUID.randomUUID();
        var segunda = UUID.randomUUID();

        // Act
        var omision = OmisionEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, List.of(primera, segunda));

        // Assert
        assertThat(omision.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(omision.getEvaluaciones()).containsExactlyInAnyOrder(primera, segunda);
    }

    @Test
    void debeRechazarEvaluacionJurado_cuandoEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasJuradoDomain.crear(
                null, List.of(UUID.randomUUID())))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoLaListaEstaVacia() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasJuradoDomain.crear(UUID.randomUUID(), List.of()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCuantitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCuantitativasJurado.LOTE_VACIO)));
    }

    @Test
    void debeRechazarEvaluacionesRepetidas_cuandoUnIdApareceDosVeces() {
        // Arrange
        var repetida = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasJuradoDomain.crear(
                UUID.randomUUID(), List.of(repetida, UUID.randomUUID(), repetida)))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCuantitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCuantitativasJurado
                                                        .EVALUACIONES_REPETIDAS)));
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionJuradoEsNulaYLaListaEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasJuradoDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.OmisionEvaluacionesCuantitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.OmisionEvaluacionesCuantitativasJurado.LOTE_VACIO)));
    }
}
