package com.arquisoft.usuarios.application.usuario.query.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEliminadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarUsuarioPorCorreoValidatorImplTest {

    private final ConsultarUsuarioPorCorreoValidatorImpl validator = new ConsultarUsuarioPorCorreoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoElUsuarioExisteYEstaVigente() {
        // Arrange
        var encontrado = UsuarioDomain.reconstruir(UtilUUID.generarNuevoUUID(), "1001", "Ana Ramirez",
                "ana@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO, UtilFecha.VACIO);

        // Act / Assert
        assertThatCode(() -> validator.validar(encontrado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoNoExiste() {
        // Act / Assert
        assertThatThrownBy(() -> validator.validar(UsuarioDomain.VACIO))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoEstaDadoDeBaja() {
        // Arrange
        var encontrado = UsuarioDomain.reconstruir(UtilUUID.generarNuevoUUID(), "1001", "Ana Ramirez",
                "ana@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, Instant.parse("2026-09-25T10:15:30Z"));

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(encontrado)).isInstanceOf(UsuarioEliminadoException.class);
    }
}
