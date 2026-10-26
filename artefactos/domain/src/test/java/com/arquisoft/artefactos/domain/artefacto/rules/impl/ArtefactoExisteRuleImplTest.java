package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoExisteRuleImplTest {

    private final ArtefactoExisteRuleImpl regla = new ArtefactoExisteRuleImpl();

    @Test
    void debeLanzar_cuandoVacio() {
        // Arrange
        var existencia = new ExistenciaArtefacto(UtilUUID.generarNuevoUUID(), ArtefactoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(ArtefactoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.Artefacto.NO_ENCONTRADO);
    }

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = ArtefactoDomain.reconstruir(id, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaArtefacto(id, encontrado))).doesNotThrowAnyException();
    }
}
