package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EliminacionRespuestaDomainTest {

    @ParameterizedTest
    @EnumSource(value = TipoSolicitud.class,
            names = {"NOVEDAD_PARA_EL_ASESOR", "NOVEDAD_PARA_EL_COORDINADOR"})
    void debeCrearLaEliminacion_cuandoLosTresDatosSonValidos(TipoSolicitud tipo) {
        // Arrange
        var solicitud = UUID.randomUUID();
        var responsable = UUID.randomUUID();

        // Act
        var eliminacion = EliminacionRespuestaDomain.crear(solicitud, responsable, tipo);

        // Assert
        assertThat(eliminacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(eliminacion.getResponsableUsuario()).isEqualTo(responsable);
        assertThat(eliminacion.getTipoEsperado()).isEqualTo(tipo);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoLaSolicitudEsNula() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaDomain.crear(
                        null, UUID.randomUUID(), TipoSolicitud.NOVEDAD_PARA_EL_ASESOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElResponsableEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaDomain.crear(
                        UUID.randomUUID(), null, TipoSolicitud.NOVEDAD_PARA_EL_ASESOR));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoElTipoEsperadoEsNulo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaDomain.crear(UUID.randomUUID(), UUID.randomUUID(), null));

        // Assert
        assertThat(excepcion.getValidationResult()
                .tieneErroresDeCampo(SolicitudesFields.Solicitud.TIPO_SOLICITUD)).isTrue();
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosDatosSonNulos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> EliminacionRespuestaDomain.crear(null, null, null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.TIPO_SOLICITUD)).isTrue();
        assertThat(resultado.getErrores()).hasSize(3);
    }
}
