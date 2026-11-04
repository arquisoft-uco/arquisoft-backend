package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.event;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoSubidaEventTest {

    private static VersionRepositorioArtefactoDomain version() {
        var subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato A", false);
        var carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", "application/pdf", 2048L, "etag");
        var analisis = AnalisisArchivoDomain.reconstruir("application/pdf", "a".repeat(64), "etag");
        return VersionRepositorioArtefactoDomain.crear(subida, new UltimaVersionDocumento("Formato A", 2), carga, analisis);
    }

    @Test
    void debeUsarElTopicDeEventTopics() {
        // Act
        var evento = new VersionRepositorioArtefactoSubidaEvent(version(), "Repo");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.RepositorioArtefactos.VERSION_REPOSITORIO_ARTEFACTO_SUBIDA);
        assertThat(evento.getTipoEvento()).isEqualTo("VersionRepositorioArtefactoSubidaEvent");
    }

    @Test
    void debeExponerLosOchoCamposDeLaVersion() {
        // Arrange
        var version = version();

        // Act
        var evento = new VersionRepositorioArtefactoSubidaEvent(version, "Repo");

        // Assert
        assertThat(evento.getVersionRepositorioArtefacto()).isEqualTo(version.getId());
        assertThat(evento.getNombreDocumento()).isEqualTo("Formato A");
        assertThat(evento.getVersionDocumento()).isEqualTo(3);
        assertThat(evento.getRepositorioArtefacto()).isEqualTo(version.getRepositorioArtefacto());
        assertThat(evento.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(evento.getNombreArchivo()).isEqualTo("formato.pdf");
        assertThat(evento.getTipoContenido()).isEqualTo("application/pdf");
        assertThat(evento.getTamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void debeNoExponerKeyNiHash() {
        // Act
        var campos = Arrays.stream(VersionRepositorioArtefactoSubidaEvent.class.getDeclaredFields())
                .map(campo -> campo.getName().toLowerCase())
                .toList();

        // Assert
        assertThat(campos).noneMatch(nombre -> nombre.contains("uri") || nombre.contains("hash"));
    }
}
