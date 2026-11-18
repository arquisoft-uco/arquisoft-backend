package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CorreccionesRevisionItemEnviadasPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class CorreccionesEnviadasEventoDePrueba extends DomainEvent {

        private final UUID revisionItemId;
        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final ContactoDePrueba asesor;

        private CorreccionesEnviadasEventoDePrueba(
                UUID revisionItemId, UUID fichaPerfilId, String tituloProyecto, ContactoDePrueba asesor) {
            super(EventTopics.Fichas.REVISION_ITEM_CORRECCIONES_ENVIADAS, "CorreccionesRevisionItemEnviadasEvent");
            this.revisionItemId = revisionItemId;
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = tituloProyecto;
            this.asesor = asesor;
        }

        public UUID getRevisionItemId() {
            return revisionItemId;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }
    }

    @Test
    void debeConservarLosCamposDelEventoPublicado_cuandoSeLeeElPayload() {
        // Arrange
        var evento = new CorreccionesEnviadasEventoDePrueba(
                UUID.randomUUID(), UUID.randomUUID(), "Sistema de gestion academica",
                new ContactoDePrueba("Luis Perez", "luis.perez@uco.edu.co"));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), CorreccionesRevisionItemEnviadasPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.revisionItemId()).isEqualTo(evento.getRevisionItemId().toString());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo(evento.getTituloProyecto());
        assertThat(payload.asesor().nombre()).isEqualTo("Luis Perez");
        assertThat(payload.asesor().email()).isEqualTo("luis.perez@uco.edu.co");
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","revisionItemId":"11111111-1111-1111-1111-111111111111",
                 "fichaPerfilId":"22222222-2222-2222-2222-222222222222","tituloProyecto":"Sistema",
                 "asesor":{"nombre":"Luis Perez","email":"luis.perez@uco.edu.co"}}
                """;

        // Act
        var payload = mapper.readValue(json, CorreccionesRevisionItemEnviadasPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
