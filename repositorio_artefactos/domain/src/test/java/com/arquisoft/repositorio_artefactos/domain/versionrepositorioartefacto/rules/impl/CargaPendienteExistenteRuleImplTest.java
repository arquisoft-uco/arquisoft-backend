package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CargaPendienteExistenteRuleImplTest {

    private final CargaPendienteExistenteRuleImpl regla = new CargaPendienteExistenteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var encontrada = CargaPendienteDomain.reconstruir(carga, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "a.pdf", "application/pdf", 10L, "etag");

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaCargaPendiente(carga, encontrada)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoVacia() {
        // Arrange
        var existencia = new ExistenciaCargaPendiente(UtilUUID.generarNuevoUUID(), CargaPendienteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(CargaPendienteNoEncontradaException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_NO_ENCONTRADA);
    }
}
