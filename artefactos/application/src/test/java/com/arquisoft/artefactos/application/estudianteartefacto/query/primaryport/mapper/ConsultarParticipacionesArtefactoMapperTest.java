package com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarParticipacionesArtefactoMapperTest {

    @Test
    void debeConstruirCriteriaConElEstudiante_cuandoQueryValida() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarParticipacionesArtefactoQuery.crear(estudiante);

        // Act
        var criteria = ConsultarParticipacionesArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.estudiante()).isEqualTo(estudiante);
    }
}
