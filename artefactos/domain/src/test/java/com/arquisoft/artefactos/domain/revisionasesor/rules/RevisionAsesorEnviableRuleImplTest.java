package com.arquisoft.artefactos.domain.revisionasesor.rules;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEnviableException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.impl.RevisionAsesorEnviableRuleImpl;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorEnviableRuleImplTest {

    @Test
    void debePasar_cuandoLaRevisionEstaPendiente() {
        // Arrange
        var regla = new RevisionAsesorEnviableRuleImpl();
        var entrada = new EstadoActualRevisionAsesor(UtilUUID.generarNuevoUUID(), EstadoRevisionAsesor.PENDIENTE);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"EN_PROGRESO", "RESUELTA", "CERRADA"})
    void debeLanzarNoEnviable_cuandoLaRevisionNoEstaPendiente(EstadoRevisionAsesor estado) {
        // Arrange
        var regla = new RevisionAsesorEnviableRuleImpl();
        var revision = UtilUUID.generarNuevoUUID();
        var entrada = new EstadoActualRevisionAsesor(revision, estado);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(RevisionAsesorNoEnviableException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.NO_ENVIABLE);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(revision.toString(), "??");
                    assertThat(ex.getMessage()).contains(revision.toString(), estado.getId());
                });
    }
}
