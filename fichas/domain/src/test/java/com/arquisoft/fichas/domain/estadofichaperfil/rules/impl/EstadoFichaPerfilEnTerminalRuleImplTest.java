package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilEnTerminalRuleImplTest {

    private final EstadoFichaPerfilEnTerminalRuleImpl regla = new EstadoFichaPerfilEnTerminalRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaNoTieneEstadoRegistrado() {
        // Arrange
        var estado = new EstadoActualFicha(UUID.randomUUID(), Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debeLanzarExcepcion_cuandoElEstadoActualEsTerminal() {
        // Arrange
        var estado = new EstadoActualFicha(UUID.randomUUID(), Optional.of(EstadoFicha.APROBADA));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);
    }

    @Test
    void debePasar_cuandoElEstadoActualNoEsTerminal() {
        // Arrange
        var estado = new EstadoActualFicha(UUID.randomUUID(), Optional.of(EstadoFicha.EN_CONSTRUCCION));

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }
}
