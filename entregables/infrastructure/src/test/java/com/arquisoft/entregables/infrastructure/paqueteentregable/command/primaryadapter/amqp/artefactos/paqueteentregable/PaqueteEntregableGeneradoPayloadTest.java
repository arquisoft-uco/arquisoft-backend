package com.arquisoft.entregables.infrastructure.paqueteentregable.command.primaryadapter.amqp.artefactos.paqueteentregable;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaqueteEntregableGeneradoPayloadTest {

    private final tools.jackson.databind.json.JsonMapper mapper =
            new RabbitMQConfig().rabbitObjectMapper();

    private static final class PaqueteEntregableGeneradoEventoDePrueba extends DomainEvent {

        private final UUID paqueteEntregable;
        private final UUID entregableProyectoGrado;
        private final String bucket;
        private final String claveObjeto;
        private final String nombreArchivo;
        private final long tamanoBytes;
        private final Instant generadoEn;

        private PaqueteEntregableGeneradoEventoDePrueba(UUID paqueteEntregable, UUID entregableProyectoGrado,
                                                        String bucket, String claveObjeto, String nombreArchivo,
                                                        long tamanoBytes, Instant generadoEn) {
            super(EventTopics.Artefactos.PAQUETE_ENTREGABLE_GENERADO, "PaqueteEntregableGeneradoEvent");
            this.paqueteEntregable = paqueteEntregable;
            this.entregableProyectoGrado = entregableProyectoGrado;
            this.bucket = bucket;
            this.claveObjeto = claveObjeto;
            this.nombreArchivo = nombreArchivo;
            this.tamanoBytes = tamanoBytes;
            this.generadoEn = generadoEn;
        }

        public UUID getPaqueteEntregable() {
            return paqueteEntregable;
        }

        public UUID getEntregableProyectoGrado() {
            return entregableProyectoGrado;
        }

        public String getBucket() {
            return bucket;
        }

        public String getClaveObjeto() {
            return claveObjeto;
        }

        public String getNombreArchivo() {
            return nombreArchivo;
        }

        public long getTamanoBytes() {
            return tamanoBytes;
        }

        public Instant getGeneradoEn() {
            return generadoEn;
        }
    }

    @Test
    void debeDeserializarEventoReal_conTodosLosCampos() {
        // Arrange
        var evento = new PaqueteEntregableGeneradoEventoDePrueba(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "bucket", "paquetes/x.zip", "p.zip", 1234L, Instant.now());

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), PaqueteEntregableGeneradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.paqueteEntregable()).isEqualTo(evento.getPaqueteEntregable().toString());
        assertThat(payload.entregableProyectoGrado()).isEqualTo(evento.getEntregableProyectoGrado().toString());
        assertThat(payload.bucket()).isEqualTo("bucket");
        assertThat(payload.claveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(payload.nombreArchivo()).isEqualTo("p.zip");
        assertThat(payload.tamanoBytes()).isEqualTo(1234L);
        assertThat(payload.generadoEn()).isEqualTo(evento.getGeneradoEn());
    }

    @Test
    void debeDeserializarConNulo_cuandoFaltaUnCampo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","paqueteEntregable":"11111111-1111-1111-1111-111111111111"}
                """;

        // Act
        var payload = mapper.readValue(json, PaqueteEntregableGeneradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.tamanoBytes()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
        assertThat(payload.nombreArchivo()).isNull();
    }
}
