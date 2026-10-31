package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.RevisionVersionSiguienteRegistradaRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteNoRegistradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionVersionSiguienteRegistradaRuleImplTest {

    private final RevisionVersionSiguienteRegistradaRuleImpl regla = new RevisionVersionSiguienteRegistradaRuleImpl();

    private static SituacionReaperturaObservacion situacion(
            EstadoObservacion destino, int version, int ultima, RevisionAsesorDomain siguiente) {
        return new SituacionReaperturaObservacion(
                UtilUUID.generarNuevoUUID(), destino, EstadoRevisionAsesor.RESUELTA, version, ultima, siguiente);
    }

    private static RevisionAsesorDomain revision() {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.PENDIENTE,
                UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoDestinoEsCerrada() {
        // Arrange
        var entrada = situacion(EstadoObservacion.CERRADA, 1, 2, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoHayVersionSiguienteConRevision() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, 1, 2, revision());

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoSeReabreEnSitio() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, 2, 2, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaVersionSiguienteNoTieneRevision() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, 1, 2, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionVersionSiguienteNoRegistradaException.class, ex -> {
                    var id = entrada.observacion().toString();
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.RevisionAsesor.VERSION_SIGUIENTE_NO_REGISTRADA);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(id, "??");
                    assertThat(ex.getMessage()).contains(id);
                });
    }
}
