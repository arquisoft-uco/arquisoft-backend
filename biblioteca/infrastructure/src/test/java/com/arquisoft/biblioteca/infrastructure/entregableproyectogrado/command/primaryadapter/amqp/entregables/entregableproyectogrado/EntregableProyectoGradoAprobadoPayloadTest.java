package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoAprobadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    private static final class AprobadoEventoDePrueba extends DomainEvent {

        private final UUID entregableProyectoGrado = UtilUUID.generarNuevoUUID();
        private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();
        private final int version = 2;
        private final String estadoEntregable = "APROBADO";
        private final String estadoEntregableNombre = "Aprobado";
        private final String bucket = "entregables";
        private final String claveObjeto = "paquetes/a.zip";
        private final String nombreArchivo = "a.zip";

        private AprobadoEventoDePrueba() {
            super(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_APROBADO, "EntregableProyectoGradoAprobadoEvent");
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

        public String getEstadoEntregable() {
            return estadoEntregable;
        }

        public String getEstadoEntregableNombre() {
            return estadoEntregableNombre;
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
    }

    @Test
    void debeConservarTodosLosCamposDelContrato_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var evento = new AprobadoEventoDePrueba();

        // Act
        var payload = mapper.readValue(mapper.writeValueAsString(evento), EntregableProyectoGradoAprobadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo(evento.getIdEvento());
        assertThat(payload.ocurridoEn()).isEqualTo(evento.getOcurridoEn());
        assertThat(payload.entregableProyectoGrado()).isEqualTo(evento.getEntregableProyectoGrado().toString());
        assertThat(payload.proyectoGrado()).isEqualTo(evento.getProyectoGrado().toString());
        assertThat(payload.version()).isEqualTo(2);
        assertThat(payload.estadoEntregable()).isEqualTo("APROBADO");
        assertThat(payload.claveObjeto()).isEqualTo("paquetes/a.zip");
        assertThat(payload.nombreArchivo()).isEqualTo("a.zip");
    }

    @Test
    void debeDeserializarConNull_cuandoFaltanLosCamposDeUbicacion() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","entregableProyectoGrado":"44444444-4444-4444-4444-444444444444",
                 "campoDesconocido":"x"}
                """;

        // Act
        var payload = mapper.readValue(json, EntregableProyectoGradoAprobadoPayload.class);

        // Assert
        assertThat(payload.estadoEntregable()).isNull();
        assertThat(payload.claveObjeto()).isNull();
        assertThat(payload.nombreArchivo()).isNull();
        assertThat(payload.version()).isNull();
    }
}
