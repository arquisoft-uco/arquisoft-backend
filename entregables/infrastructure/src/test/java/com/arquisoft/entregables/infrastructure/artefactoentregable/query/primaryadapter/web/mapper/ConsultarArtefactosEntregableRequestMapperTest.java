package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarArtefactosEntregableRequestMapperTest {

    @Test
    void debeConstruirLaQuery_conElEntregableDelPath() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarArtefactosEntregableRequestMapper.toQuery(entregable);

        // Assert
        assertThat(query.entregableProyectoGrado()).isEqualTo(entregable);
    }
}
