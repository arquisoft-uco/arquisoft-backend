package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaModificadaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ConsistenciaCarga;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CargaSinModificarRuleImplTest {

    private final CargaSinModificarRuleImpl regla = new CargaSinModificarRuleImpl();

    @Test
    void debePasar_cuandoEtagsIguales() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "plantilla.docx", "abc", "abc")))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEtagsDistintos() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "plantilla.docx", "abc", "xyz")))
                .isInstanceOf(CargaModificadaException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_MODIFICADA);
    }

    @Test
    void debeLanzar_cuandoEtagAnalizadoVacio() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "plantilla.docx", "abc", "")))
                .isInstanceOf(CargaModificadaException.class);
    }

    @Test
    void debeLanzar_cuandoEtagConsultadoVacio() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ConsistenciaCarga(UtilUUID.generarNuevoUUID(), "plantilla.docx", "", "")))
                .isInstanceOf(CargaModificadaException.class);
    }
}
