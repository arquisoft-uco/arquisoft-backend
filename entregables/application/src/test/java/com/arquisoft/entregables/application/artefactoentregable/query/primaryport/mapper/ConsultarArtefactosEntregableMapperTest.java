package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarArtefactosEntregableMapperTest {

    @Test
    void debeLlevarElEntregableAlCriteria() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var query = ConsultarArtefactosEntregableQuery.crear(entregable);

        // Act
        var criteria = ConsultarArtefactosEntregableMapper.toCriteria(query);

        // Assert
        assertThat(criteria.entregableProyectoGrado()).isEqualTo(entregable);
    }
}
