package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionFinalizadaPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(String nombre, String email) {
    }

    private static final class EvaluacionFinalizadaEventoDePrueba extends DomainEvent {

        private final UUID evaluacionId = UtilUUID.generarNuevoUUID();
        private final UUID entregableId;
        private final String nombreProyecto = "Sistema de riego";
        private final int versionEntregable = 2;
        private final String resultado;
        private final String resultadoNombre = "Aprobada";
        private final BigDecimal notaFinal = new BigDecimal("4.5");
        private final List<ContactoDePrueba> estudiantes = List.of(new ContactoDePrueba("Ana", "ana@uco.edu.co"));

        private EvaluacionFinalizadaEventoDePrueba(UUID entregableId, String resultado) {
            super(EventTopics.Evaluaciones.EVALUACION_FINALIZADA, "EvaluacionFinalizadaEvent");
            this.entregableId = entregableId;
            this.resultado = resultado;
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

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarLosCuatroCamposIgnorandoElResto_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var evento = new EvaluacionFinalizadaEventoDePrueba(entregable, "APROBADA_CON_OBSERVACIONES");

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EvaluacionFinalizadaPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.entregableId()).isEqualTo(entregable.toString());
        assertThat(payload.resultado()).isEqualTo("APROBADA_CON_OBSERVACIONES");
    }

    @Test
    void debeDeserializarConNull_cuandoFaltaElResultado() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","entregableId":"44444444-4444-4444-4444-444444444444","campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EvaluacionFinalizadaPayload.class);

        // Assert
        assertThat(payload.resultado()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.idEvento()).isEqualTo("evt-1");
    }
}
