package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAsesorValidatorImplTest {

    private final RemoverAsesorValidatorImpl validator = new RemoverAsesorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoAsesorEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, AsesorDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), AsesorDomain.VACIO))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = AsesorDomain.reconstruir(usuario, Instant.parse("2026-09-23T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, removido))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }
}
