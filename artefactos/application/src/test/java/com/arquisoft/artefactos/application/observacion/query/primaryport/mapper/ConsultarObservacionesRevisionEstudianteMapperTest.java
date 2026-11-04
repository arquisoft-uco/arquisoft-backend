package com.arquisoft.artefactos.application.observacion.query.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionEstudianteQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesRevisionEstudianteMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesRevisionEstudianteQuery.crear(revision, estudiante);

        // Act
        var criteria = ConsultarObservacionesRevisionEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.revisionAsesor()).isEqualTo(revision);
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
