package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaModificadaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ConsistenciaCarga;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CargaSinModificarRuleImplTest {

    private final CargaSinModificarRuleImpl regla = new CargaSinModificarRuleImpl();

    @Test
    void debePasar_cuandoEtagsIguales() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "abc", "abc")))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEtagDistinto() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "abc", "xyz")))
                .isInstanceOf(CargaModificadaException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.CARGA_MODIFICADA);
    }

    @Test
    void debeLanzar_cuandoEtagVacio() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "abc", "")))
                .isInstanceOf(CargaModificadaException.class);
    }
}
