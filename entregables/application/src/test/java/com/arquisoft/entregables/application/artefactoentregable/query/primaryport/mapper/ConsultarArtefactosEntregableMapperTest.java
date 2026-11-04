package com.arquisoft.entregables.application.artefactoentregable.query.primaryport.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarArtefactosEntregableMapperTest {

    @Test
    void debeLlevarEntregableYAsesorAlCriteria() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarArtefactosEntregableQuery.crear(entregable, asesor);

        // Act
        var criteria = ConsultarArtefactosEntregableMapper.toCriteria(query);

        // Assert
        assertThat(criteria.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(criteria.asesor()).isEqualTo(asesor);
    }
}
