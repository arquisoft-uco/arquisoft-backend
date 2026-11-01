package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository.RevisionAsesorAsesorJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorAsesorQueryMapperTest {

    @Test
    void debeMapearCampoACampo_cuandoConvierteEntidadAReadModel() {
        // Arrange
        var entity = RevisionAsesorAsesorJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .versionArtefactoId(UtilUUID.generarNuevoUUID())
                .artefactoId(UtilUUID.generarNuevoUUID())
                .version(3)
                .asesorId(UtilUUID.generarNuevoUUID())
                .estadoRevisionAsesorId("RESUELTA")
                .estadoRevisionAsesorNombre("Resuelta")
                .fechaCreacion(Instant.parse("2026-10-01T10:00:00Z"))
                .build();

        // Act
        var readModel = RevisionAsesorAsesorQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.versionArtefacto()).isEqualTo(entity.getVersionArtefactoId());
        assertThat(readModel.artefacto()).isEqualTo(entity.getArtefactoId());
        assertThat(readModel.version()).isEqualTo(3);
        assertThat(readModel.estadoRevisionAsesor()).isEqualTo("RESUELTA");
        assertThat(readModel.estadoRevisionAsesorNombre()).isEqualTo("Resuelta");
        assertThat(readModel.fechaCreacion()).isEqualTo(entity.getFechaCreacion());
    }
}
