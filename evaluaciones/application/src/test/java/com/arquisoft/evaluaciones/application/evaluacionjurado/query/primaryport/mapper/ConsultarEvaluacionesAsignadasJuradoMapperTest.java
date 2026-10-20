package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesAsignadasJuradoMapperTest {

    @Test
    void debeConstruirCriteriaConElJurado() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(jurado);

        // Act
        var criteria = ConsultarEvaluacionesAsignadasJuradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.jurado()).isEqualTo(jurado);
    }
}
