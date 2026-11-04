package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionAsesorRealizadaQueryTest {

    @Test
    void debeCrearQuery_cuandoLosDatosSonValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarEvaluacionAsesorRealizadaQuery.crear(evaluacionAsesor, asesor);

        // Assert
        assertThat(query.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(query.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularTodosLosErroresDeEntrada_cuandoAmbosDatosSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionAsesorRealizadaQuery.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionAsesor.ASESOR,
                                                EvaluacionesCodes.EvaluacionAsesor.ASESOR_REQUERIDO)));
    }
}
