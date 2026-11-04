package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CargaPendienteExistenteRuleImplTest {

    private final CargaPendienteExistenteRuleImpl regla = new CargaPendienteExistenteRuleImpl();

    @Test
    void debeLanzar_cuandoVacia() {
        // Arrange
        var existencia = new ExistenciaCargaPendiente(UtilUUID.generarNuevoUUID(), CargaPendienteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(CargaPendienteNoEncontradaException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.CARGA_NO_ENCONTRADA);
    }
}
