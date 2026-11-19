package com.arquisoft.mapas_ruta.domain.maparuta.event;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaPlazoVencidoEventTest {

    @Test
    void debeTenerTopicYGettersCorrectos_cuandoSeConstruyeElEvento() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var fechaFin = LocalDate.of(2026, 11, 18);

        // Act
        var evento = new MapaRutaPlazoVencidoEvent(mapaRuta, proyectoGrado, fechaFin);

        // Assert
        assertThat(evento.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(evento.getFechaFin()).isEqualTo(fechaFin);
        assertThat(evento.getTemaEvento()).isEqualTo("mapas_ruta.mapa_ruta.plazo_vencido");
        assertThat(evento.getTipoEvento()).isEqualTo("MapaRutaPlazoVencidoEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
    }
}
