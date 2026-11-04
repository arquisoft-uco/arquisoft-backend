package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoEnUsoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.UsoVersionRepositorioArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioArtefactoSinUsoRuleImplTest {

    private final VersionRepositorioArtefactoSinUsoRuleImpl regla = new VersionRepositorioArtefactoSinUsoRuleImpl();

    @Test
    void debeNoLanzar_cuandoNoEstaEnUso() {
        // Arrange
        var uso = new UsoVersionRepositorioArtefacto(crearVersion(UtilUUID.generarNuevoUUID()), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(uso)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEnUso_cuandoEstaEnUso() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var uso = new UsoVersionRepositorioArtefacto(crearVersion(id), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(uso))
                .isInstanceOf(VersionRepositorioArtefactoEnUsoException.class)
                .satisfies(ex -> {
                    var excepcion = (VersionRepositorioArtefactoEnUsoException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.EN_USO);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(id.toString());
                    assertThat(excepcion.getError().getDetalle()).contains(id.toString());
                });
    }

    private VersionRepositorioArtefactoDomain crearVersion(UUID id) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                id, "Doc", 3, UtilUUID.generarNuevoUUID(), "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    }
}
