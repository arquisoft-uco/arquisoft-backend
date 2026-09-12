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
        UUID actor = UUID.randomUUID();
        UUID evaluacionJurado = UUID.randomUUID();
        var evaluaciones = List.of(
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, UUID.randomUUID(), UUID.randomUUID()),
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, UUID.randomUUID(), UUID.randomUUID()));

        // Act
        RegistroEvaluacionesCualitativasJuradoDomain registro =
                RegistroEvaluacionesCualitativasJuradoDomain.crear(actor, evaluaciones);

        // Assert
        assertThat(registro.getActor()).isEqualTo(actor);
        assertThat(registro.getEvaluaciones()).hasSize(2);
    }

    @Test
    void debeAcumularErrores_cuandoActorYEvaluacionesNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.RegistroEvaluacionesCualitativasJurado.ACTOR,
                                                EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.ACTOR_REQUERIDO),
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
        UUID evaluacionJurado = UUID.randomUUID();
        UUID itemRepetido = UUID.randomUUID();
        var evaluaciones = List.of(
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, itemRepetido, UUID.randomUUID()),
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, itemRepetido, UUID.randomUUID()));

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(UUID.randomUUID(), evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.ITEMS_REPETIDOS));
    }

    @Test
    void debeRechazarPadresDistintos_cuandoLasEvaluacionesNoComparteLaMismaEvaluacionDeJurado() {
        // Arrange
        var evaluaciones = List.of(
                EvaluacionCualitativaJuradoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()),
                EvaluacionCualitativaJuradoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasJuradoDomain.crear(UUID.randomUUID(), evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasJurado.PADRES_DISTINTOS));
    }
}
