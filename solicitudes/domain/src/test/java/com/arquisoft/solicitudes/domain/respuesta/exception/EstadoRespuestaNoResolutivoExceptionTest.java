package com.arquisoft.solicitudes.domain.respuesta.exception;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoRespuestaNoResolutivoExceptionTest {

    @Test
    void debeNombrarElEstadoEnElMensajeYDejarElIdEnElDetalle_cuandoSeConstruye() {
        // Act
        var excepcion = new EstadoRespuestaNoResolutivoException(EstadoRespuesta.EN_REVISION);

        // Assert
        assertThat(excepcion.getCodigoError())
                .isEqualTo(SolicitudesCodes.Respuesta.ESTADO_NO_RESOLUTIVO);
        assertThat(excepcion.getError().getMessage())
                .contains(EstadoRespuesta.EN_REVISION.getNombre())
                .doesNotContain(EstadoRespuesta.EN_REVISION.getId());
        assertThat(excepcion.getMessage())
                .contains(EstadoRespuesta.EN_REVISION.getId());
    }
}
