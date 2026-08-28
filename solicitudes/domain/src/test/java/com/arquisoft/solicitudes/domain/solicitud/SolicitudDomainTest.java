package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitudDomainTest {

    @Test
    void debeCrearLaSolicitud_cuandoLosDatosSonValidos() {
        // Arrange
        UUID destinatario = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();

        // Act
        SolicitudDomain solicitud = SolicitudDomain.crear(
                destinatario, remitente, "  Necesito reportar una novedad  ",
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(solicitud.getId()).isNotNull();
        assertThat(solicitud.getFechaCreacion()).isNotNull();
        assertThat(solicitud.getDestinatario()).isEqualTo(destinatario);
        assertThat(solicitud.getRemitente()).isEqualTo(remitente);
        assertThat(solicitud.getMensajeSolicitud()).isEqualTo("Necesito reportar una novedad");
        assertThat(solicitud.getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
        assertThat(solicitud.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElMensajeEnBlancoYLosDemasCamposSonNulos() {
        // Arrange
        // (mensaje en blanco + destinatario/remitente/tipo nulos, todo en un solo intento)

        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> SolicitudDomain.crear(null, null, "  ", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeAcumularErrorDeLongitud_cuandoElMensajeSuperaLosCienCaracteres() {
        // Arrange
        String mensajeLargo = "a".repeat(101);

        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> SolicitudDomain.crear(UUID.randomUUID(), UUID.randomUUID(),
                        mensajeLargo, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();

        // Act
        SolicitudDomain solicitud = SolicitudDomain.reconstruir(
                id, null, null, fecha, null, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(solicitud.getId()).isEqualTo(id);
        assertThat(solicitud.getFechaCreacion()).isEqualTo(fecha);
        assertThat(solicitud.getDestinatario()).isNull();
        assertThat(solicitud.getMensajeSolicitud()).isNull();
    }

    @Test
    void debeReportarVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(SolicitudDomain.VACIO.esVacio()).isTrue();
        assertThat(SolicitudDomain.VACIO.getTipoSolicitud()).isEqualTo(TipoSolicitud.VACIO);
    }
}
