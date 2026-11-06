package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEsUltimaParaEliminarException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.VigenciaVersionRepositorioArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioArtefactoUltimaParaEliminarRuleImplTest {

    private final VersionRepositorioArtefactoUltimaParaEliminarRuleImpl regla =
            new VersionRepositorioArtefactoUltimaParaEliminarRuleImpl();

    @Test
    void debeNoLanzar_cuandoEsLaUltima() {
        // Arrange
        var vigencia = new VigenciaVersionRepositorioArtefacto(crearVersion(UtilUUID.generarNuevoUUID(), 3), 3);

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }

    @Test
    void debeNoLanzar_cuandoEsLaUnica() {
        // Arrange
        var vigencia = new VigenciaVersionRepositorioArtefacto(crearVersion(UtilUUID.generarNuevoUUID(), 1), 1);

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEsUltima_cuandoHayUnaMasReciente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaVersionRepositorioArtefacto(crearVersion(id, 2), 3);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(VersionRepositorioArtefactoNoEsUltimaParaEliminarException.class)
                .satisfies(ex -> {
                    var excepcion = (VersionRepositorioArtefactoNoEsUltimaParaEliminarException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.VersionRepositorioArtefacto.NO_ES_ULTIMA_PARA_ELIMINAR);
                    assertThat(excepcion.getError().getMessage()).contains("3").contains("Formato A");
                    assertThat(excepcion.getError().getMessage()).doesNotContain(id.toString());
                    assertThat(excepcion.getError().getDetalle()).contains(id.toString());
                });
    }

    private VersionRepositorioArtefactoDomain crearVersion(UUID id, int numero) {
        return VersionRepositorioArtefactoDomain.reconstruir(id, "Formato A", numero, UtilUUID.generarNuevoUUID(),
                "Repo", new DatosArchivo("a.pdf", "application/pdf", 10L));
    }
}
