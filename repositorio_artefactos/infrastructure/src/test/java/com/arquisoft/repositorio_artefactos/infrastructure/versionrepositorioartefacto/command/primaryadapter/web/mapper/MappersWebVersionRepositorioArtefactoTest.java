package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionRepositorioArtefactoRequestDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SubirVersionRepositorioArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MappersWebVersionRepositorioArtefactoTest {

    @Test
    void solicitarDebeConstruirElCommand() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var dto = new SolicitarCargaVersionRepositorioArtefactoRequestDTO("formato.pdf", "application/pdf", 10L);

        // Act
        var command = SolicitarCargaVersionRepositorioArtefactoRequestMapper.toCommand(
                dto, repositorio, solicitante);

        // Assert
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.solicitante()).isEqualTo(solicitante);
        assertThat(command.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(command.tipoContenido()).isEqualTo("application/pdf");
        assertThat(command.tamanoBytes()).isEqualTo(10L);
    }

    @Test
    void subirDebeConstruirElCommand() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var dto = new SubirVersionRepositorioArtefactoRequestDTO(carga.toString(), "Formato A", true);

        // Act
        var command = SubirVersionRepositorioArtefactoRequestMapper.toCommand(dto, repositorio, solicitante);

        // Assert
        assertThat(command.carga()).isEqualTo(carga);
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.solicitante()).isEqualTo(solicitante);
        assertThat(command.nombreDocumento()).isEqualTo("Formato A");
        assertThat(command.nuevoDocumento()).isTrue();
    }

    @Test
    void solicitarDebeConstruirLaRespuesta() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var expiraEn = Instant.now();

        // Act
        var response = SolicitarCargaVersionRepositorioArtefactoResponseMapper.toResponse(
                new CargaVersionRepositorioArtefactoResult(carga, "http://minio/b", Map.of("k", "v"), expiraEn));

        // Assert
        assertThat(response.carga()).isEqualTo(carga);
        assertThat(response.url()).isEqualTo("http://minio/b");
        assertThat(response.campos()).containsEntry("k", "v");
        assertThat(response.expiraEn()).isEqualTo(expiraEn);
    }

    @Test
    void subirDebeConstruirLaRespuesta() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var response = SubirVersionRepositorioArtefactoResponseMapper.toResponse(
                new VersionRepositorioArtefactoSubidaResult(id, 4));

        // Assert
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.numeroVersion()).isEqualTo(4);
    }
}
