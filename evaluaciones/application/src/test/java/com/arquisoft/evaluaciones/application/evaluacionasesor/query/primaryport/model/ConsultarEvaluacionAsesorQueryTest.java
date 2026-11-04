package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionAsesorQueryTest {

    @Test
    void debeCrear_cuandoLaEvaluacionEsValida() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionAsesorQuery.crear(evaluacion);

        // Assert
        assertThat(query.evaluacion()).isEqualTo(evaluacion);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoLaEvaluacionEsNula() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionAsesorQuery.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(Tuple.tuple(
                                        EvaluacionesFields.EvaluacionAsesor.EVALUACION,
                                        EvaluacionesCodes.EvaluacionAsesor.EVALUACION_REQUERIDO)));
    }
}
