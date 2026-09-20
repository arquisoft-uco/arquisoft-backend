package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminacionRespuestaNovedadAsesorDomainTest {

    @Test
    void debeCrearLaEliminacion_cuandoAmbosDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesorUsuario = UUID.randomUUID();

        // Act
        var eliminacion = EliminacionRespuestaNovedadAsesorDomain.crear(solicitud, asesorUsuario);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getAsesorUsuario()).isEqualTo(asesorUsuario);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadAsesorDomain.crear(null, UUID.randomUUID()));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElAsesorUsuarioEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadAsesorDomain.crear(UUID.randomUUID(), null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }

    @Test
    void debeAcumularAmbosErrores_cuandoLosDosDatosSonNulos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaNovedadAsesorDomain.crear(null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.getErrores()).hasSize(2);
    }
}
