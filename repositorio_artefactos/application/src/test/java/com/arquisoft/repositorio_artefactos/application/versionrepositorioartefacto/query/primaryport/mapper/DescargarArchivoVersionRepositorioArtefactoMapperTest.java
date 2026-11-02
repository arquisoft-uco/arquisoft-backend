package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.DescargarArchivoVersionRepositorioArtefactoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DescargarArchivoVersionRepositorioArtefactoMapperTest {

    @Test
    void debeConvertirQueryEnCriteria() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var query = DescargarArchivoVersionRepositorioArtefactoQuery.crear(version);

        // Act
        var criteria = DescargarArchivoVersionRepositorioArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.version()).isEqualTo(version);
    }
}
