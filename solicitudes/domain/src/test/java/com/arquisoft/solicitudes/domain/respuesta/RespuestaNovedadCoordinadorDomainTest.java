package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RespuestaNovedadCoordinadorDomainTest {

    @Test
    void debeCrearLaAccion_cuandoLosTresDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID coordinador = UUID.randomUUID();

        // Act
        RespuestaNovedadCoordinadorDomain accion =
                RespuestaNovedadCoordinadorDomain.crear(solicitud, "contenido", coordinador);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getContenido()).isEqualTo("contenido");
        assertThat(accion.getCoordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeRecortarElContenido_cuandoTraeEspaciosAlrededor() {
        // Act
        var accion = RespuestaNovedadCoordinadorDomain.crear(
                UUID.randomUUID(), "  contenido  ", UUID.randomUUID());

        // Assert
        assertThat(accion.getContenido()).isEqualTo("contenido");
    }

    @Test
    void debeAcumularErrores_cuandoSolicitudYCoordinadorSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaNovedadCoordinadorDomain.crear(null, "contenido", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.SOLICITUD)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
