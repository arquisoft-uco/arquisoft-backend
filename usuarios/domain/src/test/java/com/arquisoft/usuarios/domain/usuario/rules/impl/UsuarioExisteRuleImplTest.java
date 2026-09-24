package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.usuario.model.ExistenciaUsuario;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioExisteRuleImplTest {

    private final UsuarioExisteRuleImpl rule = new UsuarioExisteRuleImpl();

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoElEncontradoEsVacio() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var existencia = new ExistenciaUsuario(usuarioId, UsuarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioFueEncontrado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var encontrado = UsuarioDomain.reconstruir(usuarioId, "usr001", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO);
        var existencia = new ExistenciaUsuario(usuarioId, encontrado);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
