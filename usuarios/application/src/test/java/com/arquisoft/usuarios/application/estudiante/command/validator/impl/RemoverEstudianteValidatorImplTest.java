package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverEstudianteValidatorImplTest {

    private final RemoverEstudianteValidatorImpl validator = new RemoverEstudianteValidatorImpl();

    @Test
    void noDebeLanzar_cuandoEstudianteEstaVigente() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, EstudianteDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEstudianteNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), EstudianteDomain.VACIO))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEstudianteYaFueRemovido() {
        // Arrange
        var usuario = UUID.randomUUID();
        var removido = EstudianteDomain.reconstruir(usuario, Instant.parse("2026-09-16T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, removido))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }
}
