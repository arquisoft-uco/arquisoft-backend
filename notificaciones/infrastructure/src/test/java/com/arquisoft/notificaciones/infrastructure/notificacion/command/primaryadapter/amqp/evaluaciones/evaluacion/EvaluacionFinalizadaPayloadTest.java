package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFinalizadaPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID id, String nombre, String email) {
    }

    private static final class EvaluacionFinalizadaEventoDePrueba extends DomainEvent {

        private final UUID evaluacionId = UUID.randomUUID();
        private final UUID entregableId = UUID.randomUUID();
        private final String nombreProyecto = "Sistema de gestión";
        private final int versionEntregable = 3;
        private final String resultado = "APROBADA";
        private final String resultadoNombre = "Aprobada";
        private final BigDecimal notaFinal = new BigDecimal("3.25");
        private final ContactoDePrueba coordinador =
                new ContactoDePrueba(UUID.randomUUID(), "Carlos Coord", "carlos@uco.edu.co");
        private final List<ContactoDePrueba> estudiantes =
                List.of(new ContactoDePrueba(UUID.randomUUID(), "Ana Gomez", "ana@uco.edu.co"));
        private final ContactoDePrueba asesor =
                new ContactoDePrueba(UUID.randomUUID(), "Alba Asesora", "alba@uco.edu.co");

        private EvaluacionFinalizadaEventoDePrueba() {
            super(EventTopics.Evaluaciones.EVALUACION_FINALIZADA, "EvaluacionFinalizadaEvent");
        }

        public UUID getEvaluacionId() {
            return evaluacionId;
        }

        public UUID getEntregableId() {
            return entregableId;
        }

        public String getNombreProyecto() {
            return nombreProyecto;
        }

        public int getVersionEntregable() {
            return versionEntregable;
        }

        public String getResultado() {
            return resultado;
        }

        public String getResultadoNombre() {
            return resultadoNombre;
        }

        public BigDecimal getNotaFinal() {
            return notaFinal;
        }

        public ContactoDePrueba getCoordinador() {
            return coordinador;
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }

        public ContactoDePrueba getAsesor() {
            return asesor;
        }
    }

    @Test
    void debeConservarLosCamposQueNecesitaElCorreo_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new EvaluacionFinalizadaEventoDePrueba();

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EvaluacionFinalizadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.evaluacionId()).isEqualTo(evento.getEvaluacionId().toString());
        assertThat(payload.entregableId()).isEqualTo(evento.getEntregableId().toString());
        assertThat(payload.nombreProyecto()).isEqualTo("Sistema de gestión");
        assertThat(payload.versionEntregable()).isEqualTo(3);
        assertThat(payload.resultado()).isEqualTo("APROBADA");
        assertThat(payload.resultadoNombre()).isEqualTo("Aprobada");
        assertThat(payload.notaFinal()).isEqualByComparingTo("3.25");
        assertThat(payload.coordinador())
                .isEqualTo(new EvaluacionFinalizadaPayload.ContactoPayload("Carlos Coord", "carlos@uco.edu.co"));
        assertThat(payload.asesor())
                .isEqualTo(new EvaluacionFinalizadaPayload.ContactoPayload("Alba Asesora", "alba@uco.edu.co"));
        assertThat(payload.estudiantes()).containsExactly(
                new EvaluacionFinalizadaPayload.ContactoPayload("Ana Gomez", "ana@uco.edu.co"));
    }

    @Test
    void debeDeserializarConNulls_cuandoElEventoLlegaSinCamposOConCamposDesconocidos() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","resultado":"RECHAZADA","campoNuevo":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EvaluacionFinalizadaPayload.class);

        // Assert
        assertThat(payload.resultado()).isEqualTo("RECHAZADA");
        assertThat(payload.estudiantes()).isNull();
        assertThat(payload.coordinador()).isNull();
    }
}
