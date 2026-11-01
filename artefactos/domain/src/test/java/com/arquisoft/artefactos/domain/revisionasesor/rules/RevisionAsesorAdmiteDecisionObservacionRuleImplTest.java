package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteDecisionObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteDecisionObservacionRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorAdmiteDecisionObservacionRuleImplTest {

    @Test
    void noDebeLanzar_cuandoLaRevisionEstaResuelta() {
        // Arrange
        var regla = new RevisionAsesorAdmiteDecisionObservacionRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.RESUELTA);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "EN_PROGRESO", "CERRADA"})
    void debeLanzar_cuandoLaRevisionNoAdmiteDecision(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionAsesorAdmiteDecisionObservacionRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoActualRevisionAsesor(revision, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorNoAdmiteDecisionObservacionException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.RevisionAsesor.NO_ADMITE_DECISION_OBSERVACION);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString(), estado.getId());
                });
    }
}
