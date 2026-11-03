package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioArtefactoExistenteRuleImplTest {

    private final VersionRepositorioArtefactoExistenteRuleImpl regla = new VersionRepositorioArtefactoExistenteRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaVersionExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var archivo = ArchivoVersionDomain.reconstruir("versiones/" + version, "formato.pdf");

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaVersionRepositorioArtefacto(version, archivo)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_yNoIncluirUuidEnElMensajeDeCliente_cuandoElArchivoEsVacio() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaVersionRepositorioArtefacto(version, ArchivoVersionDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(VersionRepositorioArtefactoNoEncontradaException.class, excepcion -> {
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.VERSION_NO_ENCONTRADA);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(version.toString());
                    assertThat(excepcion.getError().getDetalle()).contains(version.toString());
                });
    }
}
