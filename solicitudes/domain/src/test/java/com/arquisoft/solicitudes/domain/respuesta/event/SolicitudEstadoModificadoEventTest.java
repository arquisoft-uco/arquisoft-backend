package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEstadoModificadoEventTest {

    private static final UsuarioDomain REMITENTE = UsuarioDomain.reconstruir(
            UUID.randomUUID(), "EST-1", "Ana Estudiante", "ana@uco.edu.co", Instant.now());
    private static final UsuarioDomain RESPONSABLE = UsuarioDomain.reconstruir(
            UUID.randomUUID(), "COO-1", "Pedro Coordinador", "pedro@uco.edu.co", Instant.now());

    @Test
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        var solicitud = UUID.randomUUID();

        // Act
        var evento = new SolicitudEstadoModificadoEvent(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, solicitud,
                EstadoRespuesta.APROBADA, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitud);
        assertThat(evento.getNuevoEstado()).isEqualTo(EstadoRespuesta.APROBADA.getId());
        assertThat(evento.getNuevoEstadoNombre()).isEqualTo(EstadoRespuesta.APROBADA.getNombre());
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getResponsableNombre()).isEqualTo("Pedro Coordinador");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @Test
    void debeExponerElTemaYElTipoDeEventoDelCoordinador_cuandoElTipoEsNovedadParaElCoordinador() {
        // Act
        var evento = new SolicitudEstadoModificadoEvent(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, UUID.randomUUID(),
                EstadoRespuesta.APROBADA, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ESTADO_MODIFICADO);
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.respuesta.novedad_coordinador_estado_modificado");
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorEstadoModificadoEvent");
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoNoTieneEventoDeEstadoModificado() {
        // Act & Assert
        assertThatThrownBy(() -> new SolicitudEstadoModificadoEvent(
                TipoSolicitud.CAMBIO_DE_ASESOR, UUID.randomUUID(),
                EstadoRespuesta.APROBADA, REMITENTE, RESPONSABLE))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoEsElCentinelaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> new SolicitudEstadoModificadoEvent(
                TipoSolicitud.VACIO, UUID.randomUUID(),
                EstadoRespuesta.APROBADA, REMITENTE, RESPONSABLE))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }
}
