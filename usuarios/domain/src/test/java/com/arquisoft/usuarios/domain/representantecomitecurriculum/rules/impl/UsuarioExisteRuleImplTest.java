package com.arquisoft.usuarios.domain.representantecomitecurriculum.rules.impl;

import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.model.ExistenciaUsuario;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioExisteRuleImplTest {

    private final UsuarioExisteRuleImpl rule = new UsuarioExisteRuleImpl();

    @Test
    void debePasar_cuandoUsuarioExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        var entrada = new ExistenciaUsuario(usuario, true);

        // Act / Assert
        assertThatCode(() -> rule.validar(entrada))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioNoExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        var entrada = new ExistenciaUsuario(usuario, false);

        // Act / Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
