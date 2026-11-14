package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.primaryport.model.ConsultarEvaluacionesCualitativasAsesorEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCualitativasAsesorEstudianteMapperTest {

    @Test
    void debeMapearCriteria_cuandoQueryValida() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCualitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);

        // Act
        var criteria = ConsultarEvaluacionesCualitativasAsesorEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
