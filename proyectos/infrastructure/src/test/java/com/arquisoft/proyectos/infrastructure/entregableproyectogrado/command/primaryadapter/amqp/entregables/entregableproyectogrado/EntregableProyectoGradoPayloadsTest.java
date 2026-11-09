package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoPayloadsTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class EntregableEventoDePrueba extends DomainEvent {

        private final UUID entregableProyectoGrado;
        private final UUID proyectoGrado;
        private final int version;
        private final String campoExtra = "ignorado";

        private EntregableEventoDePrueba(String tema, UUID entregableProyectoGrado, UUID proyectoGrado, int version) {
            super(tema, "EntregableEventoDePrueba");
            this.entregableProyectoGrado = entregableProyectoGrado;
            this.proyectoGrado = proyectoGrado;
            this.version = version;
        }

        public UUID getEntregableProyectoGrado() {
            return entregableProyectoGrado;
        }

        public UUID getProyectoGrado() {
            return proyectoGrado;
        }

        public int getVersion() {
            return version;
        }

        public String getCampoExtra() {
            return campoExtra;
        }
    }

    @Test
    void debeLeerLosCamposQueUsaProyectosEIgnorarElResto_cuandoSeLeenLosTresEventosPublicados() {
        // Arrange
        var entregable = UUID.randomUUID();
        var proyectoGrado = UUID.randomUUID();
        var generado = new EntregableEventoDePrueba(
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO, entregable, proyectoGrado, 2);
        var cerrado = new EntregableEventoDePrueba(
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO, entregable, proyectoGrado, 2);
        var cancelado = new EntregableEventoDePrueba(
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO, entregable, proyectoGrado, 2);
        var rechazado = new EntregableEventoDePrueba(
                EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_RECHAZADO, entregable, proyectoGrado, 2);

        // Act
        var payloadGenerado = mapper.readValue(
                mapper.writeValueAsString(generado), EntregableProyectoGradoGeneradoPayload.class);
        var payloadCerrado = mapper.readValue(
                mapper.writeValueAsString(cerrado), EntregableProyectoGradoCerradoPayload.class);
        var payloadCancelado = mapper.readValue(
                mapper.writeValueAsString(cancelado), EntregableProyectoGradoCanceladoPayload.class);
        var payloadRechazado = mapper.readValue(
                mapper.writeValueAsString(rechazado), EntregableProyectoGradoRechazadoPayload.class);

        // Assert
        assertThat(payloadGenerado.idEvento()).isEqualTo(generado.getIdEvento());
        assertThat(payloadGenerado.ocurridoEn()).isEqualTo(generado.getOcurridoEn());
        assertThat(payloadGenerado.entregableProyectoGrado()).isEqualTo(entregable.toString());
        assertThat(payloadGenerado.proyectoGrado()).isEqualTo(proyectoGrado.toString());
        assertThat(payloadGenerado.version()).isEqualTo(2);
        assertThat(payloadCerrado.entregableProyectoGrado()).isEqualTo(entregable.toString());
        assertThat(payloadCerrado.version()).isEqualTo(2);
        assertThat(payloadCancelado.proyectoGrado()).isEqualTo(proyectoGrado.toString());
        assertThat(payloadCancelado.ocurridoEn()).isEqualTo(cancelado.getOcurridoEn());
        assertThat(payloadRechazado.idEvento()).isEqualTo(rechazado.getIdEvento());
        assertThat(payloadRechazado.entregableProyectoGrado()).isEqualTo(entregable.toString());
        assertThat(payloadRechazado.proyectoGrado()).isEqualTo(proyectoGrado.toString());
        assertThat(payloadRechazado.version()).isEqualTo(2);
        assertThat(payloadRechazado.ocurridoEn()).isEqualTo(rechazado.getOcurridoEn());
    }
}
