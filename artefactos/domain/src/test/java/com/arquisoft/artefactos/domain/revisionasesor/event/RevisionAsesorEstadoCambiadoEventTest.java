package com.arquisoft.artefactos.domain.revisionasesor.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorEstadoCambiadoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new RevisionAsesorEstadoCambiadoEvent(revision, "RESUELTA", "Ana", "ana@uco.edu.co", "Luis");

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.REVISION_ASESOR_ESTADO_CAMBIADO);
        assertThat(evento.getRevisionAsesorId()).isEqualTo(revision);
        assertThat(evento.getEstado()).isEqualTo("RESUELTA");
        assertThat(evento.getAsesorNombre()).isEqualTo("Ana");
        assertThat(evento.getAsesorEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(evento.getEstudianteNombre()).isEqualTo("Luis");
        assertThat(evento.getIdEvento()).isNotBlank();
    }
}
