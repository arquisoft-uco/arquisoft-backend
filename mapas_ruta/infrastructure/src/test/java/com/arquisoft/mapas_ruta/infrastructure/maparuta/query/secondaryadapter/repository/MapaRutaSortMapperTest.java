package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapaRutaSortMapperTest {

    @Test
    void debeTraducirCadaClaveOrdenable_cuandoSeConsultaLaRuta() {
        // Act & Assert
        assertThat(MapaRutaSortMapper.traducir("fechaInicio")).isEqualTo("fechaInicio");
        assertThat(MapaRutaSortMapper.traducir("fechaFin")).isEqualTo("fechaFin");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(MapaRutaSortMapper.traducir("coordinador")).isNull();
        assertThat(MapaRutaSortMapper.traducir("tituloProyecto")).isNull();
    }
}
