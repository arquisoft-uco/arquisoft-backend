package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.fichaperfil;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FichaPerfilNoAprobadaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class FichaPerfilNoAprobadaEventoDePrueba extends DomainEvent {

        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final ContactoDePrueba asesor;
        private final List<ContactoDePrueba> estudiantes;

        private FichaPerfilNoAprobadaEventoDePrueba(UUID fichaPerfilId, ContactoDePrueba asesor,
                                                    List<ContactoDePrueba> estudiantes) {
            super(EventTopics.Fichas.FICHA_PERFIL_NO_APROBADA, "FichaPerfilNoAprobadaEvent");
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = "Sistema de gestión";
            this.asesor = asesor;
            this.estudiantes = estudiantes;
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

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarLosDestinatarios_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new FichaPerfilNoAprobadaEventoDePrueba(UUID.randomUUID(),
                new ContactoDePrueba("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co"),
                List.of(new ContactoDePrueba("Ana Gomez", "ana.gomez@soyuco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), FichaPerfilNoAprobadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.asesor()).isEqualTo(
                new FichaPerfilNoAprobadaPayload.ContactoPayload("Carlos Ruiz", "carlos.ruiz@soyuco.edu.co"));
        assertThat(payload.estudiantes()).containsExactly(
                new FichaPerfilNoAprobadaPayload.ContactoPayload("Ana Gomez", "ana.gomez@soyuco.edu.co"));
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","fichaPerfilId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","estudiantes":[]}
                """;

        // Act
        var payload = mapper.readValue(json, FichaPerfilNoAprobadaPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.asesor()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
