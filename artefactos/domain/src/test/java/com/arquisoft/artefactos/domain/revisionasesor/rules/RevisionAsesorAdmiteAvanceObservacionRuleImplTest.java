package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteAvanceObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteAvanceObservacionRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorAdmiteAvanceObservacionRuleImplTest {

    @Test
    void noDebeLanzar_cuandoLaRevisionEstaEnProgreso() {
        // Arrange
        var regla = new RevisionAsesorAdmiteAvanceObservacionRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.EN_PROGRESO);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "RESUELTA", "CERRADA"})
    void debeLanzar_cuandoLaRevisionNoAdmiteAvance(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionAsesorAdmiteAvanceObservacionRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoActualRevisionAsesor(revision, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorNoAdmiteAvanceObservacionException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.RevisionAsesor.NO_ADMITE_AVANCE_OBSERVACION);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString(), estado.getId());
                });
    }
}
