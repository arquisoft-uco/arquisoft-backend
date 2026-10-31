package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefactoConsultada;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionArtefactoConsultadaExisteRuleImplTest {

    private final VersionArtefactoConsultadaExisteRuleImpl regla = new VersionArtefactoConsultadaExisteRuleImpl();

    @Test
    void debeLanzar_cuandoAsesorEsPorDefecto() {
        // Arrange
        var entrada = new ExistenciaVersionArtefactoConsultada(
                UtilUUID.generarNuevoUUID(), UtilUUID.obtenerUUIDPorDefecto());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOf(VersionArtefactoNoEncontradaException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.NO_ENCONTRADA);
    }

    @Test
    void noDebeLanzar_cuandoAsesorExiste() {
        // Arrange
        var entrada = new ExistenciaVersionArtefactoConsultada(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }
}
