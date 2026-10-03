package com.arquisoft.solicitudes.domain.respuesta.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudRespondidaEventTest {

    private static final UsuarioDomain REMITENTE = UsuarioDomain.reconstruir(
            UUID.randomUUID(), "EST-1", "Ana Estudiante", "ana@uco.edu.co", Instant.now());
    private static final UsuarioDomain RESPONSABLE = UsuarioDomain.reconstruir(
            UUID.randomUUID(), "COO-1", "Pedro Coordinador", "pedro@uco.edu.co", Instant.now());

    @Test
    void debeCrearElEventoDelCoordinador_cuandoElTipoEsNovedadParaElCoordinador() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "No puedo asistir");

        // Act
        var evento = SolicitudRespondidaEvent.crear(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, respuesta, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento).isInstanceOf(SolicitudNovedadCoordinadorRespondidaEvent.class);
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Solicitudes.NOVEDAD_COORDINADOR_RESPONDIDA);
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorRespondidaEvent");
    }

    @Test
    void debeLlevarLosDatosDeLaRespuestaYLosUsuarios_cuandoConstruyeElEvento() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "No puedo asistir");

        // Act
        var evento = (SolicitudNovedadCoordinadorRespondidaEvent) SolicitudRespondidaEvent.crear(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, respuesta, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(respuesta.getSolicitud());
        assertThat(evento.getRespuestaId()).isEqualTo(respuesta.getId());
        assertThat(evento.getContenido()).isEqualTo("No puedo asistir");
        assertThat(evento.getEstadoRespuesta()).isEqualTo(respuesta.getEstadoRespuesta().getId());
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getCoordinadorNombre()).isEqualTo("Pedro Coordinador");
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoNoTieneEventoDeRespuesta() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "contenido");

        // Act & Assert
        assertThatThrownBy(() -> SolicitudRespondidaEvent.crear(
                TipoSolicitud.CAMBIO_DE_ASESOR, respuesta, REMITENTE, RESPONSABLE))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }
}
