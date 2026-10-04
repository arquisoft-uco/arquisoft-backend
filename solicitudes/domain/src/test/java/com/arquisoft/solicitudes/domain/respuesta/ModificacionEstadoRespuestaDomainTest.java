package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModificacionEstadoRespuestaDomainTest {

    @Test
    void debeCrearLaModificacionConElEstadoTipado_cuandoLosCuatroDatosSonValidos() {
        // Arrange
        var solicitud = UUID.randomUUID();
        var responsableUsuario = UUID.randomUUID();

        // Act
        var modificacion = ModificacionEstadoRespuestaDomain.crear(
                solicitud, responsableUsuario, "APROBADA", TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(modificacion.getSolicitud()).isEqualTo(solicitud);
        assertThat(modificacion.getResponsableUsuario()).isEqualTo(responsableUsuario);
        assertThat(modificacion.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
        assertThat(modificacion.getTipoEsperado()).isEqualTo(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
    }

    @Test
    void debeAceptarElEstadoRecortado_cuandoLlegaConEspaciosAlrededor() {
        // Act
        var modificacion = ModificacionEstadoRespuestaDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), "  APROBADA  ", TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        // Assert
        assertThat(modificacion.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA);
    }

    @Test
    void debeAcumularLosCuatroErrores_cuandoLosCuatroDatosSonInvalidos() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaDomain.crear(null, null, "   ", null));

        // Assert
        var resultado = excepcion.getValidationResult();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.ID)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.DESTINATARIO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Respuesta.ESTADO)).isTrue();
        assertThat(resultado.tieneErroresDeCampo(SolicitudesFields.Solicitud.TIPO_SOLICITUD)).isTrue();
        assertThat(resultado.getErrores()).hasSize(4);
    }

    @Test
    void debeAcumularElErrorDeCatalogo_cuandoElNuevoEstadoNoPerteneceAlCatalogo() {
        // Act
        var excepcion = assertThrows(DomainValidationException.class,
                () -> ModificacionEstadoRespuestaDomain.crear(
                        null, UUID.randomUUID(), "XYZ", TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).hasSize(2);
        assertThat(errores).anySatisfy(error -> {
            assertThat(error.campo()).isEqualTo(SolicitudesFields.Respuesta.ESTADO);
            assertThat(error.codigoError()).isEqualTo(SolicitudesCodes.EstadoRespuesta.ESTADO_NO_ENCONTRADO);
        });
    }
}
