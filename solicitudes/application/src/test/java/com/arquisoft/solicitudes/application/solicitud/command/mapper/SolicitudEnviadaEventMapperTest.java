package com.arquisoft.solicitudes.application.solicitud.command.mapper;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudAmpliacionPlazoEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudCambioAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadCoordinadorEnviadaEvent;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudEnviadaEventMapperTest {

    private static final UsuarioDomain REMITENTE = UsuarioDomain.reconstruir(
            UUID.randomUUID(), "EST-1", "Ana Estudiante", "ana@uco.edu.co", Instant.now());
    private static final UsuarioDomain DESTINATARIO = UsuarioDomain.reconstruir(
            UUID.randomUUID(), "ASE-1", "Pedro Asesor", "pedro@uco.edu.co", Instant.now());

    private static SolicitudDomain solicitud(TipoSolicitud tipo) {
        return SolicitudDomain.crear(DESTINATARIO.getId(), REMITENTE.getId(), "mensaje", tipo);
    }

    @ParameterizedTest
    @CsvSource({
            "NOVEDAD_PARA_EL_ASESOR," + EventTopics.Solicitudes.NOVEDAD_ASESOR_ENVIADA,
            "NOVEDAD_PARA_EL_COORDINADOR," + EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ENVIADA,
            "CAMBIO_DE_ASESOR," + EventTopics.Solicitudes.CAMBIO_ASESOR_ENVIADA,
            "AMPLIACION_DE_PLAZO," + EventTopics.Solicitudes.AMPLIACION_PLAZO_ENVIADA
    })
    void debeConstruirElEventoDelTipo_cuandoElTipoEsEnviable(TipoSolicitud tipo, String topicEsperado) {
        // Arrange
        var solicitud = solicitud(tipo);

        // Act
        DomainEvent evento = SolicitudEnviadaEventMapper.toEvent(solicitud, REMITENTE, DESTINATARIO);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(topicEsperado);
    }

    @Test
    void debeLlevarLosDatosDeLaSolicitudYLosUsuarios_cuandoConstruyeElEvento() {
        // Arrange
        var solicitud = solicitud(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);

        // Act
        var evento = (SolicitudNovedadAsesorEnviadaEvent)
                SolicitudEnviadaEventMapper.toEvent(solicitud, REMITENTE, DESTINATARIO);

        // Assert
        assertThat(evento.getSolicitudId()).isEqualTo(solicitud.getId());
        assertThat(evento.getRemitenteNombre()).isEqualTo("Ana Estudiante");
        assertThat(evento.getDestinatarioNombre()).isEqualTo("Pedro Asesor");
        assertThat(evento.getDestinatarioEmail()).isEqualTo("pedro@uco.edu.co");
        assertThat(evento.getMensajeSolicitud()).isEqualTo("mensaje");
    }

    @Test
    void debeUsarLaClaseDeEventoPropiaDeCadaTipo_cuandoSeConstruye() {
        // Act & Assert
        assertThat(SolicitudEnviadaEventMapper.toEvent(
                solicitud(TipoSolicitud.NOVEDAD_PARA_EL_ASESOR), REMITENTE, DESTINATARIO))
                .isInstanceOf(SolicitudNovedadAsesorEnviadaEvent.class);
        assertThat(SolicitudEnviadaEventMapper.toEvent(
                solicitud(TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR), REMITENTE, DESTINATARIO))
                .isInstanceOf(SolicitudNovedadCoordinadorEnviadaEvent.class);
        assertThat(SolicitudEnviadaEventMapper.toEvent(
                solicitud(TipoSolicitud.CAMBIO_DE_ASESOR), REMITENTE, DESTINATARIO))
                .isInstanceOf(SolicitudCambioAsesorEnviadaEvent.class);
        assertThat(SolicitudEnviadaEventMapper.toEvent(
                solicitud(TipoSolicitud.AMPLIACION_DE_PLAZO), REMITENTE, DESTINATARIO))
                .isInstanceOf(SolicitudAmpliacionPlazoEnviadaEvent.class);
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoElTipoNoSeEnvia() {
        // Arrange
        var solicitud = solicitud(TipoSolicitud.REGISTRO_Y_MODIFICACION_DE_USUARIOS);

        // Act & Assert
        assertThatThrownBy(() -> SolicitudEnviadaEventMapper.toEvent(solicitud, REMITENTE, DESTINATARIO))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }

    @Test
    void debeLanzarTipoNoEncontrado_cuandoLaSolicitudEsElCentinelaVacio() {
        // Act & Assert
        assertThatThrownBy(() -> SolicitudEnviadaEventMapper.toEvent(
                SolicitudDomain.VACIO, REMITENTE, DESTINATARIO))
                .isInstanceOf(TipoSolicitudNoEncontradoException.class);
    }
}
