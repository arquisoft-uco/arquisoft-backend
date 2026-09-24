package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminacionSolicitudNovedadAsesorDomainTest {

    @Test
    void debeCrearLaEliminacion_cuandoAmbosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitenteUsuario = UUID.randomUUID();

        // Act
        var eliminacion = EliminacionSolicitudNovedadAsesorDomain.crear(solicitud, remitenteUsuario);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudNovedadAsesorDomain.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElRemitenteUsuarioEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudNovedadAsesorDomain.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
    }

    @Test
    void debeAcumularAmbosErrores_cuandoLosDosDatosSonNulos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudNovedadAsesorDomain.crear(null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.getErrores()).hasSize(2);
    }
}
