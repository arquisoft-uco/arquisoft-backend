package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TareaSortMapperTest {

    @Test
    void debeTraducirCamposOrdenables_cuandoSeConsultaLaRuta() {
        // Act & Assert
        assertThat(TareaSortMapper.traducir("descripcion")).isEqualTo("descripcion");
        assertThat(TareaSortMapper.traducir("estadoItem")).isEqualTo("estadoItemId");
    }

    @Test
    void debeDevolverNulo_cuandoElCampoNoEsOrdenable() {
        // Act & Assert
        assertThat(TareaSortMapper.traducir("asesor")).isNull();
        assertThat(TareaSortMapper.traducir("mapaRuta")).isNull();
    }
}
