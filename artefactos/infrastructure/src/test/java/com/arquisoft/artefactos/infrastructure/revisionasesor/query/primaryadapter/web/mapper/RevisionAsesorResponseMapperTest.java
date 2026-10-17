package com.arquisoft.artefactos.infrastructure.revisionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.readmodel.RevisionAsesorReadModel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorResponseMapperTest {

    @Test
    void debeMapearCampoACampo_cuandoConvierteReadModelAResponse() {
        // Arrange
        var readModel = new RevisionAsesorReadModel(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 4, "CERRADA", "Cerrada");

        // Act
        var response = RevisionAsesorResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(readModel.id());
        assertThat(response.versionArtefacto()).isEqualTo(readModel.versionArtefacto());
        assertThat(response.artefacto()).isEqualTo(readModel.artefacto());
        assertThat(response.version()).isEqualTo(4);
        assertThat(response.estadoRevisionAsesor()).isEqualTo("CERRADA");
        assertThat(response.estadoRevisionAsesorNombre()).isEqualTo("Cerrada");
    }
}
