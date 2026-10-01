package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.ExistenciaEstadoFichaPerfil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilExisteRuleImplTest {

    private final EstadoFichaPerfilExisteRuleImpl regla = new EstadoFichaPerfilExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaNoTieneEstado() {
        // Arrange
        var existencia = new ExistenciaEstadoFichaPerfil(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoLaFichaTieneEstado() {
        // Arrange
        var existencia = new ExistenciaEstadoFichaPerfil(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
