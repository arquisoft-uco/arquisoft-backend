package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EvaluacionCualitativaJuradoDomainTest {

    @Test
    void debeCrearEvaluacionCualitativa_cuandoDatosValidos() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID item = UUID.randomUUID();
        UUID criterio = UUID.randomUUID();

        // Act
        EvaluacionCualitativaJuradoDomain evaluacion =
                EvaluacionCualitativaJuradoDomain.crear(evaluacionJurado, item, criterio);

        // Assert
        assertThat(evaluacion.getId()).isNotNull();
        assertThat(evaluacion.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(evaluacion.getItem()).isEqualTo(item);
        assertThat(evaluacion.getCriterio()).isEqualTo(criterio);
        assertThat(evaluacion.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosTresIdentificadoresSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionCualitativaJuradoDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.ITEM,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.ITEM_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.CRITERIO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.CRITERIO_REQUERIDO)));
    }

    @Test
    void debeReconstruirSinValidar_cuandoSeCargaDesdePersistencia() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        EvaluacionCualitativaJuradoDomain evaluacion =
                EvaluacionCualitativaJuradoDomain.reconstruir(id, null, null, null);

        // Assert
        assertThat(evaluacion.getId()).isEqualTo(id);
        assertThat(evaluacion.getEvaluacionJurado()).isNull();
        assertThat(evaluacion.getItem()).isNull();
        assertThat(evaluacion.getCriterio()).isNull();
    }

    @Test
    void debeIdentificarseComoVacio_cuandoEsLaConstanteVacio() {
        // Act & Assert
        assertThat(EvaluacionCualitativaJuradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EvaluacionCualitativaJuradoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()).esVacio()).isFalse();
    }
}
