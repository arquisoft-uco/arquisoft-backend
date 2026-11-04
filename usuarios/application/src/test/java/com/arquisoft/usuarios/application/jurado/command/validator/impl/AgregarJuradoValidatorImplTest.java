package com.arquisoft.usuarios.application.jurado.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarJuradoValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarJuradoValidatorImpl validator = new AgregarJuradoValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioNoEsJuradoOEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = JuradoDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(usuario), JuradoDomain.VACIO))
                .doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(usuario(usuario), eliminado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoYaEsJuradoVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), JuradoDomain.crear(usuario)))
                .isInstanceOf(JuradoUsuarioDuplicadoException.class)
                .extracting(ex -> ((JuradoUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Jurado.USUARIO_DUPLICADO);
    }
}
