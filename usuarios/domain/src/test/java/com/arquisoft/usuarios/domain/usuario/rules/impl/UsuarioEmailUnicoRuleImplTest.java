package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.secondaryport.UsuarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioEmailUnicoRuleImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @Test
    void debePasar_cuandoEmailNoEstaRegistrado() {
        // Arrange
        when(usuarioOutputPort.existePorEmail("nuevo@example.com")).thenReturn(false);
        UsuarioEmailUnicoRuleImpl regla = new UsuarioEmailUnicoRuleImpl(usuarioOutputPort);

        // Act / Assert
        assertThatCode(() -> regla.validar("nuevo@example.com")).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEmailYaEstaRegistrado() {
        // Arrange
        when(usuarioOutputPort.existePorEmail("repetido@example.com")).thenReturn(true);
        UsuarioEmailUnicoRuleImpl regla = new UsuarioEmailUnicoRuleImpl(usuarioOutputPort);

        // Act / Assert
        assertThatThrownBy(() -> regla.validar("repetido@example.com"))
                .isInstanceOf(UsuarioEmailDuplicadoException.class)
                .hasMessageContaining("repetido@example.com");
    }
}
