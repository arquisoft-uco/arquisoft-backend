package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RespuestaSolicitudDomainTest {

    @Test
    void debeCrearLaAccion_cuandoLosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID responsable = UUID.randomUUID();
        RespuestaDomain respuesta = RespuestaDomain.crear(solicitud, "contenido");

        // Act
        RespuestaSolicitudDomain accion = RespuestaSolicitudDomain.crear(
                respuesta, responsable, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(accion.getRespuesta()).isEqualTo(respuesta);
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getResponsableUsuario()).isEqualTo(responsable);
        assertThat(accion.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }

    @Test
    void debeAcumularErrores_cuandoRespuestaResponsableYTipoSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaSolicitudDomain.crear(null, null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.SOLICITUD)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.TIPO_SOLICITUD)).isTrue();
        assertThat(resultado.getErrores()).hasSize(3);
    }

    @Test
    void debeReportarElTipoConSuPropioCodigo_cuandoElTipoEsperadoEsNulo() {
        // Arrange
        RespuestaDomain respuesta = RespuestaDomain.crear(UUID.randomUUID(), "contenido");

        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaSolicitudDomain.crear(respuesta, UUID.randomUUID(), null));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).hasSize(1);
        assertThat(errores.get(0).codigoError()).isEqualTo(SolicitudesCodes.Solicitud.TIPO_REQUERIDO);
    }
}
