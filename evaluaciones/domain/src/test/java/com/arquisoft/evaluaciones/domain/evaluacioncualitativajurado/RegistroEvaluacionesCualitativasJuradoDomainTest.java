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

class RegistroEvaluacionesCualitativasJuradoDomainTest {

    @Test
    void debeCrearRegistro_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var evaluaciones = List.of(
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, UUID.randomUUID(), UUID.randomUUID()),
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, UUID.randomUUID(), UUID.randomUUID()));

        // Act
        var registro = RegistroEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, evaluaciones);

        // Assert
        assertThat(registro.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(registro.getEvaluaciones()).hasSize(2);
    }

    @Test
    void debeRechazarEvaluacionJurado_cuandoEsNula() {
        // Arrange
        var evaluaciones = List.of(
                EvaluacionCualitativaJuradoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(null, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoListaEvaluacionesEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(UUID.randomUUID(), null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_VACIO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoListaEvaluacionesEstaVacia() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(UUID.randomUUID(), List.of()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.LOTE_VACIO));
    }

    @Test
    void debeRechazarItemsRepetidos_cuandoDosParesComparteElMismoItemConDistintoCriterio() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var itemRepetido = UUID.randomUUID();
        var evaluaciones = List.of(
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, itemRepetido, UUID.randomUUID()),
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, itemRepetido, UUID.randomUUID()));

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.ITEMS_REPETIDOS));
    }
}
