package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.observacionitem;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemReabiertaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class ObservacionItemReabiertaEventoDePrueba extends DomainEvent {

        private final UUID observacionItemId;
        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final List<ContactoDePrueba> estudiantes;

        private ObservacionItemReabiertaEventoDePrueba(
                UUID observacionItemId, UUID fichaPerfilId, String tituloProyecto, List<ContactoDePrueba> estudiantes) {
            super(EventTopics.Fichas.OBSERVACION_ITEM_REABIERTA, "ObservacionItemReabiertaEvent");
            this.observacionItemId = observacionItemId;
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = tituloProyecto;
            this.estudiantes = estudiantes;
        }

        public UUID getObservacionItemId() {
            return observacionItemId;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarLosCamposDelEventoPublicado_cuandoSeLeeElPayload() {
        // Arrange
        var evento = new ObservacionItemReabiertaEventoDePrueba(
                UUID.randomUUID(), UUID.randomUUID(), "Sistema de gestion academica",
                List.of(new ContactoDePrueba("Ana Gomez", "ana.gomez@uco.edu.co"),
                        new ContactoDePrueba("Luis Perez", "luis.perez@uco.edu.co")));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), ObservacionItemReabiertaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.observacionItemId()).isEqualTo(evento.getObservacionItemId().toString());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo(evento.getTituloProyecto());
        assertThat(payload.estudiantes()).extracting(ObservacionItemReabiertaPayload.ContactoPayload::email)
                .containsExactly("ana.gomez@uco.edu.co", "luis.perez@uco.edu.co");
        assertThat(payload.estudiantes()).extracting(ObservacionItemReabiertaPayload.ContactoPayload::nombre)
                .containsExactly("Ana Gomez", "Luis Perez");
    }

    @Test
    void debeDejarOcurridoEnNuloEIgnorarCamposDesconocidos_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","observacionItemId":"11111111-1111-1111-1111-111111111111",
                 "fichaPerfilId":"22222222-2222-2222-2222-222222222222","tituloProyecto":"Sistema",
                 "campoNuevo":"ignorado",
                 "estudiantes":[{"nombre":"Ana Gomez","email":"ana.gomez@uco.edu.co"}]}
                """;

        // Act
        var payload = mapper.readValue(json, ObservacionItemReabiertaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.estudiantes()).hasSize(1);
    }
}
