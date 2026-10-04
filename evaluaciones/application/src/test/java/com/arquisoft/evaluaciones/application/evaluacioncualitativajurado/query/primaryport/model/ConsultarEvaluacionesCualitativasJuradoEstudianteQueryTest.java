package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCualitativasJuradoEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoLaEvaluacionJuradoEsValida() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();

        // Act
        ConsultarEvaluacionesCualitativasJuradoEstudianteQuery query =
                ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(evaluacionJurado);

        // Assert
        assertThat(query.evaluacionJurado()).isEqualTo(evaluacionJurado);
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoLaEvaluacionJuradoEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO)));
    }
}
