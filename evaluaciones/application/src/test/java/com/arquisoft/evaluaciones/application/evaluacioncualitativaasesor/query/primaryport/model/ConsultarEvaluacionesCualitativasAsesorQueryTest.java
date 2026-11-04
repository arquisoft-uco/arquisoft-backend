package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCualitativasAsesorQueryTest {

    @Test
    void debeCrearQuery_cuandoLosDatosSonValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionesCualitativasAsesorQuery.crear(evaluacionAsesor, asesor);

        // Assert
        assertThat(query.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(query.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularTodosLosErroresDeEntrada_cuandoAmbosDatosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesCualitativasAsesorQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO)));
    }
}
