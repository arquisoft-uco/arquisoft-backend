package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class VersionArtefactoResponseMapperTest {

    @Test
    void debeMapearCampoACampo_incluidoUriArchivo() {
        // Arrange
        var readModel = new VersionArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 4,
                Instant.parse("2026-10-20T10:00:00Z"), "artefactos/v4.pdf", "Proyecto Uno", "Documento Uno", 1, "repo-uno", "Asesor Uno");

        // Act
        var response = VersionArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(readModel.id());
        assertThat(response.artefacto()).isEqualTo(readModel.artefacto());
        assertThat(response.version()).isEqualTo(4);
        assertThat(response.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(response.uriArchivo()).isEqualTo("artefactos/v4.pdf", "Proyecto Uno", "Documento Uno", 1, "repo-uno", "Asesor Uno");
    }
}
