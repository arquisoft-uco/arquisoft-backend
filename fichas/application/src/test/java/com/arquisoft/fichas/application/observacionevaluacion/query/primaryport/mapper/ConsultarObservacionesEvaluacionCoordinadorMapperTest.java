package com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.query.primaryport.model.ConsultarObservacionesEvaluacionCoordinadorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesEvaluacionCoordinadorMapperTest {

    @Test
    void debeConstruirCriteria_cuandoRecibeQuery() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesEvaluacionCoordinadorQuery.crear(fichaPerfil);

        // Act
        var criteria = ConsultarObservacionesEvaluacionCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.fichaPerfil()).isEqualTo(fichaPerfil);
    }
}
