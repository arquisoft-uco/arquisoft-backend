package com.arquisoft.usuarios.application.bibliotecario.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarBibliotecarioValidatorImplTest {

    private final AgregarBibliotecarioValidatorImpl validator = new AgregarBibliotecarioValidatorImpl();

    @Test
    void noDebeLanzar_cuandoElUsuarioNoEsBibliotecarioOEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = BibliotecarioDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, BibliotecarioDomain.VACIO))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(usuario, eliminado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaEsBibliotecarioVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, BibliotecarioDomain.crear(usuario)))
                .isInstanceOf(BibliotecarioUsuarioDuplicadoException.class)
                .extracting(ex -> ((BibliotecarioUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Bibliotecario.USUARIO_DUPLICADO);
    }
}
