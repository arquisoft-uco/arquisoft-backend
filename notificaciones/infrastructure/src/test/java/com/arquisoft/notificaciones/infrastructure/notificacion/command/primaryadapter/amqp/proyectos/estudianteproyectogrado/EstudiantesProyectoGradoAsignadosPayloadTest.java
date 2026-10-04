package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudiantesProyectoGradoAsignadosPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class EstudiantesAsignadosEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final List<ContactoDePrueba> estudiantes;

        private EstudiantesAsignadosEventoDePrueba(UUID proyectoGradoId, List<ContactoDePrueba> estudiantes) {
            super(EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS, "EstudiantesProyectoGradoAsignadosEvent");
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
    void debeConservarLosEstudiantes_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EstudiantesAsignadosEventoDePrueba(UUID.randomUUID(),
                List.of(new ContactoDePrueba("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                        new ContactoDePrueba("Luis Diaz", "luis.diaz@soyuco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento),
                EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.estudiantes()).containsExactly(
                new EstudiantesProyectoGradoAsignadosPayload.ContactoPayload("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                new EstudiantesProyectoGradoAsignadosPayload.ContactoPayload("Luis Diaz", "luis.diaz@soyuco.edu.co"));
    }

    @Test
    void debeDejarOcurridoEnNulo_cuandoElProductorAunNoLoEnvia() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444",
                 "tituloProyecto":"Sistema","estudiantes":[]}
                """;

        // Act
        var payload = mapper.readValue(json, EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
