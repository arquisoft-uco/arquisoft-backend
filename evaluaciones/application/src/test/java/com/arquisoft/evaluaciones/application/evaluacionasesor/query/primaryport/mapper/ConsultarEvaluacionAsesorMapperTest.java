package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionAsesorMapperTest {

    @Test
    void debeMapearLaEvaluacion_cuandoSeConvierteACriteria() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionAsesorQuery.crear(evaluacion);

        // Act
        var criteria = ConsultarEvaluacionAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacion()).isEqualTo(evaluacion);
    }
}
