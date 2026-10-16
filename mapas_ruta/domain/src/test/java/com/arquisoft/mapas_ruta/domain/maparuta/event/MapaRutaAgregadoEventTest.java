package com.arquisoft.mapas_ruta.domain.maparuta.event;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaAgregadoEventTest {

    @Test
    void debeExponerLosDatosDelMapa_cuandoSeConstruyeElEvento() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var inicio = LocalDate.of(2026, 10, 1);
        var fin = LocalDate.of(2026, 12, 1);

        // Act
        var evento = new MapaRutaAgregadoEvent(mapaRuta, proyectoGrado, inicio, fin);

        // Assert
        assertThat(evento.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(evento.getFechaInicio()).isEqualTo(inicio);
        assertThat(evento.getFechaFin()).isEqualTo(fin);
    }

    @Test
    void debeUsarTopicTipoIdYMomento_cuandoSeConstruyeElEvento() {
        // Act
        var evento = new MapaRutaAgregadoEvent(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));

        // Assert
        assertThat(evento.getTemaEvento()).isEqualTo("mapas_ruta.mapa_ruta.agregado");
        assertThat(evento.getTipoEvento()).isEqualTo("MapaRutaAgregadoEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
