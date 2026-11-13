package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarObservacionesItemAsesorQueryTest {

    private static final ConsultaCriteriaQuery CRITERIO = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

    @Test
    void debeCrearQuery_cuandoLosDatosSonValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarObservacionesItemAsesorQuery.crear(evaluacion, asesor, CRITERIO);

        // Assert
        assertThat(query.evaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.criterio()).isSameAs(CRITERIO);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoEvaluacionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemAsesorQuery.crear(null, null, CRITERIO))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR,
                                                EvaluacionesCodes.ObservacionItemAsesor
                                                        .EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA),
                                        tuple(EvaluacionesFields.ObservacionItemAsesor.ASESOR,
                                                EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO)));
    }
}
