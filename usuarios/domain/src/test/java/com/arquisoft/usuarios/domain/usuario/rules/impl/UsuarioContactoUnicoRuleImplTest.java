package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioContactoDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadContactoUsuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioContactoUnicoRuleImplTest {

    private final UsuarioContactoUnicoRuleImpl regla = new UsuarioContactoUnicoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoContactoYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadContactoUsuario("573001112233", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(UsuarioContactoDuplicadoException.class);
    }

    @Test
    void noDebeLanzarExcepcion_cuandoContactoNoExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadContactoUsuario("573001112233", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
