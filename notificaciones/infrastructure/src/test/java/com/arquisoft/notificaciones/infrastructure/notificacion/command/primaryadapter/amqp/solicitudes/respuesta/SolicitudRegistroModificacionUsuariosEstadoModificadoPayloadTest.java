package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudRegistroModificacionUsuariosEstadoModificadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class SolicitudRegistroModificacionUsuariosEstadoModificadoEventoDePrueba extends DomainEvent {

        private final UUID solicitudId;
        private final String nuevoEstado;
        private final String nuevoEstadoNombre;
        private final String remitenteNombre;
        private final String remitenteEmail;
        private final String responsableNombre;

        private SolicitudRegistroModificacionUsuariosEstadoModificadoEventoDePrueba(
                UUID solicitudId, String nuevoEstado, String nuevoEstadoNombre,
                String remitenteNombre, String remitenteEmail, String responsableNombre) {
            super(EventTopics.Solicitudes.REGISTRO_MODIFICACION_USUARIOS_ESTADO_MODIFICADO,
                    "SolicitudRegistroModificacionUsuariosEstadoModificadoEvent");
            this.solicitudId = solicitudId;
            this.nuevoEstado = nuevoEstado;
            this.nuevoEstadoNombre = nuevoEstadoNombre;
            this.remitenteNombre = remitenteNombre;
            this.remitenteEmail = remitenteEmail;
            this.responsableNombre = responsableNombre;
        }

        public UUID getSolicitudId() {
            return solicitudId;
        }

        public String getNuevoEstado() {
            return nuevoEstado;
        }

        public String getNuevoEstadoNombre() {
            return nuevoEstadoNombre;
        }

        public String getRemitenteNombre() {
            return remitenteNombre;
        }

        public String getRemitenteEmail() {
            return remitenteEmail;
        }

        public String getResponsableNombre() {
            return responsableNombre;
        }
    }

    @Test
    void debeDeserializar_cuandoLlegaElJsonDelEvento() {
        // Arrange
        var evento = new SolicitudRegistroModificacionUsuariosEstadoModificadoEventoDePrueba(
                UtilUUID.generarNuevoUUID(), "APROBADA", "Aprobada",
                "Ana Estudiante", "ana.est@soyuco.edu.co", "Pedro Administrador");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), SolicitudRegistroModificacionUsuariosEstadoModificadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.solicitudId()).isEqualTo(evento.getSolicitudId().toString());
        assertThat(payload.nuevoEstado()).isEqualTo(evento.getNuevoEstado());
        assertThat(payload.nuevoEstadoNombre()).isEqualTo(evento.getNuevoEstadoNombre());
        assertThat(payload.remitenteNombre()).isEqualTo(evento.getRemitenteNombre());
        assertThat(payload.remitenteEmail()).isEqualTo(evento.getRemitenteEmail());
        assertThat(payload.responsableNombre()).isEqualTo(evento.getResponsableNombre());
    }

    @Test
    void debeDeclararExactamenteLosCamposDelEvento_cuandoSeInspeccionaElRecord() {
        // Act
        var campos = Arrays.stream(SolicitudRegistroModificacionUsuariosEstadoModificadoPayload.class.getRecordComponents())
                .map(componente -> componente.getName())
                .toList();

        // Assert
        assertThat(campos).containsExactly("idEvento", "ocurridoEn", "solicitudId", "nuevoEstado",
                "nuevoEstadoNombre", "remitenteNombre", "remitenteEmail", "responsableNombre");
    }
}
