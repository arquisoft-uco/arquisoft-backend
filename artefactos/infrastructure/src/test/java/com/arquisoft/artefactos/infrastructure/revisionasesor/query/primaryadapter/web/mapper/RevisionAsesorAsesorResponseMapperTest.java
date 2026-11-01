package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorAsesorReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorAsesorResponseMapperTest {

    @Test
    void debeMapearTodosLosCampos_cuandoConvierteReadModelADTO() {
        // Arrange
        var readModel = new RevisionAsesorAsesorReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                4, "CERRADA", "Cerrada", Instant.parse("2026-10-01T10:00:00Z"));

        // Act
        var response = RevisionAsesorAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(readModel.id());
        assertThat(response.versionArtefacto()).isEqualTo(readModel.versionArtefacto());
        assertThat(response.artefacto()).isEqualTo(readModel.artefacto());
        assertThat(response.version()).isEqualTo(4);
        assertThat(response.estadoRevisionAsesor()).isEqualTo("CERRADA");
        assertThat(response.estadoRevisionAsesorNombre()).isEqualTo("Cerrada");
        assertThat(response.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
    }
}
