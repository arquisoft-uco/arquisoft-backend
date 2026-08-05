package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilEnTerminalRuleImplTest {

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"APROBADA", "APROBADA_CON_OBSERVACIONES", "NO_APROBADA"})
    void debeLanzarExcepcion_cuandoEstadoEsTerminal(EstadoFicha estadoTerminal) {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var regla = new EstadoFichaPerfilEnTerminalRuleImpl(
                new EstadoFichaPerfilOutputPortStub(Optional.of(estadoTerminal)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(fichaPerfilId))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = {"EN_CONSTRUCCION", "DISPONIBLE_PARA_EVALUACION"})
    void noDebeLanzarExcepcion_cuandoEstadoNoEsTerminal(EstadoFicha estadoNoTerminal) {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var regla = new EstadoFichaPerfilEnTerminalRuleImpl(
                new EstadoFichaPerfilOutputPortStub(Optional.of(estadoNoTerminal)));

        // Act & Assert
        assertThatCode(() -> regla.validar(fichaPerfilId)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEstadoFichaPerfilNoEncontradoException_cuandoNoHayEstadoRegistrado() {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        var regla = new EstadoFichaPerfilEnTerminalRuleImpl(
                new EstadoFichaPerfilOutputPortStub(Optional.empty()));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(fichaPerfilId))
                .isInstanceOf(EstadoFichaPerfilNoEncontradoException.class)
                .hasMessageContaining(fichaPerfilId.toString());
    }

    /** fichas:domain no tiene Mockito en el classpath de test: el doble se escribe a mano. */
    private record EstadoFichaPerfilOutputPortStub(Optional<EstadoFicha> estadoActual)
            implements EstadoFichaPerfilOutputPort {

        @Override
        public void registrarEstadoInicial(EstadoFichaPerfilDomain aggregate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId) {
            return estadoActual;
        }
    }
}
