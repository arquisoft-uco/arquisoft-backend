package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorSinObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorConObservacionesRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorConObservacionesRuleImplTest {

    @Test
    void debePasar_cuandoExistenObservaciones() {
        // Arrange
        var regla = new RevisionAsesorConObservacionesRuleImpl();
        var entrada = new ObservacionesDeRevision(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinObservaciones_cuandoNoExisten() {
        // Arrange
        var regla = new RevisionAsesorConObservacionesRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new ObservacionesDeRevision(revision, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorSinObservacionesException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.SIN_OBSERVACIONES);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString());
                });
    }
}
