package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarVersionesRepositorioArtefactoMapperTest {

    @Test
    void debeMapearRepositorioAlCriteria_cuandoQueryValida() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var query = ConsultarVersionesRepositorioArtefactoQuery.crear(repositorio);

        // Act
        var criteria = ConsultarVersionesRepositorioArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.repositorioArtefacto()).isEqualTo(repositorio);
    }
}
