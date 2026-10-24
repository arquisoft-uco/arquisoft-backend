package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionesRepositorioArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionesRepositorioArtefactoExistenRuleImplTest {

    private final VersionesRepositorioArtefactoExistenRuleImpl regla =
            new VersionesRepositorioArtefactoExistenRuleImpl();

    private static VersionRepositorioArtefactoDomain version(UUID id) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                id, "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoTodasLasVersionesExisten() {
        // Arrange
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaVersionesRepositorioArtefacto(
                List.of(primera, segunda), List.of(version(segunda), version(primera)));

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoFaltaUnaVersion() {
        // Arrange
        var presente = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaVersionesRepositorioArtefacto(
                List.of(presente, ausente), List.of(version(presente)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.VERSIONES_NO_ENCONTRADAS);
    }
}
