package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.application.solicitud.command.validator.impl.EnviarSolicitudValidatorImpl;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnviarSolicitudValidatorImplTest {

    private final EnviarSolicitudValidatorImpl validator = new EnviarSolicitudValidatorImpl();

    private static DisponibilidadSolicitud disponibilidad(boolean yaExiste) {
        var clave = new ClaveSolicitud(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "mensaje");
        return new DisponibilidadSolicitud(clave, yaExiste);
    }

    @Test
    void noDebeLanzar_cuandoLaSolicitudNoExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(disponibilidad(false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaClaveYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(disponibilidad(true)))
                .isInstanceOf(SolicitudDuplicadaException.class);
    }
}
