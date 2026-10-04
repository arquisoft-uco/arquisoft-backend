package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminacionRespuestaNovedadCoordinadorDomainTest {

    @Test
    void debeCrearLaEliminacion_cuandoAmbosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var coordinadorUsuario = UUID.randomUUID();

        // Act
        var eliminacion = EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinadorUsuario);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getCoordinadorUsuario()).isEqualTo(coordinadorUsuario);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoLosDosDatosSonNulos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadCoordinadorDomain.crear(null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.getErrores()).hasSize(2);
    }
}
