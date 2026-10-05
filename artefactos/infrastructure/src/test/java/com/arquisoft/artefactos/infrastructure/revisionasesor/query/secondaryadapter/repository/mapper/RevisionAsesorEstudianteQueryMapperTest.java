package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.RevisionAsesorEstudianteJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorEstudianteQueryMapperTest {

    @Test
    void debeMapearCampoACampo_cuandoConvierteEntidadAReadModel() {
        // Arrange
        var entity = RevisionAsesorEstudianteJpaQueryEntity.builder()
                .id(UUID.randomUUID())
                .versionArtefactoId(UUID.randomUUID())
                .artefactoId(UUID.randomUUID())
                .version(3)
                .estudianteId(UUID.randomUUID())
                .estadoRevisionAsesorId("RESUELTA")
                .estadoRevisionAsesorNombre("Resuelta")
                .build();

        // Act
        var readModel = RevisionAsesorEstudianteQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.versionArtefacto()).isEqualTo(entity.getVersionArtefactoId());
        assertThat(readModel.artefacto()).isEqualTo(entity.getArtefactoId());
        assertThat(readModel.version()).isEqualTo(3);
        assertThat(readModel.estadoRevisionAsesor()).isEqualTo("RESUELTA");
        assertThat(readModel.estadoRevisionAsesorNombre()).isEqualTo("Resuelta");
    }
}
