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
                Instant.parse("2026-10-20T10:00:00Z"), "artefactos/v4.pdf", "Proyecto Uno", "Documento Uno", 1, "repo-uno", "Asesor Uno",
                UtilUUID.generarNuevoUUID(), "RESUELTA", "Resuelta");

        // Act
        var response = VersionArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.id()).isEqualTo(readModel.id());
        assertThat(response.artefacto()).isEqualTo(readModel.artefacto());
        assertThat(response.version()).isEqualTo(4);
        assertThat(response.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(response.uriArchivo()).isEqualTo("artefactos/v4.pdf");
        assertThat(response.nombreProyecto()).isEqualTo("Proyecto Uno");
        assertThat(response.nombreDocumento()).isEqualTo("Documento Uno");
        assertThat(response.versionDocumento()).isEqualTo(1);
        assertThat(response.nombreRepositorio()).isEqualTo("repo-uno");
        assertThat(response.nombreAsesor()).isEqualTo("Asesor Uno");
        assertThat(response.revisionAsesor()).isEqualTo(readModel.revisionAsesor());
        assertThat(response.estadoRevision()).isEqualTo("RESUELTA");
        assertThat(response.nombreEstadoRevision()).isEqualTo("Resuelta");
    }

    @Test
    void debeMapearRevisionComoNula_cuandoLaVersionNoTieneRevision() {
        // Arrange
        var readModel = new VersionArtefactoReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 1,
                Instant.parse("2026-10-20T10:00:00Z"), "artefactos/v1.pdf", "Proyecto Uno", "Documento Uno", 1,
                "repo-uno", "Asesor Uno", null, null, null);

        // Act
        var response = VersionArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(response.revisionAsesor()).isNull();
        assertThat(response.estadoRevision()).isNull();
        assertThat(response.nombreEstadoRevision()).isNull();
    }
}
