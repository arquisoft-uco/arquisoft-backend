package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudiantesProyectoGradoAsignadosPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
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
    void debeConservarIdsIgnorandoNombreYCorreo_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var evento = new EstudiantesAsignadosEventoDePrueba(UtilUUID.generarNuevoUUID(),
                List.of(new ContactoDePrueba(primero, "Ana Gomez", "ana.gomez@soyuco.edu.co"),
                        new ContactoDePrueba(segundo, "Luis Diaz", "luis.diaz@soyuco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento),
                EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.estudiantes()).containsExactly(
                new EstudiantesProyectoGradoAsignadosPayload.EstudiantePayload(primero.toString()),
                new EstudiantesProyectoGradoAsignadosPayload.EstudiantePayload(segundo.toString()));
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaLaListaDeEstudiantes() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444",
                 "tituloProyecto":"Sistema","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
