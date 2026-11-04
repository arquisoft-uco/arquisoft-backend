package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository.ArtefactoSinAvanceJpaQueryEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoSinAvanceQueryMapperTest {

    @Test
    void debeCalcularDiasSinAvanceYMapearCampos_cuandoHayFechaUltimaVersion() {
        // Arrange
        var fecha = Instant.now().minus(45, ChronoUnit.DAYS).minusSeconds(60);
        var entity = ArtefactoSinAvanceJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .proyectoGradoId(UtilUUID.generarNuevoUUID())
                .nombreProyecto("Alfa")
                .versionRepositorioArtefactoId(UtilUUID.generarNuevoUUID())
                .nombreDocumento("Informe")
                .versionDocumento(2)
                .nombreRepositorio("Repo")
                .fechaUltimaVersion(fecha)
                .estadoUltimaRevision("Pendiente")
                .build();

        // Act
        var readModel = ArtefactoSinAvanceQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.diasSinAvance()).isEqualTo(45L);
        assertThat(readModel.fechaUltimaVersion()).isEqualTo(fecha);
        assertThat(readModel.id()).isEqualTo(entity.getId());
        assertThat(readModel.proyectoGrado()).isEqualTo(entity.getProyectoGradoId());
        assertThat(readModel.versionRepositorioArtefacto()).isEqualTo(entity.getVersionRepositorioArtefactoId());
        assertThat(readModel.nombreDocumento()).isEqualTo("Informe");
        assertThat(readModel.estadoUltimaRevision()).isEqualTo("Pendiente");
    }

    @Test
    void debeDejarDiasSinAvanceNulo_cuandoNoHayFechaUltimaVersion() {
        // Arrange
        var entity = ArtefactoSinAvanceJpaQueryEntity.builder().id(UtilUUID.generarNuevoUUID()).build();

        // Act
        var readModel = ArtefactoSinAvanceQueryMapper.toReadModel(entity);

        // Assert
        assertThat(readModel.fechaUltimaVersion()).isNull();
        assertThat(readModel.diasSinAvance()).isNull();
    }
}
