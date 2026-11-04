package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCualitativasAsesorMapperTest {

    @Test
    void debeConservarEvaluacionYAsesor_alConvertirQueryACriteria() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCualitativasAsesorQuery.crear(evaluacionAsesor, asesor);

        // Act
        var criteria = ConsultarEvaluacionesCualitativasAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(criteria.asesor()).isEqualTo(asesor);
    }
}
