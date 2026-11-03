package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionAsesorRealizadaQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionAsesorRealizadaMapperTest {

    @Test
    void debeMapearEvaluacionAsesorYAsesor_cuandoSeConvierteACriteria() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionAsesorRealizadaQuery.crear(evaluacionAsesor, asesor);

        // Act
        var criteria = ConsultarEvaluacionAsesorRealizadaMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(criteria.asesor()).isEqualTo(asesor);
    }
}
