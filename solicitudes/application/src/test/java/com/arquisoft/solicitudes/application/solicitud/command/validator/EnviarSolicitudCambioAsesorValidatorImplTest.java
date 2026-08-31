package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.application.solicitud.command.validator.impl.EnviarSolicitudCambioAsesorValidatorImpl;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnviarSolicitudCambioAsesorValidatorImplTest {

    private final EnviarSolicitudCambioAsesorValidatorImpl validator =
            new EnviarSolicitudCambioAsesorValidatorImpl();

    private static EnvioSolicitudCambioAsesorDomain envio() {
        RemitenteDomain remitente = RemitenteDomain.crear(UUID.randomUUID());
        DestinatarioDomain destinatario = DestinatarioDomain.crear(UUID.randomUUID());
        SolicitudDomain solicitud = SolicitudDomain.crear(
                destinatario.getId(), remitente.getId(), "cambio de asesor",
                TipoSolicitud.CAMBIO_DE_ASESOR);
        return EnvioSolicitudCambioAsesorDomain.crear(solicitud, remitente, destinatario);
    }

    @Test
    void debePasar_cuandoRemitenteYDestinatarioExisten() {
        // Act & Assert
        assertThatCode(() -> validator.validarExistenciaUsuarios(envio(), true, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarRemitenteNoEncontrado_cuandoElRemitenteNoExiste() {
        // Arrange
        var envio = envio();

        // Act & Assert
        assertThatThrownBy(() -> validator.validarExistenciaUsuarios(envio, false, true))
                .isInstanceOf(RemitenteNoEncontradoException.class)
                .hasMessageContaining(envio.getRemitenteUsuario().toString());
    }

    @Test
    void debeLanzarDestinatarioNoEncontrado_cuandoElDestinatarioNoExiste() {
        // Arrange
        var envio = envio();

        // Act & Assert
        assertThatThrownBy(() -> validator.validarExistenciaUsuarios(envio, true, false))
                .isInstanceOf(DestinatarioNoEncontradoException.class)
                .hasMessageContaining(envio.getDestinatarioUsuario().toString());
    }

    @Test
    void debeReportarPrimeroLaAusenciaDelRemitente_cuandoAmbosFaltan() {
        // Act & Assert — el orden es parte del contrato: remitente antes que destinatario
        assertThatThrownBy(() -> validator.validarExistenciaUsuarios(envio(), false, false))
                .isInstanceOf(RemitenteNoEncontradoException.class);
    }

    @Test
    void debeLanzarSolicitudDuplicada_cuandoLaClaveYaExiste() {
        // Arrange
        var clave = new ClaveSolicitud(
                UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), "cambio de asesor");

        // Act & Assert
        assertThatCode(() -> validator.validarUnicidad(new DisponibilidadSolicitud(clave, false)))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> validator.validarUnicidad(new DisponibilidadSolicitud(clave, true)))
                .isInstanceOf(SolicitudDuplicadaException.class);
    }
}
