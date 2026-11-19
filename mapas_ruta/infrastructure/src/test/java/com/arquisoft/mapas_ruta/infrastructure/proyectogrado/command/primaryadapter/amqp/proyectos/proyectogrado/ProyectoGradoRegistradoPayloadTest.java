package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoRegistradoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeDeserializarEventoReal_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var json = """
                {
                    "idEvento": "evt-1",
                    "temaEvento": "proyectos.proyecto_grado.registrado",
                    "tipoEvento": "ProyectoGradoRegistradoEvent",
                    "ocurridoEn": "2026-11-01T10:00:00Z",
                    "proyectoGradoId": "11111111-1111-1111-1111-111111111111",
                    "fichaPerfilId": "22222222-2222-2222-2222-222222222222",
                    "tituloProyecto": "Titulo",
                    "coordinador": {
                        "id": "33333333-3333-3333-3333-333333333333",
                        "nombre": "Ana",
                        "email": "ana@uco.edu.co"
                    }
                }
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-1");
        assertThat(payload.ocurridoEn()).isEqualTo(Instant.parse("2026-11-01T10:00:00Z"));
        assertThat(payload.proyectoGradoId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(payload.fichaPerfilId()).isEqualTo("22222222-2222-2222-2222-222222222222");
        assertThat(payload.tituloProyecto()).isEqualTo("Titulo");
        assertThat(payload.coordinadorId()).isEqualTo("33333333-3333-3333-3333-333333333333");
    }

    @Test
    void debeDeserializarConNulos_cuandoFaltaElCampoNuevo() {
        // Arrange
        var json = """
                {"idEvento":"evt-1","proyectoGradoId":"11111111-1111-1111-1111-111111111111",
                 "coordinador":{"nombre":"Ana"}}
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.coordinadorId()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }

    @Test
    void debeDevolverCoordinadorNulo_cuandoNoViajaElCoordinador() {
        // Act
        var payload = mapper.readValue("{\"idEvento\":\"evt-1\"}", ProyectoGradoRegistradoPayload.class);

        // Assert
        assertThat(payload.coordinadorId()).isNull();
    }
}
