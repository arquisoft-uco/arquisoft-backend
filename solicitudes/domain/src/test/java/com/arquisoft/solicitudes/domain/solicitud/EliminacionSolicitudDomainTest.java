package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminacionSolicitudDomainTest {

    @Test
    void debeCrearLaEliminacion_cuandoLosTresDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var remitenteUsuario = UUID.randomUUID();

        // Act
        var eliminacion = EliminacionSolicitudDomain.crear(
                solicitud, remitenteUsuario, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getRemitenteUsuario()).isEqualTo(remitenteUsuario);
        assertThat(eliminacion.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudDomain.crear(
                        null, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElRemitenteUsuarioEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudDomain.crear(
                        UUID.randomUUID(), null, TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElTipoEsperadoEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudDomain.crear(UUID.randomUUID(), UUID.randomUUID(), null));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).hasSize(1);
        assertThat(errores.get(0).campo()).isEqualTo(SolicitudesFields.Solicitud.TIPO_SOLICITUD);
        assertThat(errores.get(0).codigoError()).isEqualTo(SolicitudesCodes.Solicitud.TIPO_REQUERIDO);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosDatosSonNulos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionSolicitudDomain.crear(null, null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.REMITENTE)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.TIPO_SOLICITUD)).isTrue();
        assertThat(resultado.getErrores()).hasSize(3);
    }
}
