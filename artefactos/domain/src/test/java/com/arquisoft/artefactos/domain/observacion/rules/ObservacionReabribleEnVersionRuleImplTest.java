package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoReabribleException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.ObservacionReabribleEnVersionRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionReabribleEnVersionRuleImplTest {

    private final ObservacionReabribleEnVersionRuleImpl regla = new ObservacionReabribleEnVersionRuleImpl();

    private static SituacionReaperturaObservacion situacion(
            EstadoObservacion destino, EstadoRevisionAsesor estado, int version, int ultima) {
        return new SituacionReaperturaObservacion(
                UtilUUID.generarNuevoUUID(), destino, estado, version, ultima, RevisionAsesorDomain.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoDestinoEsCerradaFueraDeVentana() {
        // Arrange
        var entrada = situacion(EstadoObservacion.CERRADA, EstadoRevisionAsesor.RESUELTA, 1, 5);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoEsLaUltimaVersionYRevisionEnProgreso() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, EstadoRevisionAsesor.EN_PROGRESO, 3, 3);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoEsLaVersionInmediataAnterior() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, EstadoRevisionAsesor.RESUELTA, 2, 3);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEsLaUltimaVersionYRevisionResuelta() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, EstadoRevisionAsesor.RESUELTA, 3, 3);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(ObservacionNoReabribleException.class, ex -> {
                    var id = entrada.observacion().toString();
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Observacion.NO_REABRIBLE);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(id, "??");
                    assertThat(ex.getMessage()).contains(id);
                });
    }

    @Test
    void debeLanzar_cuandoLaVersionEsMasAntiguaQueLaAnterior() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, EstadoRevisionAsesor.RESUELTA, 1, 3);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(ObservacionNoReabribleException.class);
    }
}
