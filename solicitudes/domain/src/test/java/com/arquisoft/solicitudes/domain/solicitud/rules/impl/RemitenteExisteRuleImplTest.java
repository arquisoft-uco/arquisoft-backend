package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaRemitente;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemitenteExisteRuleImplTest {

    private final RemitenteExisteRuleImpl regla = new RemitenteExisteRuleImpl();

    @Test
    void debeNoLanzar_cuandoElRemitenteExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaRemitente(UUID.randomUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElRemitenteNoExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaRemitente(usuario, false)))
                .isInstanceOf(RemitenteNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }
}
