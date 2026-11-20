package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableProyectoGradoAprobadoEventTest {

    @Test
    void debeExponerTopicYCamposDelContrato_cuandoSeConstruye() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new EntregableProyectoGradoAprobadoEvent(entregable, proyecto, 2,
                EstadoEntregable.APROBADO_CON_OBSERVACIONES, "entregables", "paquetes/zip", "paquete.zip");

        // Assert
        assertThat(evento.getTemaEvento())
                .isEqualTo(EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_APROBADO)
                .isEqualTo("entregables.entregable_proyecto_grado.aprobado");
        assertThat(evento.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(evento.getVersion()).isEqualTo(2);
        assertThat(evento.getEstadoEntregable()).isEqualTo(EstadoEntregable.APROBADO_CON_OBSERVACIONES.getId());
        assertThat(evento.getEstadoEntregableNombre()).isEqualTo("Aprobado Con Observaciones");
        assertThat(evento.getBucket()).isEqualTo("entregables");
        assertThat(evento.getClaveObjeto()).isEqualTo("paquetes/zip");
        assertThat(evento.getNombreArchivo()).isEqualTo("paquete.zip");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
