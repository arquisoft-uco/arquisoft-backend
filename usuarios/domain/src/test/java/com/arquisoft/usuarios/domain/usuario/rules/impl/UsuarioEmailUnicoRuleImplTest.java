package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.DisponibilidadEmailUsuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioEmailUnicoRuleImplTest {

    private final UsuarioEmailUnicoRuleImpl regla = new UsuarioEmailUnicoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElEmailYaEstaRegistrado() {
        // Arrange
        var disponibilidad = new DisponibilidadEmailUsuario("duplicado@arquisoft.com", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(UsuarioEmailDuplicadoException.class)
                .hasMessageContaining("duplicado@arquisoft.com");
    }

    @Test
    void debePasar_cuandoElEmailEstaLibre() {
        // Arrange
        var disponibilidad = new DisponibilidadEmailUsuario("nuevo@arquisoft.com", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
