package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.evaluaciones.evaluacioncualitativajurado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionesCualitativasJuradoRegistradasPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    public record ContactoDePrueba(UUID estudiante, String email) {}

    public record DatosLoteDePrueba(
            UUID evaluacionJuradoId, UUID evaluacionId, UUID entregableId, UUID juradoId,
            String proyecto, int versionEntregable, int cantidad) {}

    private static final class EvaluacionesCualitativasJuradoRegistradasEventoDePrueba extends DomainEvent {

        private final DatosLoteDePrueba datos;
        private final List<ContactoDePrueba> estudiantes;

        private EvaluacionesCualitativasJuradoRegistradasEventoDePrueba(
                DatosLoteDePrueba datos, List<ContactoDePrueba> estudiantes) {
            super(EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS,
                    "EvaluacionesCualitativasJuradoRegistradasEvent");
            this.datos = datos;
            this.estudiantes = estudiantes;
        }

        public UUID getEvaluacionJuradoId() {
            return datos.evaluacionJuradoId();
        }

        public UUID getEvaluacionId() {
            return datos.evaluacionId();
        }

        public UUID getEntregableId() {
            return datos.entregableId();
        }

        public UUID getJuradoId() {
            return datos.juradoId();
        }

        public String getProyecto() {
            return datos.proyecto();
        }

        public int getVersionEntregable() {
            return datos.versionEntregable();
        }

        public int getCantidad() {
            return datos.cantidad();
        }

        public List<ContactoDePrueba> getEstudiantes() {
            return estudiantes;
        }
    }

    @Test
    void debeConservarTodosLosCamposYContactos_cuandoSeLeeElEventoPublicado() {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        var evento = new EvaluacionesCualitativasJuradoRegistradasEventoDePrueba(
                new DatosLoteDePrueba(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        "Sistema de gestión académica", 2, 3),
                List.of(new ContactoDePrueba(estudiante, "estudiante@uco.edu.co")));

        // Act
        var payload = mapper.readValue(
                mapper.writeValueAsString(evento), EvaluacionesCualitativasJuradoRegistradasPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.evaluacionJuradoId()).isEqualTo(evento.getEvaluacionJuradoId().toString());
        assertThat(payload.evaluacionId()).isEqualTo(evento.getEvaluacionId().toString());
        assertThat(payload.entregableId()).isEqualTo(evento.getEntregableId().toString());
        assertThat(payload.juradoId()).isEqualTo(evento.getJuradoId().toString());
        assertThat(payload.proyecto()).isEqualTo("Sistema de gestión académica");
        assertThat(payload.versionEntregable()).isEqualTo(2);
        assertThat(payload.cantidad()).isEqualTo(3);
        assertThat(payload.estudiantes()).hasSize(1);
        assertThat(payload.estudiantes().get(0).estudiante()).isEqualTo(estudiante.toString());
        assertThat(payload.estudiantes().get(0).email()).isEqualTo("estudiante@uco.edu.co");
    }

    @Test
    void debeLeerListaVaciaDeEstudiantes_cuandoElProductorNoEnviaElCampo() {
        // Arrange
        String json = """
                {"idEvento":"evt-1","evaluacionJuradoId":"11111111-1111-1111-1111-111111111111",
                 "proyecto":"Sistema","cantidad":1}
                """;

        // Act
        var payload = mapper.readValue(json, EvaluacionesCualitativasJuradoRegistradasPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.estudiantes()).isNull();
    }

    @Test
    void debeDeserializarSinLanzar_cuandoEsElJsonRealCapturadoDeLaDeadLetter() {
        // Arrange — captura literal de RabbitMQ management (mensaje real rechazado)
        String json = "{\"estudiantes\":[{\"estudiante\":\"b9137aff-2723-4104-95e8-c4915eaaec80\","
                + "\"email\":\"b9137aff-2723-4104-95e8-c4915eaaec80@stub.arquisoft.local\"}],"
                + "\"cantidad\":1,\"entregableId\":\"22222222-2222-2222-2222-222222222222\","
                + "\"evaluacionId\":\"55555555-5555-5555-5555-555555555555\","
                + "\"evaluacionJuradoId\":\"6f5f111b-2593-48fb-858a-2a6a031600de\","
                + "\"idEvento\":\"f60da856-8bc6-47aa-b40d-61aa54004fd9\","
                + "\"juradoId\":\"ec6e985d-49f3-438a-ac5a-74d9f1bcfaee\","
                + "\"ocurridoEn\":\"2026-09-12T17:55:58.703596900Z\","
                + "\"proyecto\":\"Proyecto de grado X\","
                + "\"temaEvento\":\"evaluaciones.evaluacion_cualitativa_jurado.registradas\","
                + "\"tipoEvento\":\"EvaluacionesCualitativasJuradoRegistradasEvent\","
                + "\"versionEntregable\":1}";

        // Act
        var payload = mapper.readValue(json, EvaluacionesCualitativasJuradoRegistradasPayload.class);

        // Assert
        assertThat(payload.estudiantes()).hasSize(1);
        assertThat(payload.estudiantes().get(0).estudiante())
                .isEqualTo("b9137aff-2723-4104-95e8-c4915eaaec80");
    }
}
