package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.asesorficha;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaCambiadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class AsesorFichaCambiadoEventoDePrueba extends DomainEvent {

        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final String asesorNombre;
        private final String asesorEmail;

        private AsesorFichaCambiadoEventoDePrueba(
                UUID fichaPerfilId, String tituloProyecto,
                String asesorNombre, String asesorEmail) {
            super(EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO, "AsesorFichaCambiadoEvent");
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = tituloProyecto;
            this.asesorNombre = asesorNombre;
            this.asesorEmail = asesorEmail;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public String getAsesorNombre() {
            return asesorNombre;
        }

        public String getAsesorEmail() {
            return asesorEmail;
        }
    }

    @Test
    void debeConservarIdEventoYOcurridoEn_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new AsesorFichaCambiadoEventoDePrueba(
                UUID.randomUUID(), "Sistema de gestion academica",
                "Ana Gomez", "ana.gomez@soyuco.edu.co");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), AsesorFichaCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo(evento.getTituloProyecto());
        assertThat(payload.asesorNombre()).isEqualTo(evento.getAsesorNombre());
        assertThat(payload.asesorEmail()).isEqualTo(evento.getAsesorEmail());
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        String json = """
                {"idEvento":"evt-1","fichaPerfilId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","asesorNombre":"Ana Gomez",
                 "asesorEmail":"ana.gomez@soyuco.edu.co"}
                """;

        // Act
        var payload = mapper.readValue(json, AsesorFichaCambiadoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
