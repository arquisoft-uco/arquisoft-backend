package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoCerradoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new EntregableProyectoGradoCerradoEvent(entregable, proyecto, 3);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_CERRADO)
                .isEqualTo("entregables.entregable_proyecto_grado.cerrado");
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(evento.getVersion()).isEqualTo(3);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
