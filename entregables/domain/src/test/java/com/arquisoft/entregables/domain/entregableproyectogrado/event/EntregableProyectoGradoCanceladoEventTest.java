package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoCanceladoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new EntregableProyectoGradoCanceladoEvent(entregable, proyecto, 3);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CANCELADO)
                .isEqualTo("entregables.entregable_proyecto_grado.cancelado");
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(evento.getVersion()).isEqualTo(3);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
