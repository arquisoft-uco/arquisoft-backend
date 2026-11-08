package com.arquisoft.artefactos.domain.paqueteentregable.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PaqueteEntregableGeneradoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var paquete = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var generadoEn = Instant.now();

        // Act
        var evento = new PaqueteEntregableGeneradoEvent(
                paquete, entregable, "bucket", "paquetes/x.zip", "p.zip", 9L, generadoEn);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Artefactos.PAQUETE_ENTREGABLE_GENERADO)
                .isEqualTo("artefactos.paquete_entregable.generado");
        assertThat(evento.getPaqueteEntregable()).isEqualTo(paquete);
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getBucket()).isEqualTo("bucket");
        assertThat(evento.getClaveObjeto()).isEqualTo("paquetes/x.zip");
        assertThat(evento.getNombreArchivo()).isEqualTo("p.zip");
        assertThat(evento.getTamanoBytes()).isEqualTo(9L);
        assertThat(evento.getGeneradoEn()).isEqualTo(generadoEn);
        assertThat(evento.getIdEvento()).isNotBlank();
    }
}
