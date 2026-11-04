package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.VigenciaVersionArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionArtefactoRulesImplTest {

    @Test
    void debeLanzar_cuandoLaVersionNoExiste() {
        // Arrange
        var regla = new VersionArtefactoExisteRuleImpl();
        var entrada = new ExistenciaVersionArtefacto(UtilUUID.generarNuevoUUID(), VersionArtefactoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(VersionArtefactoNoEncontradaException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.VersionArtefacto.NO_ENCONTRADA));
    }

    @Test
    void noDebeLanzar_cuandoLaVersionExiste() {
        // Arrange
        var regla = new VersionArtefactoExisteRuleImpl();
        var id = UtilUUID.generarNuevoUUID();
        var version = VersionArtefactoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(), 1,
                UtilFecha.generarInstanteActual(), "uri");

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaVersionArtefacto(id, version))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoLaVersionNoEsLaUltima() {
        // Arrange
        var regla = new VersionArtefactoEsUltimaRuleImpl();
        var entrada = new VigenciaVersionArtefacto(UtilUUID.generarNuevoUUID(), 2, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(VersionArtefactoNoEsUltimaException.class,
                        ex -> assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.VersionArtefacto.NO_ES_ULTIMA));
    }

    @Test
    void noDebeLanzar_cuandoLaVersionEsLaUltima() {
        // Arrange
        var regla = new VersionArtefactoEsUltimaRuleImpl();
        var entrada = new VigenciaVersionArtefacto(UtilUUID.generarNuevoUUID(), 2, true);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
