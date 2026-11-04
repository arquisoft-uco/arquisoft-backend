package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaParaEliminarException;
import com.arquisoft.artefactos.domain.versionartefacto.model.VigenciaVersionArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionArtefactoUltimaParaEliminarRuleImplTest {

    @Test
    void debeNoLanzar_cuandoEsUltima() {
        // Arrange
        var regla = new VersionArtefactoUltimaParaEliminarRuleImpl();
        var entrada = new VigenciaVersionArtefacto(UtilUUID.generarNuevoUUID(), 3, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoEsUltima() {
        // Arrange
        var regla = new VersionArtefactoUltimaParaEliminarRuleImpl();
        var version = UtilUUID.generarNuevoUUID();
        var entrada = new VigenciaVersionArtefacto(version, 2, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(VersionArtefactoNoEsUltimaParaEliminarException.class, ex -> {
                    assertThat(ex.getCodigoError())
                            .isEqualTo(ArtefactosCodes.VersionArtefacto.NO_ES_ULTIMA_PARA_ELIMINAR);
                    assertThat(ex.getError().getMessage()).isNotBlank().doesNotContain(version.toString(), "??");
                    assertThat(ex.getMessage()).contains(version.toString());
                });
    }
}
