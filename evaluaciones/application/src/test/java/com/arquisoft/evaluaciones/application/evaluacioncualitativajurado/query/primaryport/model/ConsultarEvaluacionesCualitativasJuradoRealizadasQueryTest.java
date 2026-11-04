package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCualitativasJuradoRealizadasQueryTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesCualitativasJuradoRealizadasQuery.crear(evaluacionJurado, jurado);

        // Assert
        assertThat(query.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(query.jurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularErrores_cuandoAmbosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCualitativasJuradoRealizadasQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaJurado.JURADO,
                                                EvaluacionesCodes.EvaluacionCualitativaJurado.JURADO_REQUERIDO)));
    }
}
