package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorConObservacionesAbiertasException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesAbiertasRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorSinObservacionesAbiertasRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorSinObservacionesAbiertasRuleImplTest {

    @Test
    void debeLanzar_cuandoLaRevisionEstaEnProgresoYHayAbiertas() {
        // Arrange
        var regla = new RevisionAsesorSinObservacionesAbiertasRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new ObservacionesAbiertasRevision(revision, EstadoRevisionAsesor.EN_PROGRESO, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorConObservacionesAbiertasException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.RevisionAsesor.CON_OBSERVACIONES_ABIERTAS);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoNoHayAbiertas() {
        // Arrange
        var regla = new RevisionAsesorSinObservacionesAbiertasRuleImpl();
        var entrada = new ObservacionesAbiertasRevision(
                UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoLaRevisionYaEstaResueltaAunqueHayaAbiertas() {
        // Arrange
        var regla = new RevisionAsesorSinObservacionesAbiertasRuleImpl();
        var entrada = new ObservacionesAbiertasRevision(
                UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.RESUELTA, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
