package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCuantitativasAsesorEstudianteMapperTest {

    @Test
    void debeMapearCriteria_cuandoQueryValida() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery.crear(evaluacionAsesor, estudiante);

        // Act
        var criteria = ConsultarEvaluacionesCuantitativasAsesorEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
