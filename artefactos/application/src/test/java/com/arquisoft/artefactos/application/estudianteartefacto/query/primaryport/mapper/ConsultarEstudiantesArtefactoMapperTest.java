package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarEstudiantesArtefactoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstudiantesArtefactoMapperTest {

    @Test
    void debeConstruirCriteria_cuandoQueryValida() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var query = ConsultarEstudiantesArtefactoQuery.crear(artefacto);

        // Act
        var criteria = ConsultarEstudiantesArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.artefacto()).isEqualTo(artefacto);
    }
}
