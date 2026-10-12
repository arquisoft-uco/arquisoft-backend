package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoRegistradoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class ProyectoGradoRegistradoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final UUID fichaPerfilId;
        private final String tituloProyecto;
        private final ContactoDePrueba coordinador;

        private ProyectoGradoRegistradoEventoDePrueba(UUID proyectoGradoId, UUID fichaPerfilId,
                                                      ContactoDePrueba coordinador) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO, "ProyectoGradoRegistradoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.fichaPerfilId = fichaPerfilId;
            this.tituloProyecto = "Sistema de gestión";
            this.coordinador = coordinador;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public UUID getFichaPerfilId() {
            return fichaPerfilId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public ContactoDePrueba getCoordinador() {
            return coordinador;
        }
    }

    @Test
    void debeConservarElCoordinador_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new ProyectoGradoRegistradoEventoDePrueba(UUID.randomUUID(), UUID.randomUUID(),
                new ContactoDePrueba("Laura Mesa", "laura.mesa@uco.edu.co"));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.fichaPerfilId()).isEqualTo(evento.getFichaPerfilId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.coordinador()).isEqualTo(
                new ProyectoGradoRegistradoPayload.ContactoPayload("Laura Mesa", "laura.mesa@uco.edu.co"));
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444",
                 "fichaPerfilId":"11111111-1111-1111-1111-111111111111","tituloProyecto":"Sistema",
                 "coordinador":{"nombre":"Laura Mesa","email":"laura.mesa@uco.edu.co"}}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
