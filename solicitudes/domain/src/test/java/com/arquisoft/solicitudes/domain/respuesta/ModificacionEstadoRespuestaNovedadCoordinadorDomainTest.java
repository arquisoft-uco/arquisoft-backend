package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificacionEstadoRespuestaNovedadCoordinadorDomainTest {

    @Test
    void debeCrearLaModificacionConElEstadoTipado_cuandoLosTresDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinadorUsuario = UUID.randomUUID();

        // Act
        var modificacion = ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                solicitud, coordinadorUsuario, "APROBADA");

        // Assert
        assertThat(modificacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(modificacion.getCoordinadorUsuario()).isEqualTo(coordinadorUsuario);
        assertThat(modificacion.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoLosTresDatosSonInvalidos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(null, null, "   "));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
        assertThat(resultado.getErrores()).hasSize(3);
    }

    @Test
    void debeAcumularElErrorDeCatalogo_cuandoElNuevoEstadoNoPerteneceAlCatalogo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaNovedadCoordinadorDomain.crear(
                        null, UUID.randomUUID(), "XYZ"));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).hasSize(2);
        assertThat(errores).anySatisfy(error -> {
            assertThat(error.campo()).isEqualTo(SolicitudesFields.Respuesta.ESTADO);
            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.EstadoRespuesta.ESTADO_NO_ENCONTRADO);
        });
    }
}
