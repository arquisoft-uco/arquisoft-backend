package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaDestinatario;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestinatarioExisteRuleImplTest {

    private final DestinatarioExisteRuleImpl regla = new DestinatarioExisteRuleImpl();

    @Test
    void debeNoLanzar_cuandoElDestinatarioExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaDestinatario(UUID.randomUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElDestinatarioNoExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaDestinatario(usuario, false)))
                .isInstanceOf(DestinatarioNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }
}
