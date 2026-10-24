package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class MappersWebConsultarVersionesRepositorioArtefactoTest {

    @Test
    void debeMapearCamposSinUriArchivo_cuandoReadModelCompleto() {
        // Arrange
        var readModel = new VersionRepositorioArtefactoReadModel(UtilUUID.generarNuevoUUID(), "Formato A", 2,
                Instant.parse("2026-10-20T10:00:00Z"), "application/pdf", 2048L, "formato.pdf", "a".repeat(64));

        // Act
        var dto = VersionRepositorioArtefactoResponseMapper.toResponse(readModel);

        // Assert
        assertThat(dto.id()).isEqualTo(readModel.id());
        assertThat(dto.nombreDocumento()).isEqualTo("Formato A");
        assertThat(dto.numeroVersion()).isEqualTo(2);
        assertThat(dto.fechaCreacion()).isEqualTo(readModel.fechaCreacion());
        assertThat(dto.tipoContenido()).isEqualTo("application/pdf");
        assertThat(dto.tamanoBytes()).isEqualTo(2048L);
        assertThat(dto.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(dto.hashSha256()).isEqualTo("a".repeat(64));
        assertThat(Arrays.stream(dto.getClass().getRecordComponents()).map(c -> c.getName()))
                .doesNotContain("uriArchivo");
    }

    @Test
    void debeConstruirQuery_cuandoIdValido() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarVersionesRepositorioArtefactoRequestMapper.toQuery(repositorio);

        // Assert
        assertThat(query.repositorioArtefacto()).isEqualTo(repositorio);
    }
}
