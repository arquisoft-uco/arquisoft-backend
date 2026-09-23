package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioInactivoException;
import com.arquisoft.usuarios.domain.usuario.model.EstadoActividadUsuario;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioActivoRuleImplTest {

    private final UsuarioActivoRuleImpl rule = new UsuarioActivoRuleImpl();

    @Test
    void debeLanzarUsuarioInactivo_cuandoElEstadoEsInactivo() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var usuario = UsuarioDomain.reconstruir(usuarioId, "usr001", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO);
        var estadoActividad = new EstadoActividadUsuario(usuarioId, usuario);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(estadoActividad))
                .isInstanceOf(UsuarioInactivoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElEstadoEsActivo() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var usuario = UsuarioDomain.reconstruir(usuarioId, "usr001", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO);
        var estadoActividad = new EstadoActividadUsuario(usuarioId, usuario);

        // Act & Assert
        assertThatCode(() -> rule.validar(estadoActividad)).doesNotThrowAnyException();
    }
}
