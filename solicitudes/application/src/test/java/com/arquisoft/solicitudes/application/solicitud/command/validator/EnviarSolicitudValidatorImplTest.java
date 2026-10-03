package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.validator.impl.EnviarSolicitudValidatorImpl;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoAsignadoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnviarSolicitudValidatorImplTest {

    private final EnviarSolicitudValidatorImpl validator = new EnviarSolicitudValidatorImpl();

    private EnvioSolicitudDomain envio;

    private static DisponibilidadSolicitud disponibilidad(boolean yaExiste) {
        var clave = new ClaveSolicitud(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "mensaje");
        return new DisponibilidadSolicitud(clave, yaExiste);
    }

    @BeforeEach
    void setUp() {
        var command = EnviarSolicitudNovedadAsesorCommand.crear(
                UUID.randomUUID(), UUID.randomUUID().toString(), "novedad");
        envio = EnviarSolicitudNovedadAsesorMapper.toDomain(command);
    }

    @Test
    void noDebeLanzar_cuandoElDestinatarioEstaAsignadoYLaSolicitudNoExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(envio, true, disponibilidad(false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDestinatarioNoAsignado_cuandoElDestinatarioNoEsElResponsable() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(envio, false, disponibilidad(false)))
                .isInstanceOf(DestinatarioNoAsignadoException.class);
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaClaveYaExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(envio, true, disponibilidad(true)))
                .isInstanceOf(SolicitudDuplicadaException.class);
    }

    @Test
    void debeLanzarPrimeroLaAsignacion_cuandoFallanLaAsignacionYLaUnicidad() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(envio, false, disponibilidad(true)))
                .isInstanceOf(DestinatarioNoAsignadoException.class);
    }
}
