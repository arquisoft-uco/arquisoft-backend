package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RespuestaNovedadAsesorDomainTest {

    @Test
    void debeCrearLaAccion_cuandoLosTresDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID asesor = UUID.randomUUID();

        // Act
        RespuestaNovedadAsesorDomain accion =
                RespuestaNovedadAsesorDomain.crear(solicitud, "contenido", asesor);

        // Assert
        assertThat(accion.getSolicitud()).isEqualTo(solicitud);
        assertThat(accion.getContenido()).isEqualTo("contenido");
        assertThat(accion.getAsesorUsuario()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularErrores_cuandoSolicitudYAsesorSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> RespuestaNovedadAsesorDomain.crear(null, "contenido", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.SOLICITUD)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }
}
