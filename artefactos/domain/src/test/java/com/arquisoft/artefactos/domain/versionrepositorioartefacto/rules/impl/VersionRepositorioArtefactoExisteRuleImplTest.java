package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioArtefactoExisteRuleImplTest {

    private final VersionRepositorioArtefactoExisteRuleImpl regla = new VersionRepositorioArtefactoExisteRuleImpl();

    @Test
    void debePasar_cuandoLaVersionExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrada = VersionRepositorioArtefactoDomain.reconstruir(
                id, "Doc", 1, UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaVersionRepositorioArtefacto(id, encontrada)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarVersionNoEncontrada_cuandoElResultadoEsVacio() {
        // Arrange
        var existencia = new ExistenciaVersionRepositorioArtefacto(
                UtilUUID.generarNuevoUUID(), VersionRepositorioArtefactoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(VersionRepositorioArtefactoNoEncontradaException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.VERSION_NO_ENCONTRADA);
    }
}
