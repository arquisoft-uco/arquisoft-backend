package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TamanoArchivoPermitidoRuleImplTest {

    private static final long MAXIMO = 5_242_880L;

    private final TamanoArchivoPermitidoRuleImpl regla = new TamanoArchivoPermitidoRuleImpl();

    @Test
    void debePasar_enElLimite() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new LimiteTamanoArchivo("plantilla.docx", MAXIMO, MAXIMO))).doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(new LimiteTamanoArchivo("plantilla.docx", 1L, MAXIMO))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoExcede() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new LimiteTamanoArchivo("plantilla.docx", MAXIMO + 1, MAXIMO)))
                .isInstanceOf(TamanoArchivoExcedidoException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.VersionRepositorioArtefacto.TAMANO_ARCHIVO_EXCEDIDO);
    }

    @Test
    void debeLanzar_cuandoCero() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new LimiteTamanoArchivo("plantilla.docx", 0L, MAXIMO)))
                .isInstanceOf(TamanoArchivoExcedidoException.class);
    }
}
