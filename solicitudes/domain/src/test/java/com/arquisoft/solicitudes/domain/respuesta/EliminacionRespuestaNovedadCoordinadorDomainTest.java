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
        UUID solicitud = UUID.randomUUID();
        UUID coordinadorUsuario = UUID.randomUUID();

        // Act
        EliminacionRespuestaNovedadCoordinadorDomain eliminacion =
                EliminacionRespuestaNovedadCoordinadorDomain.crear(solicitud, coordinadorUsuario);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getCoordinadorUsuario()).isEqualTo(coordinadorUsuario);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadCoordinadorDomain.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElCoordinadorUsuarioEsNulo() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadCoordinadorDomain.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }

    @Test
    void debeAcumularAmbosErrores_cuandoLosDosDatosSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadCoordinadorDomain.crear(null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.getErrores()).hasSize(2);
    }
}
