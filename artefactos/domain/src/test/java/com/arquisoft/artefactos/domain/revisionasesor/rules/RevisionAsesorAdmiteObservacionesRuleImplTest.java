package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorAdmiteObservacionesRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorAdmiteObservacionesRuleImplTest {

    @Test
    void noDebeLanzar_cuandoLaRevisionEstaPendiente() {
        // Arrange
        var regla = new RevisionAsesorAdmiteObservacionesRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"EN_PROGRESO", "RESUELTA", "CERRADA"})
    void debeLanzar_cuandoLaRevisionNoEstaPendiente(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionAsesorAdmiteObservacionesRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoActualRevisionAsesor(revision, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorNoAdmiteObservacionesException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.NO_ADMITE_OBSERVACIONES);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString(), estado.getId());
                });
    }
}
