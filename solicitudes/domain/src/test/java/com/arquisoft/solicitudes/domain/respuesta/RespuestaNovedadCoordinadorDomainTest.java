package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RespuestaNovedadCoordinadorDomainTest {

    @Test
    void debeCrearLaAccion_cuandoLosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();
        RespuestaDomain respuesta = RespuestaDomain.crear(solicitud, "contenido");

        // Act
        RespuestaNovedadCoordinadorDomain accion =
                RespuestaNovedadCoordinadorDomain.crear(respuesta, coordinador);

        // Assert
        assertThat(accion.getRespuesta()).isEqualTo(respuesta);
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getCoordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularErrores_cuandoRespuestaYCoordinadorSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaNovedadCoordinadorDomain.crear(null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.SOLICITUD)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
