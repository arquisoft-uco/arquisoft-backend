package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

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

    private static final class AsignadosEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final List<EstudianteDePrueba> estudiantes;

        private AsignadosEventoDePrueba(UUID proyectoGradoId, String tituloProyecto,
                                        List<EstudianteDePrueba> estudiantes) {
            super(EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS,
                    "EstudiantesProyectoGradoAsignadosEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.tituloProyecto = tituloProyecto;
            this.estudiantes = estudiantes;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public String getTituloProyecto() {
            return tituloProyecto;
        }

        public List<EstudianteDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    private record EstudianteDePrueba(UUID id, String nombre, String email) {
    }

    @Test
    void debeConservarTodosLosCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var evento = new AsignadosEventoDePrueba(UtilUUID.generarNuevoUUID(), "Sistema", List.of(
                new EstudianteDePrueba(primero, "Laura", "laura@uco.edu.co"),
                new EstudianteDePrueba(segundo, "Pedro", "pedro@uco.edu.co")));

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.estudiantes()).extracting(EstudiantesProyectoGradoAsignadosPayload.EstudianteAsignado::id)
                .containsExactly(primero.toString(), segundo.toString());
    }

    @Test
    void debeDeserializarConNull_cuandoElEventoAnteriorNoTraeEstudiantes() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"44444444-4444-4444-4444-444444444444","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
