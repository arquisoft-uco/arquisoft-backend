package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TamanoArchivoPermitidoRuleImplTest {

    private final TamanoArchivoPermitidoRuleImpl regla = new TamanoArchivoPermitidoRuleImpl();

    @Test
    void debeLanzar_cuandoExcede() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new LimiteTamanoArchivo(5_242_881L, 5_242_880L)))
                .isInstanceOf(TamanoArchivoExcedidoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.TAMANO_ARCHIVO_EXCEDIDO);
    }

    @Test
    void debePasar_cuandoIgualAlMaximo() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new LimiteTamanoArchivo(5_242_880L, 5_242_880L)))
                .doesNotThrowAnyException();
    }
}
