package com.arquisoft.mapas_ruta.domain.tarea.event;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TareaFinalizadaEventTest {

    @Test
    void debeExponerTemaYTipo_cuandoSeCrea() {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        var evento = new TareaFinalizadaEvent(tarea);

        // Assert
        assertThat(evento.getTarea()).isEqualTo(tarea);
        assertThat(evento.getTemaEvento()).isEqualTo("mapas_ruta.tarea.finalizada");
        assertThat(evento.getTipoEvento()).isEqualTo("TareaFinalizadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
