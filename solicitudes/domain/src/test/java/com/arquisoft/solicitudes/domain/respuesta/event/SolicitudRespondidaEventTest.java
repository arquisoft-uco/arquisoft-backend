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
    void debeAsignarTodosLosCampos_cuandoSeConstruye() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "No puedo asistir");

        // Act
        var evento = new SolicitudRespondidaEvent(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, respuesta, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(respuesta.getSolicitud());
        assertThat(evento.getRespuestaId()).isEqualTo(respuesta.getId());
        assertThat(evento.getContenido()).isEqualTo("No puedo asistir");
        assertThat(evento.getEstadoRespuesta()).isEqualTo(respuesta.getEstadoRespuesta().getId());
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getRemitenteEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getResponsableNombre()).isEqualTo("Pedro Coordinador");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }

    @Test
    void debeExponerElTemaYElTipoDeEventoDelCoordinador_cuandoElTipoEsNovedadParaElCoordinador() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "No puedo asistir");

        // Act
        var evento = new SolicitudRespondidaEvent(
                TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, respuesta, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Solicitudes.NOVEDAD_COORDINADOR_RESPONDIDA);
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.respuesta.novedad_coordinador_respondida");
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadCoordinadorRespondidaEvent");
    }

    @Test
    void debeExponerElTemaYElTipoDeEventoDelAsesor_cuandoElTipoEsNovedadParaElAsesor() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "Puedes presentar el lunes");

        // Act
        var evento = new SolicitudRespondidaEvent(
                TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, respuesta, REMITENTE, RESPONSABLE);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Solicitudes.NOVEDAD_ASESOR_RESPONDIDA);
        assertThat(evento.getTemaEvento()).isEqualTo("solicitudes.respuesta.novedad_asesor_respondida");
        assertThat(evento.getTipoEvento()).isEqualTo("SolicitudNovedadAsesorRespondidaEvent");
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoNoTieneEventoDeRespuesta() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "contenido");

        // Act & Assert
        assertThatThrownBy(() -> new SolicitudRespondidaEvent(
                TipoSolicitud.CAMBIO_DE_ASESOR, respuesta, REMITENTE, RESPONSABLE))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoEsElCentinelaVacio() {
        // Arrange
        var respuesta = RespuestaDomain.crear(UUID.randomUUID(), "contenido");

        // Act & Assert
        assertThatThrownBy(() -> new SolicitudRespondidaEvent(
                TipoSolicitud.VACIO, respuesta, REMITENTE, RESPONSABLE))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }
}
