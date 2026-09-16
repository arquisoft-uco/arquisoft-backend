package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoRespuestaNoResolutivoExceptionTest {

    @Test
    void debeExponerElCodigoYElMensajeConLosDatosDeEntrada_cuandoSeConstruye() {
        // Arrange
        var solicitud = UUID.randomUUID();

        // Act
        var excepcion = new EstadoRespuestaNoResolutivoException(solicitud, "EN_REVISION");

        // Assert
        assertThat(excepcion.getCodigoError())
                .isEqualTo(SolicitudesCodes.Respuesta.ESTADO_NO_RESOLUTIVO);
        assertThat(excepcion.getMessage())
                .contains("EN_REVISION")
                .contains(solicitud.toString());
    }
}
