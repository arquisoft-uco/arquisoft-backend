package com.arquisoft.usuarios.application.bibliotecario.command.validator.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverBibliotecarioValidatorImplTest {

    private final RemoverBibliotecarioValidatorImpl validator = new RemoverBibliotecarioValidatorImpl();

    @Test
    void noDebeLanzar_cuandoBibliotecarioEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, BibliotecarioDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoBibliotecarioNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), BibliotecarioDomain.VACIO))
                .isInstanceOf(BibliotecarioNoEncontradoException.class);
    }
}
