package com.arquisoft.usuarios.application.jurado.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarJuradoValidatorImplTest {

    private final AgregarJuradoValidatorImpl validator = new AgregarJuradoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoElUsuarioNoEsJuradoOEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = JuradoDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, JuradoDomain.VACIO))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(usuario, eliminado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaEsJuradoVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, JuradoDomain.crear(usuario)))
                .isInstanceOf(JuradoUsuarioDuplicadoException.class)
                .extracting(ex -> ((JuradoUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Jurado.USUARIO_DUPLICADO);
    }
}
