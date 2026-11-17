package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntregableDisponibleSortMapperTest {

    @Test
    void debeTraducirLasTresClavesDeOrden_cuandoSonOrdenables() {
        // Act & Assert
        assertThat(EntregableDisponibleSortMapper.traducir("titulo")).isEqualTo("tituloProyecto");
        assertThat(EntregableDisponibleSortMapper.traducir("tituloAusente")).isEqualTo("tituloAusente");
        assertThat(EntregableDisponibleSortMapper.traducir("versionEntregable")).isEqualTo("versionEntregable");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoExiste() {
        // Act & Assert
        assertThat(EntregableDisponibleSortMapper.traducir("nombreArchivo")).isNull();
    }
}
