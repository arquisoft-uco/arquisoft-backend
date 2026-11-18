package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoOmitidoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class OmitidoEventoDePrueba extends DomainEvent {

        private final UUID artefacto = UtilUUID.generarNuevoUUID();
        private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();

        private OmitidoEventoDePrueba() {
            super(EventTopics.Artefactos.ARTEFACTO_OMITIDO, "ArtefactoOmitidoEvent");
        }

        public UUID getArtefacto() {
            return artefacto;
        }

        public UUID getProyectoGrado() {
            return proyectoGrado;
        }

        public String getDocumento() {
            return "Anteproyecto";
        }
    }

    @Test
    void debeDeserializarEventoReal_cuandoSeSerializa() {
        // Arrange
        var evento = new OmitidoEventoDePrueba();

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), ArtefactoOmitidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.artefacto()).isEqualTo(evento.getArtefacto().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.getProyectoGrado().toString());
    }

    @Test
    void debeTolerarCamposFaltantes_cuandoElEventoNoLosTrae() {
        // Act
        var payload = mapper.readValue("""
                {"idEvento":"evt-1"}
                """, ArtefactoOmitidoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.artefacto()).isNull();
        assertThat(payload.proyectoGrado()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
