package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverCoordinadorValidatorImplTest {

    private final RemoverCoordinadorValidatorImpl validator = new RemoverCoordinadorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoCoordinadorEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, CoordinadorDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoCoordinadorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), CoordinadorDomain.VACIO))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoCoordinadorYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = CoordinadorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, removido))
                .isInstanceOf(CoordinadorNoEncontradoException.class);
    }
}
