package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminacionSolicitudNovedadCoordinadorDomainTest {

    @Test
    void debeCrearLaEliminacion_cuandoAmbosDatosSonValidos() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID remitenteUsuario = UUID.randomUUID();

        // Act
        EliminacionSolicitudNovedadCoordinadorDomain eliminacion =
                EliminacionSolicitudNovedadCoordinadorDomain.crear(solicitud, remitenteUsuario);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudNovedadCoordinadorDomain.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElRemitenteUsuarioEsNulo() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudNovedadCoordinadorDomain.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
    }

    @Test
    void debeAcumularAmbosErrores_cuandoLosDosDatosSonNulos() {
        // Act
        DomainValidationException excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudNovedadCoordinadorDomain.crear(null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.getErrores()).hasSize(2);
    }
}
