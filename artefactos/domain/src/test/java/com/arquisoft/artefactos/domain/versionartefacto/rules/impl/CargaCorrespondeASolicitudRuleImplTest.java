package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaNoCorrespondeException;
import com.arquisoft.artefactos.domain.versionartefacto.model.CorrespondenciaCarga;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CargaCorrespondeASolicitudRuleImplTest {

    private final CargaCorrespondeASolicitudRuleImpl regla = new CargaCorrespondeASolicitudRuleImpl();
    private final UUID artefacto = UtilUUID.generarNuevoUUID();
    private final UUID solicitante = UtilUUID.generarNuevoUUID();
    private final CargaPendienteDomain carga = CargaPendienteDomain.reconstruir(
            UtilUUID.generarNuevoUUID(), artefacto, solicitante, "a.pdf", "application/pdf", 10L, "etag");

    @Test
    void debePasar_cuandoCoincide() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new CorrespondenciaCarga(carga, solicitante, artefacto)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoSolicitanteDistinto() {
        // Arrange
        var correspondencia = new CorrespondenciaCarga(carga, UtilUUID.generarNuevoUUID(), artefacto);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(correspondencia))
                .isInstanceOf(CargaNoCorrespondeException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.CARGA_NO_CORRESPONDE);
    }

    @Test
    void debeLanzar_cuandoArtefactoDistinto() {
        // Arrange
        var correspondencia = new CorrespondenciaCarga(carga, solicitante, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(correspondencia)).isInstanceOf(CargaNoCorrespondeException.class);
    }
}
