package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCuantitativasJuradoRealizadasQueryTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery.crear(evaluacionJurado, jurado);

        // Assert
        assertThat(query.evaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(query.jurado()).isEqualTo(jurado);
    }

    @Test
    void debeAcumularErrores_cuandoAmbosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO)));
    }
}
