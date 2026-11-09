package com.arquisoft.artefactos.infrastructure.paqueteentregable;

import com.arquisoft.artefactos.domain.paqueteentregable.event.PaqueteEntregableGeneradoEvent;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArchivoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoPaqueteGenerado;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaqueteEntregableGeneradoEventSerializacionTest {

    private final tools.jackson.databind.json.JsonMapper mapper = new RabbitMQConfig().rabbitObjectMapper();

    @Test
    void debeConservarArchivoYArtefactos_cuandoElEventoHaceIdaYVuelta() {
        // Arrange
        var artefacto = new ArtefactoPaqueteGenerado(UtilUUID.generarNuevoUUID(), 3, UtilUUID.generarNuevoUUID(),
                "Informe", UtilUUID.generarNuevoUUID(), "Repositorio", "01-Informe/v3-informe.pdf");
        var evento = PaqueteEntregableGeneradoEvent.de(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                new ArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 9L), Instant.parse("2026-11-08T10:00:00Z"),
                List.of(artefacto));

        // Act
        var json = mapper.writeValueAsString(evento);
        var leido = mapper.readValue(json, PaqueteEntregableGeneradoEvent.class);

        // Assert
        assertThat(leido.getPaqueteEntregable()).isEqualTo(evento.getPaqueteEntregable());
        assertThat(leido.getEntregableProyectoGrado()).isEqualTo(evento.getEntregableProyectoGrado());
        assertThat(leido.getBucket()).isEqualTo("bucket");
        assertThat(leido.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(leido.getNombreArchivo()).isEqualTo("p.zip");
        assertThat(leido.getTamanoBytes()).isEqualTo(9L);
        assertThat(leido.getGeneradoEn()).isEqualTo(evento.getGeneradoEn());
        assertThat(leido.getArtefactos()).containsExactly(artefacto);
    }
}
