package com.arquisoft.artefactos.domain.paqueteentregable.event;

import com.arquisoft.artefactos.domain.paqueteentregable.model.ArchivoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoPaqueteGenerado;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaqueteEntregableGeneradoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var paquete = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        var generadoEn = Instant.now();

        // Act
        var evento = PaqueteEntregableGeneradoEvent.de(
                paquete, entregable, new ArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 9L), generadoEn, null);

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

    @Test
    void debeExponerArtefactosInmutables_cuandoSeConstruyeConLista() {
        // Arrange
        var item = new ArtefactoPaqueteGenerado(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 3,
                UtilUUID.generarNuevoUUID(),
                "Informe", UtilUUID.generarNuevoUUID(), "Repo", "01-Informe/v3-informe.pdf");
        var archivo = new ArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 9L);

        // Act
        var evento = PaqueteEntregableGeneradoEvent.de(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), archivo, Instant.now(), List.of(item));

        // Assert
        assertThat(evento.getArtefactos()).containsExactly(item);
        assertThatThrownBy(() -> evento.getArtefactos().add(item)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void debeDevolverListaVacia_cuandoArtefactosEsNulo() {
        // Arrange
        var archivo = new ArchivoPaquete("bucket", "paquetes/x.zip", "p.zip", 9L);

        // Act
        var evento = PaqueteEntregableGeneradoEvent.de(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), archivo, Instant.now(), null);

        // Assert
        assertThat(evento.getArtefactos()).isEmpty();
    }
}
