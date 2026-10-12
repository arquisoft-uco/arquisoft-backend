package com.arquisoft.mapas_ruta.domain.maparuta.event;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaFechaFinCambiadaEventTest {

    @Test
    void debeExponerPayloadTopicYTipo_cuandoSeConstruyeElEvento() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var inicio = LocalDate.of(2026, 10, 1);
        var anterior = LocalDate.of(2026, 12, 1);
        var nueva = LocalDate.of(2026, 12, 20);

        // Act
        var evento = new MapaRutaFechaFinCambiadaEvent(mapaRuta, proyectoGrado, inicio, anterior, nueva);

        // Assert
        assertThat(evento.getMapaRuta()).isEqualTo(mapaRuta);
        assertThat(evento.getProyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(evento.getFechaInicio()).isEqualTo(inicio);
        assertThat(evento.getFechaFinAnterior()).isEqualTo(anterior);
        assertThat(evento.getFechaFin()).isEqualTo(nueva);
        assertThat(evento.getTemaEvento()).isEqualTo("mapas_ruta.mapa_ruta.fecha_fin_cambiada");
        assertThat(evento.getTipoEvento()).isEqualTo("MapaRutaFechaFinCambiadaEvent");
        assertThat(evento.getIdEvento()).isNotBlank();
        assertThat(evento.getOcurridoEn()).isNotNull();
    }
}
