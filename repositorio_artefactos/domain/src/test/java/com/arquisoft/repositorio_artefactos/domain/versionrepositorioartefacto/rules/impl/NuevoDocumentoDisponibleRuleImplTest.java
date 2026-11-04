package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoYaExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaDocumento;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NuevoDocumentoDisponibleRuleImplTest {

    private final NuevoDocumentoDisponibleRuleImpl regla = new NuevoDocumentoDisponibleRuleImpl();

    @Test
    void debePasar_cuandoEsNuevoYNoHayVersiones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDocumento("Formato", true, 0))).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoNoEsNuevoYHayVersiones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDocumento("Formato", false, 3))).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoNoEsNuevoYNoHayVersiones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDocumento("Formato", false, 0))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEsNuevoYYaHayVersiones() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaDocumento("Formato", true, 1)))
                .isInstanceOf(DocumentoYaExisteException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.DOCUMENTO_YA_EXISTE);
    }
}
