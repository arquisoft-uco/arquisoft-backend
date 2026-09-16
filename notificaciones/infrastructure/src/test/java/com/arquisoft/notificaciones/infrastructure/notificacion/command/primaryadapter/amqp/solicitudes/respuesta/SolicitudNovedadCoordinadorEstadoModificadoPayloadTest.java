package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.solicitudes.respuesta;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolicitudNovedadCoordinadorEstadoModificadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class SolicitudNovedadCoordinadorEstadoModificadoEventoDePrueba extends DomainEvent {

        private final UUID solicitudId;
        private final String nuevoEstado;
        private final String nuevoEstadoNombre;
        private final String remitenteNombre;
        private final String remitenteEmail;
        private final String coordinadorNombre;

        private SolicitudNovedadCoordinadorEstadoModificadoEventoDePrueba(
                UUID solicitudId, String nuevoEstado, String nuevoEstadoNombre,
                String remitenteNombre, String remitenteEmail, String coordinadorNombre) {
            super(EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ESTADO_MODIFICADO,
                    "SolicitudNovedadCoordinadorEstadoModificadoEvent");
            this.solicitudId = solicitudId;
            this.nuevoEstado = nuevoEstado;
            this.nuevoEstadoNombre = nuevoEstadoNombre;
            this.remitenteNombre = remitenteNombre;
            this.remitenteEmail = remitenteEmail;
            this.coordinadorNombre = coordinadorNombre;
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

        public String getCoordinadorNombre() {
            return coordinadorNombre;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new SolicitudNovedadCoordinadorEstadoModificadoEventoDePrueba(
                UUID.randomUUID(), "APROBADA", "Aprobada",
                "Ana Estudiante", "ana.est@soyuco.edu.co", "Pedro Coordinador");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), SolicitudNovedadCoordinadorEstadoModificadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.solicitudId()).isEqualTo(evento.getSolicitudId().toString());
        assertThat(payload.nuevoEstado()).isEqualTo(evento.getNuevoEstado());
        assertThat(payload.nuevoEstadoNombre()).isEqualTo(evento.getNuevoEstadoNombre());
        assertThat(payload.remitenteNombre()).isEqualTo(evento.getRemitenteNombre());
        assertThat(payload.remitenteEmail()).isEqualTo(evento.getRemitenteEmail());
        assertThat(payload.coordinadorNombre()).isEqualTo(evento.getCoordinadorNombre());
    }

    @Test
    void debeDejarNuevoEstadoNombreNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        String json = """
                {"idEvento":"evt-1","solicitudId":"11111111-1111-1111-1111-111111111111",
                 "nuevoEstado":"APROBADA","remitenteNombre":"Ana Estudiante",
                 "remitenteEmail":"ana.est@soyuco.edu.co","coordinadorNombre":"Pedro Coordinador"}
                """;

        // Act
        var payload = mapper.readValue(json, SolicitudNovedadCoordinadorEstadoModificadoPayload.class);

        // Assert
        assertThat(payload.nuevoEstadoNombre()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
