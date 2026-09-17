package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnvioSolicitudCambioAsesorDomainTest {

    @Test
    void debeAgruparLosTresObjetos_cuandoLosDatosSonValidos() {
        // Arrange
        UUID remitenteUsuario = UUID.randomUUID();
        UUID destinatarioUsuario = UUID.randomUUID();
        RemitenteDomain remitente = RemitenteDomain.crear(remitenteUsuario);
        DestinatarioDomain destinatario = DestinatarioDomain.crear(destinatarioUsuario);
        SolicitudDomain solicitud = SolicitudDomain.crear(
                destinatario.getId(), remitente.getId(), "cambio de asesor",
                TipoSolicitud.CAMBIO_DE_ASESOR);

        // Act
        EnvioSolicitudCambioAsesorDomain envio =
                EnvioSolicitudCambioAsesorDomain.crear(solicitud, remitente, destinatario);

        // Assert
        assertThat(envio.getSolicitud()).isSameAs(solicitud);
        assertThat(envio.getRemitente()).isSameAs(remitente);
        assertThat(envio.getDestinatario()).isSameAs(destinatario);
        assertThat(envio.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
        assertThat(envio.getDestinatarioUsuario()).isEqualTo(destinatarioUsuario);
    }

    @Test
    void debeAcumularLosErrores_cuandoLosTresObjetosSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EnvioSolicitudCambioAsesorDomain.crear(null, null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Remitente.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Destinatario.DESTINATARIO)).isTrue();
    }
}
