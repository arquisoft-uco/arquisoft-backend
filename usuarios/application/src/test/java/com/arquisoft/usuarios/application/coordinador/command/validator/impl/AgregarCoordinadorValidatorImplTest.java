package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarCoordinadorValidatorImplTest {

    private final AgregarCoordinadorValidatorImpl validator = new AgregarCoordinadorValidatorImpl();

    @Test
    void debeOrquestarRuleUnica_enAgregarCoordinadorValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), CoordinadorDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsCoordinador() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, CoordinadorDomain.crear(usuario)))
                .isInstanceOf(CoordinadorUsuarioDuplicadoException.class);
    }

    @Test
    void noDebeLanzar_cuandoCoordinadorEstaRemovido() {
        // Arrange
        var usuario = UUID.randomUUID();
        var removido = CoordinadorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, removido)).doesNotThrowAnyException();
    }
}
