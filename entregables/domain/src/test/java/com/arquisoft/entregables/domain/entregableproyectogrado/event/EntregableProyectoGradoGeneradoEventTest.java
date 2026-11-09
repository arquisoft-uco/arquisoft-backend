package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoGeneradoEventTest {

    @Test
    void debeExponerTopicYCampos_cuandoSeConstruye() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new EntregableProyectoGradoGeneradoEvent(entregable, proyecto, 2);

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_GENERADO)
                .isEqualTo("entregables.entregable_proyecto_grado.generado");
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(evento.getVersion()).isEqualTo(2);
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
