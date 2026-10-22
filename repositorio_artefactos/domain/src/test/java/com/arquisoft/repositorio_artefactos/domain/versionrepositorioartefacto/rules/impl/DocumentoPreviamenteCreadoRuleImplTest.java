package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoNoExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaDocumento;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentoPreviamenteCreadoRuleImplTest {

    private final DocumentoPreviamenteCreadoRuleImpl regla = new DocumentoPreviamenteCreadoRuleImpl();

    @Test
    void debePasar_cuandoNoEsNuevoYHayVersiones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDocumento("Formato", false, 2))).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoEsNuevoYHayVersiones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDocumento("Formato", true, 2))).doesNotThrowAnyException();
    }

    @Test
    void debePasar_cuandoEsNuevoYNoHayVersiones() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDocumento("Formato", true, 0))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoEsNuevoYNoHayVersiones() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaDocumento("Formato", false, 0)))
                .isInstanceOf(DocumentoNoExisteException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.DOCUMENTO_NO_EXISTE);
    }
}
