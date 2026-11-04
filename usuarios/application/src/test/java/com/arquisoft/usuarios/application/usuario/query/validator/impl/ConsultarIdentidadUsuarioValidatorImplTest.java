package com.arquisoft.usuarios.application.usuario.query.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEliminadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarIdentidadUsuarioValidatorImplTest {

    private final ConsultarIdentidadUsuarioValidatorImpl validator = new ConsultarIdentidadUsuarioValidatorImpl();

    @Test
    void noDebeLanzar_cuandoElUsuarioExisteEstaInactivoYNoEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var encontrado = UsuarioDomain.reconstruir(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", EstadoUsuario.INACTIVO, UtilFecha.VACIO);

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, encontrado)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElUsuarioEsVacio() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, UsuarioDomain.VACIO))
                .isInstanceOfSatisfying(UsuarioNoEncontradoException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.NO_ENCONTRADO));
    }

    @Test
    void debeLanzarEliminado_cuandoElUsuarioEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var encontrado = UsuarioDomain.reconstruir(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", EstadoUsuario.INACTIVO, Instant.parse("2026-09-25T10:15:30Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, encontrado))
                .isInstanceOfSatisfying(UsuarioEliminadoException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.ELIMINADO));
    }
}
