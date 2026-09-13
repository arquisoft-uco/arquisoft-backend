package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioIdentificadorDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadIdentificadorUsuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioIdentificadorUnicoRuleImplTest {

    private final UsuarioIdentificadorUnicoRuleImpl regla = new UsuarioIdentificadorUnicoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoIdentificadorYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadIdentificadorUsuario("usr001", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(UsuarioIdentificadorDuplicadoException.class);
    }

    @Test
    void noDebeLanzarExcepcion_cuandoIdentificadorNoExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadIdentificadorUsuario("usr001", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
