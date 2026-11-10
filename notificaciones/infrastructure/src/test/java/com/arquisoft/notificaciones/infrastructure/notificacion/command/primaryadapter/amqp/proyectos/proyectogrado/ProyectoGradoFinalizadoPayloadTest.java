package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoFinalizadoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class ProyectoGradoFinalizadoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final List<ContactoDePrueba> estudiantes;

        private ProyectoGradoFinalizadoEventoDePrueba(UUID proyectoGradoId, List<ContactoDePrueba> estudiantes) {
            super(EventTopics.Proyectos.PROYECTO_GRADO_FINALIZADO, "ProyectoGradoFinalizadoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = "Sistema de gestión";
            this.estudiantes = estudiantes;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarLosDestinatarios_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new ProyectoGradoFinalizadoEventoDePrueba(UUID.randomUUID(),
                List.of(new ContactoDePrueba(UUID.randomUUID(), "Ana Ruiz", "ana.ruiz@soyuco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ProyectoGradoFinalizadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.estudiantes()).containsExactly(
                new ProyectoGradoFinalizadoPayload.ContactoPayload("Ana Ruiz", "ana.ruiz@soyuco.edu.co"));
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"11111111-1111-1111-1111-111111111111",
                 "tituloProyecto":"Sistema","estudiantes":[]}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoFinalizadoPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.estudiantes()).isEmpty();
    }
}
