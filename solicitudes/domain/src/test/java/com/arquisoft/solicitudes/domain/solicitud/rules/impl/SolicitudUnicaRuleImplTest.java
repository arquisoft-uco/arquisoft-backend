package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudUnicaRuleImplTest {

    private final SolicitudUnicaRuleImpl regla = new SolicitudUnicaRuleImpl();

    private static ClaveSolicitud clave() {
        return new ClaveSolicitud(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "mensaje");
    }

    @Test
    void debeNoLanzar_cuandoLaCombinacionNoExiste() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new DisponibilidadSolicitud(clave(), false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaCombinacionYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new DisponibilidadSolicitud(clave(), true)))
                .isInstanceOf(SolicitudDuplicadaException.class);
    }
}
