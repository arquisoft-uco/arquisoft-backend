package com.arquisoft.artefactos.application.observacion.query.primaryport.mapper;

import com.arquisoft.artefactos.application.observacion.query.primaryport.model.ConsultarObservacionesRevisionQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesRevisionMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var query = ConsultarObservacionesRevisionQuery.crear(revision);

        // Act
        var criteria = ConsultarObservacionesRevisionMapper.toCriteria(query);

        // Assert
        assertThat(criteria.revisionAsesor()).isEqualTo(revision);
    }
}
