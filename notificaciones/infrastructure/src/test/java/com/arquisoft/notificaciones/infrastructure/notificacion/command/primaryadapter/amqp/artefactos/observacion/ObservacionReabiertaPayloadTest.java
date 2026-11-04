package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.artefactos.observacion;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionReabiertaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    public record Contacto(String nombre, String email) {
    }

    private static final class EventoDePrueba extends DomainEvent {

        private final String documento;
        private final int numeroVersion;
        private final Contacto asesor;
        private final List<Contacto> estudiantes;
        private final String descripcion;

        private EventoDePrueba(String documento, int numeroVersion, Contacto asesor, List<Contacto> estudiantes,
                String descripcion) {
            super(EventTopics.Artefactos.OBSERVACION_REABIERTA, "ObservacionReabiertaEvent");
            this.documento = documento;
            this.numeroVersion = numeroVersion;
            this.asesor = asesor;
            this.estudiantes = estudiantes;
            this.descripcion = descripcion;
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

        public String getDescripcion() {
            return descripcion;
        }

        public List<Contacto> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarIdEventoYCampos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EventoDePrueba("Anteproyecto", 3, new Contacto("Ana Gomez", "ana@uco.edu.co"),
                List.of(new Contacto("Luis Perez", "luis@uco.edu.co"), new Contacto("Sara Mora", "sara@uco.edu.co")),
                "Falta la bibliografia");

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), ObservacionReabiertaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.documento()).isEqualTo("Anteproyecto");
        assertThat(payload.numeroVersion()).isEqualTo(3);
        assertThat(payload.descripcion()).isEqualTo("Falta la bibliografia");
        assertThat(payload.asesor().nombre()).isEqualTo("Ana Gomez");
        assertThat(payload.asesor().email()).isEqualTo("ana@uco.edu.co");
        assertThat(payload.estudiantes()).extracting(ObservacionReabiertaPayload.ContactoPayload::email)
                .containsExactly("luis@uco.edu.co", "sara@uco.edu.co");
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
        var payload = mapper.readValue(json, ObservacionReabiertaPayload.class);

        // Assert
        assertThat(payload.numeroVersion()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
