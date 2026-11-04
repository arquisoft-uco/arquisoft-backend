package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorConObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorSinObservacionesRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorSinObservacionesRuleImplTest {

    @Test
    void debePasar_cuandoNoExistenObservaciones() {
        // Arrange
        var regla = new RevisionAsesorSinObservacionesRuleImpl();
        var entrada = new ObservacionesDeRevision(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarConObservaciones_cuandoExisten() {
        // Arrange
        var regla = new RevisionAsesorSinObservacionesRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new ObservacionesDeRevision(revision, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorConObservacionesException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.CON_OBSERVACIONES);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString());
                });
    }
}
