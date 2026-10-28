package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.solicitud;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudRegistroModificacionUsuariosEnviadaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class SolicitudRegistroModificacionUsuariosEnviadaEventoDePrueba extends DomainEvent {

        private final UUID solicitudId;
        private final String remitenteNombre;
        private final String mensajeSolicitud;
        private final String destinatarioNombre;
        private final String destinatarioEmail;

        private SolicitudRegistroModificacionUsuariosEnviadaEventoDePrueba(
                UUID solicitudId, String remitenteNombre, String mensajeSolicitud,
                String destinatarioNombre, String destinatarioEmail) {
            super(EventTopics.Solicitudes.REGISTRO_MODIFICACION_USUARIOS_ENVIADA,
                    "SolicitudRegistroModificacionUsuariosEnviadaEvent");
            this.solicitudId = solicitudId;
            this.remitenteNombre = remitenteNombre;
            this.mensajeSolicitud = mensajeSolicitud;
            this.destinatarioNombre = destinatarioNombre;
            this.destinatarioEmail = destinatarioEmail;
        }

        public UUID getSolicitudId() {
            return solicitudId;
        }

        public String getRemitenteNombre() {
            return remitenteNombre;
        }

        public String getMensajeSolicitud() {
            return mensajeSolicitud;
        }

        public String getDestinatarioNombre() {
            return destinatarioNombre;
        }

        public String getDestinatarioEmail() {
            return destinatarioEmail;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new SolicitudRegistroModificacionUsuariosEnviadaEventoDePrueba(
                UtilUUID.generarNuevoUUID(), "Ana Coordinadora", "Registrar un usuario",
                "Pedro Administrador", "pedro.admin@soyuco.edu.co");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), SolicitudRegistroModificacionUsuariosEnviadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.solicitudId()).isEqualTo(evento.getSolicitudId().toString());
        assertThat(payload.remitenteNombre()).isEqualTo(evento.getRemitenteNombre());
        assertThat(payload.mensajeSolicitud()).isEqualTo(evento.getMensajeSolicitud());
        assertThat(payload.destinatarioNombre()).isEqualTo(evento.getDestinatarioNombre());
        assertThat(payload.destinatarioEmail()).isEqualTo(evento.getDestinatarioEmail());
    }
}
