package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.revisionasesor;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorCerradaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    public record Contacto(String nombre, String email) {
    }

    private static final class EventoDePrueba extends DomainEvent {

        private final String documento;
        private final int numeroVersion;
        private final Contacto asesor;
        private final List<Contacto> estudiantes;

        private EventoDePrueba(String documento, int numeroVersion, Contacto asesor, List<Contacto> estudiantes) {
            super(EventTopics.Artefactos.REVISION_ASESOR_CERRADA, "RevisionAsesorCerradaEvent");
            this.documento = documento;
            this.numeroVersion = numeroVersion;
            this.asesor = asesor;
            this.estudiantes = estudiantes;
        }

        public String getDocumento() {
            return documento;
        }

        public int getNumeroVersion() {
            return numeroVersion;
        }

        public Contacto getAsesor() {
            return asesor;
        }

        public List<Contacto> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarIdEventoYCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EventoDePrueba("Anteproyecto", 3, new Contacto("Ana Gomez", "ana@uco.edu.co"),
                List.of(new Contacto("Luis Perez", "luis@uco.edu.co"), new Contacto("Sara Mora", "sara@uco.edu.co")));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), RevisionAsesorCerradaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.numeroVersion()).isEqualTo(3);
        assertThat(payload.asesor().nombre()).isEqualTo("Ana Gomez");
        assertThat(payload.asesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(payload.estudiantes()).extracting(RevisionAsesorCerradaPayload.ContactoPayload::email)
                .containsExactly("luis@uco.edu.co", "sara@uco.edu.co");
    }

    @Test
    void debeIgnorarCamposNuevos_cuandoElEventoLosTrae() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","documento":"Anteproyecto","numeroVersion":3,
                 "proyectoGrado":"22222222-2222-2222-2222-222222222222",
                 "artefacto":"11111111-1111-1111-1111-111111111111",
                 "versionArtefacto":"33333333-3333-3333-3333-333333333333",
                 "uriArchivo":"pendientes/a.pdf","fechaVersion":"2026-11-09T08:00:00Z",
                 "versionRepositorioArtefacto":"44444444-4444-4444-4444-444444444444",
                 "repositorio":"55555555-5555-5555-5555-555555555555","nombreRepositorio":"Repo",
                 "asesor":{"nombre":"Ana","email":"ana@uco.edu.co"},
                 "estudiantes":[{"nombre":"Luis","email":"luis@uco.edu.co"}]}
                """;

        // Act
        var payload = mapper.readValue(json, RevisionAsesorCerradaPayload.class);

        // Assert
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.numeroVersion()).isEqualTo(3);
        assertThat(payload.asesor().nombre()).isEqualTo("Ana");
        assertThat(payload.estudiantes()).hasSize(1);
    }

    @Test
    void debeDejarNumeroVersionNuloYIgnorarCamposDesconocidos_cuandoElProductorEvoluciona() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","documento":"Anteproyecto",
                 "asesor":{"nombre":"Ana","email":"ana@uco.edu.co"},
                 "estudiantes":[],"campoNuevo":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, RevisionAsesorCerradaPayload.class);

        // Assert
        assertThat(payload.numeroVersion()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
