package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarObservacionesItemJuradoQueryTest {

    private static final ConsultaCriteriaQuery CRITERIO = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

    @Test
    void debeCrearQuery_cuandoLaEvaluacionCuantitativaEsValida() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();

        // Act
        var query = ConsultarObservacionesItemJuradoQuery.crear(evaluacionCuantitativaJurado, CRITERIO);

        // Assert
        assertThat(query.evaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(query.criterio()).isSameAs(CRITERIO);
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoLaEvaluacionCuantitativaEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemJuradoQuery.crear(null, CRITERIO))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO,
                                                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO_REQUERIDA)));
    }
}
