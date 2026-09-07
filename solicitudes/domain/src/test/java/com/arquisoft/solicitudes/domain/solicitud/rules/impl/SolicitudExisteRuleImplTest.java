package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudExisteRuleImplTest {

    private final SolicitudExisteRuleImpl regla = new SolicitudExisteRuleImpl();

    @Test
    void debeNoLanzar_cuandoLaSolicitudExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaSolicitud(UUID.randomUUID(), true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudNoEncontrada_cuandoLaSolicitudNoExiste() {
        // Arrange
        UUID solicitud = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new ExistenciaSolicitud(solicitud, false)))
                .isInstanceOf(SolicitudNoEncontradaException.class)
                .hasMessageContaining(solicitud.toString());
    }
}
