package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoConRevisionException;
import com.arquisoft.artefactos.domain.versionartefacto.model.RevisionesVersionArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionArtefactoSinRevisionRuleImplTest {

    @Test
    void debeNoLanzar_cuandoNoTieneRevision() {
        // Arrange
        var regla = new VersionArtefactoSinRevisionRuleImpl();
        var entrada = new RevisionesVersionArtefacto(UtilUUID.generarNuevoUUID(), 2, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoTieneRevision() {
        // Arrange
        var regla = new VersionArtefactoSinRevisionRuleImpl();
        var version = UtilUUID.generarNuevoUUID();
        var entrada = new RevisionesVersionArtefacto(version, 2, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(VersionArtefactoConRevisionException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.VersionArtefacto.CON_REVISION);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(version.toString(), "??");
                    assertThat(ex.getMessage()).contains(version.toString());
                });
    }
}
