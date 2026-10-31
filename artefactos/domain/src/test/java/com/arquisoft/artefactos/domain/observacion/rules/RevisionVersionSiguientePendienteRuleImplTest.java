package com.arquisoft.artefactos.domain.observacion.rules;

import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.impl.RevisionVersionSiguientePendienteRuleImpl;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteEnviadaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionVersionSiguientePendienteRuleImplTest {

    private final RevisionVersionSiguientePendienteRuleImpl regla = new RevisionVersionSiguientePendienteRuleImpl();

    private static RevisionAsesorDomain revision(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, UtilFecha.VACIO);
    }

    private static SituacionReaperturaObservacion situacion(
            EstadoObservacion destino, EstadoRevisionAsesor estadoSiguiente) {
        return new SituacionReaperturaObservacion(
                UtilUUID.generarNuevoUUID(), destino, EstadoRevisionAsesor.RESUELTA, 1, 2, revision(estadoSiguiente));
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionSiguienteEstaPendiente() {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoDestinoEsCerradaYLaRevisionYaSeEnvio() {
        // Arrange
        var entrada = situacion(EstadoObservacion.CERRADA, EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"EN_PROGRESO", "RESUELTA", "CERRADA"})
    void debeLanzar_cuandoLaRevisionSiguienteYaSeEnvio(EstadoRevisionAsesor estado) {
        // Arrange
        var entrada = situacion(EstadoObservacion.REABIERTA, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionVersionSiguienteEnviadaException.class, ex -> {
                    var id = entrada.revisionVersionSiguiente().getId().toString();
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.VERSION_SIGUIENTE_ENVIADA);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(id, "??");
                    assertThat(ex.getMessage()).contains(id, estado.getId());
                });
    }
}
