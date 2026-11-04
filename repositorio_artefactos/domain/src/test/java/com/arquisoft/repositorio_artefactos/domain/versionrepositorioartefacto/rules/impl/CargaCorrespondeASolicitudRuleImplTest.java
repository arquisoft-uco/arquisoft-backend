package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.CorrespondenciaCarga;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CargaCorrespondeASolicitudRuleImplTest {

    private final CargaCorrespondeASolicitudRuleImpl regla = new CargaCorrespondeASolicitudRuleImpl();
    private final UUID repositorio = UtilUUID.generarNuevoUUID();
    private final UUID solicitante = UtilUUID.generarNuevoUUID();
    private final CargaPendienteDomain carga = CargaPendienteDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), repositorio, solicitante, "a.pdf", "application/pdf", 10L, "etag");

    @Test
    void debePasar_cuandoCoincide() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new CorrespondenciaCarga(carga, solicitante, repositorio)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoSolicitanteDistinto() {
        // Arrange
        var correspondencia = new CorrespondenciaCarga(carga, UtilUUID.generarNuevoUUID(), repositorio);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(correspondencia))
                .isInstanceOf(CargaNoCorrespondeException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_NO_CORRESPONDE);
    }

    @Test
    void debeLanzar_cuandoRepositorioDistinto() {
        // Arrange
        var correspondencia = new CorrespondenciaCarga(carga, solicitante, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(correspondencia)).isInstanceOf(CargaNoCorrespondeException.class);
    }
}
