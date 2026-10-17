package com.arquisoft.usuarios.application.jurado.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverJuradoValidatorImplTest {

    private final RemoverJuradoValidatorImpl validator = new RemoverJuradoValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoElJuradoEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, usuario(usuario), JuradoDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarJuradoNoEncontrado_cuandoJuradoEsVacio() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), JuradoDomain.VACIO))
                .isInstanceOf(JuradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarJuradoNoEncontrado_cuandoJuradoEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = JuradoDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), eliminado))
                .isInstanceOf(JuradoNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioEsVacio() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, UsuarioDomain.VACIO, JuradoDomain.crear(usuario)))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontradoSinEvaluarJurado_cuandoAmbosSonVacios() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, UsuarioDomain.VACIO, JuradoDomain.VACIO))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
