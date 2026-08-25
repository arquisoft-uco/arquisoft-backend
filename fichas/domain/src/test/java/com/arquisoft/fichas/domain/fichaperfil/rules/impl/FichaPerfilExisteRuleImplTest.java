package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaFichaPerfil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilExisteRuleImplTest {

    private final FichaPerfilExisteRuleImpl regla = new FichaPerfilExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var existencia = new ExistenciaFichaPerfil(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);
    }

    @Test
    void debePasar_cuandoLaFichaExiste() {
        // Arrange
        var existencia = new ExistenciaFichaPerfil(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
