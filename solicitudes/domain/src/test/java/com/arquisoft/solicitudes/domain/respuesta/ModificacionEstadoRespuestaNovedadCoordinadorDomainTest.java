package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificacionEstadoRespuestaNovedadCoordinadorDomainTest {

    @Test
    void debeCrearLaModificacion_cuandoLosTresDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinadorUsuario = UUID.randomUUID();

        // Act
        var modificacion = ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                solicitud, coordinadorUsuario, "APROBADA");

        // Assert
        assertThat(modificacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(modificacion.getCoordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(modificacion.getNuevoEstado()).isEqualTo("APROBADA");
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                        null, UUID.randomUUID(), "APROBADA"));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElCoordinadorUsuarioEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                        UUID.randomUUID(), null, "APROBADA"));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElNuevoEstadoEstaEnBlanco() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                        UUID.randomUUID(), UUID.randomUUID(), "   "));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
    }

    @Test
    void debeAcumularLosTresErrores_cuandoLosTresDatosSonInvalidos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(null, null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
        assertThat(resultado.getErrores()).hasSize(3);
    }
}
