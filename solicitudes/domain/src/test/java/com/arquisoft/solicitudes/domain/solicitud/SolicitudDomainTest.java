package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitudDomainTest {

    @Test
    void debeCrearLaSolicitud_cuandoLosDatosSonValidos() {
        // Arrange
        var destinatario = UUID.randomUUID();
        var remitente = UUID.randomUUID();

        // Act
        var solicitud = SolicitudDomain.crear(
                destinatario, remitente, "  Necesito reportar una novedad  ",
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(solicitud.getId()).isNotNull();
        assertThat(solicitud.getFechaCreacion()).isNotNull();
        assertThat(solicitud.getDestinatarioUsuario()).isEqualTo(destinatario);
        assertThat(solicitud.getRemitenteUsuario()).isEqualTo(remitente);
        assertThat(solicitud.getMensajeSolicitud()).isEqualTo("Necesito reportar una novedad");
        assertThat(solicitud.getTipoSolicitud()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
        assertThat(solicitud.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElMensajeEnBlancoYLosDemasCamposSonNulos() {
        // Arrange
        // (mensaje en blanco + destinatario/remitente/tipo nulos, todo en un solo intento)

        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> SolicitudDomain.crear(null, null, "  ", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.TIPO_SOLICITUD)).isTrue();
    }

    @Test
    void debeReportarElErrorDelTipoConSuPropioCampoYCodigo_cuandoElTipoEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> SolicitudDomain.crear(UUID.randomUUID(), UUID.randomUUID(), "mensaje", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.getErrores()).hasSize(1);
        assertThat(resultado.getErrores().get(0).campo())
                .isEqualTo(SolicitudesFields.Solicitud.TIPO_SOLICITUD);
        assertThat(resultado.getErrores().get(0).codigoError())
                .isEqualTo(SolicitudesCodes.Solicitud.TIPO_REQUERIDO);
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isFalse();
    }

    @Test
    void debeAcumularErrorDeLongitud_cuandoElMensajeSuperaLosCienCaracteres() {
        // Arrange
        var mensajeLargo = "a".repeat(101);

        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> SolicitudDomain.crear(UUID.randomUUID(), UUID.randomUUID(),
                        mensajeLargo, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }

    @Test
    void debeAceptarElMensaje_cuandoMideCienCaracteresTrasRecortarLosEspacios() {
        // Arrange
        var mensaje = "  " + "a".repeat(100) + "  ";

        // Act
        var solicitud = SolicitudDomain.crear(UUID.randomUUID(), UUID.randomUUID(),
                mensaje, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(solicitud.getMensajeSolicitud()).isEqualTo("a".repeat(100));
    }

    @Test
    void debeRechazarElMensaje_cuandoSuperaLosCienCaracteresTrasRecortarLosEspacios() {
        // Arrange
        var mensaje = "  " + "a".repeat(101) + "  ";

        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> SolicitudDomain.crear(UUID.randomUUID(), UUID.randomUUID(),
                        mensaje, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.MENSAJE)).isTrue();
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var fecha = Instant.now();

        // Act
        var solicitud = SolicitudDomain.reconstruir(
                id, null, null, fecha, null, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(solicitud.getId()).isEqualTo(id);
        assertThat(solicitud.getFechaCreacion()).isEqualTo(fecha);
        assertThat(solicitud.getDestinatarioUsuario()).isNull();
        assertThat(solicitud.getMensajeSolicitud()).isNull();
    }

    @Test
    void debeReportarVacio_cuandoEsElCentinela() {
        // Act & Assert
        assertThat(SolicitudDomain.VACIO.esVacio()).isTrue();
        assertThat(SolicitudDomain.VACIO.getTipoSolicitud()).isEqualTo(TipoSolicitud.VACIO);
    }
}
