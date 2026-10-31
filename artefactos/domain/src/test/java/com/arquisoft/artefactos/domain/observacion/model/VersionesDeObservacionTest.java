package com.arquisoft.artefactos.domain.observacion.model;

import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionesDeObservacionTest {

    private static VersionArtefactoDomain version(int numero) {
        return VersionArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), numero, UtilFecha.VACIO, "uri");
    }

    @Test
    void debeTenerVersionPosterior_cuandoLaUltimaEsMayor() {
        // Arrange
        var versiones = new VersionesDeObservacion(version(1), 2, RevisionAsesorDomain.VACIO, false);

        // Act & Assert
        assertThat(versiones.tieneVersionPosterior()).isTrue();
    }

    @Test
    void noDebeTenerVersionPosterior_cuandoEsLaUltima() {
        // Arrange
        var versiones = new VersionesDeObservacion(version(2), 2, RevisionAsesorDomain.VACIO, false);

        // Act & Assert
        assertThat(versiones.tieneVersionPosterior()).isFalse();
    }
}
