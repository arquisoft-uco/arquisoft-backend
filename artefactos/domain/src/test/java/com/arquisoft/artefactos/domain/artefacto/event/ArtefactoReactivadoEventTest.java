package com.arquisoft.artefactos.domain.artefacto.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoReactivadoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new ArtefactoReactivadoEvent(artefacto, proyectoGrado);

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo(EventTopics.Artefactos.ARTEFACTO_REACTIVADO);
        assertThat(evento.getTemaEvento()).isEqualTo("artefactos.artefacto.reactivado");
        assertThat(evento.getArtefacto()).isEqualTo(artefacto);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
