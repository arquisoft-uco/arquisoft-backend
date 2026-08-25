package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.model.ExistenciaAsesorFicha;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaExisteRuleImplTest {

    private final AsesorFichaExisteRuleImpl regla = new AsesorFichaExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElAsesorNoExiste() {
        // Arrange
        var existencia = new ExistenciaAsesorFicha(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoElAsesorExiste() {
        // Arrange
        var existencia = new ExistenciaAsesorFicha(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
