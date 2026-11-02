package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.RevisionAsesorEstudianteJpaQueryEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
                .fechaCreacion(Instant.parse("2026-10-21T10:00:00Z"))
                .nombreProyecto("Proyecto Uno")
                .nombreDocumento("Documento Uno")
                .versionDocumento(2)
                .observacionesAbiertas(1)
                .totalObservaciones(4)
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
        assertThat(readModel.fechaCreacion()).isEqualTo(Instant.parse("2026-10-21T10:00:00Z"));
        assertThat(readModel.nombreProyecto()).isEqualTo("Proyecto Uno");
        assertThat(readModel.nombreDocumento()).isEqualTo("Documento Uno");
        assertThat(readModel.versionDocumento()).isEqualTo(2);
        assertThat(readModel.observacionesAbiertas()).isEqualTo(1);
        assertThat(readModel.totalObservaciones()).isEqualTo(4);
    }
}
