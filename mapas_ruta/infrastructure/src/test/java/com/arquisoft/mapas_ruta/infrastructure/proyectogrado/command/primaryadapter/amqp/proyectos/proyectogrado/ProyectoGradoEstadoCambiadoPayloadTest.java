package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.shared.amqp.RabbitMQConfig;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoEstadoCambiadoPayloadTest {

    private final JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeDeserializarEventoReal_cuandoSeLeeElEventoPublicado() {
        // Arrange
        var json = """
                {
                    "idEvento": "evt-2",
                    "temaEvento": "proyectos.proyecto_grado.estado_cambiado",
                    "ocurridoEn": "2026-11-19T05:05:00Z",
                    "proyectoGradoId": "11111111-1111-1111-1111-111111111111",
                    "tituloProyecto": "Titulo",
                    "estadoAnterior": "EN_PROCESO",
                    "estadoNuevo": "ATRASADO",
                    "motivo": "",
                    "coordinador": {"id": "33333333-3333-3333-3333-333333333333", "nombre": "Ana", "email": "a@uco.edu.co"},
                    "estudiantes": [],
                    "asesores": []
                }
                """;

        // Act
        var payload = mapper.readValue(json, ProyectoGradoEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.idEvento()).isEqualTo("evt-2");
        assertThat(payload.ocurridoEn()).isEqualTo(Instant.parse("2026-11-19T05:05:00Z"));
        assertThat(payload.proyectoGradoId()).isEqualTo("11111111-1111-1111-1111-111111111111");
        assertThat(payload.estadoNuevo()).isEqualTo("ATRASADO");
    }

    @Test
    void debeDeserializarConNulos_cuandoFaltaElCampoNuevo() {
        // Act
        var payload = mapper.readValue("{\"idEvento\":\"evt-2\"}", ProyectoGradoEstadoCambiadoPayload.class);

        // Assert
        assertThat(payload.estadoNuevo()).isNull();
        assertThat(payload.ocurridoEn()).isNull();
    }
}
