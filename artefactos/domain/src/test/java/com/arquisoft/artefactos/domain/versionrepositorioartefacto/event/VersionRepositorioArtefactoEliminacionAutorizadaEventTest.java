package com.arquisoft.artefactos.domain.versionrepositorioartefacto.event;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoEliminacionAutorizadaEventTest {

    @Test
    void debeCargarLosDatosDeLaVersion_cuandoSeConstruye() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var version = VersionRepositorioArtefactoDomain.reconstruir(
                id, "Documento", 4, repositorio, "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));

        // Act
        var evento = new VersionRepositorioArtefactoEliminacionAutorizadaEvent(version);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Artefactos.VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA);
        assertThat(evento.getVersionRepositorioArtefacto()).isEqualTo(id);
        assertThat(evento.getNombreDocumento()).isEqualTo("Documento");
        assertThat(evento.getVersionDocumento()).isEqualTo(4);
        assertThat(evento.getRepositorioArtefacto()).isEqualTo(repositorio);
    }
}
