package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectosEventosPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class AsignadosEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final String tituloProyecto;
        private final List<ContactoDePrueba> estudiantes;

        private AsignadosEventoDePrueba(UUID proyectoGradoId, String tituloProyecto,
                                        List<ContactoDePrueba> estudiantes) {
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

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    private static final class DestituidoEventoDePrueba extends DomainEvent {

        private final UUID proyectoGradoId;
        private final UUID estudianteId;

        private DestituidoEventoDePrueba(UUID proyectoGradoId, UUID estudianteId) {
            super(EventTopics.Proyectos.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO,
                    "EstudianteProyectoGradoDestituidoEvent");
            this.proyectoGradoId = proyectoGradoId;
            this.estudianteId = estudianteId;
        }

        public UUID getProyectoGradoId() {
            return proyectoGradoId;
        }

        public UUID getEstudianteId() {
            return estudianteId;
        }
    }

    @Test
    void debeConservarLosCamposYTolerarContactoExtra_cuandoSeLeeElEventoAsignados() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var evento = new AsignadosEventoDePrueba(UtilUUID.generarNuevoUUID(), "Sistema de riego",
                List.of(new ContactoDePrueba(estudiante, "Ana Perez", "ana@uco.edu.co")));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EstudiantesProyectoGradoAsignadosPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.tituloProyecto()).isEqualTo("Sistema de riego");
        assertThat(payload.estudiantes()).singleElement()
                .satisfies(contacto -> assertThat(contacto.id()).isEqualTo(estudiante.toString()));
    }

    @Test
    void debeConservarLosCampos_cuandoSeLeeElEventoDestituido() {
        // Arrange
        var evento = new DestituidoEventoDePrueba(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EstudianteProyectoGradoDestituidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.proyectoGradoId()).isEqualTo(evento.getProyectoGradoId().toString());
        assertThat(payload.estudianteId()).isEqualTo(evento.getEstudianteId().toString());
    }
}
