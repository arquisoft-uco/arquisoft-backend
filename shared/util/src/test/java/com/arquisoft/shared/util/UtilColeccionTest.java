package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UtilColeccionTest {

    @Test
    void debeRetornarTrue_cuandoColeccionEsNula() {
        assertThat(UtilColeccion.esVaciaONula(null)).isTrue();
    }

    @Test
    void debeRetornarTrue_cuandoColeccionEstaVacia() {
        assertThat(UtilColeccion.esVaciaONula(new ArrayList<>())).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoColeccionTieneElementos() {
        assertThat(UtilColeccion.esVaciaONula(List.of("a"))).isFalse();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayDuplicados() {
        assertThat(UtilColeccion.primerDuplicado(List.of("a", "b", "c"))).isEmpty();
    }

    @Test
    void debeRetornarPrimerDuplicado_cuandoHayRepetidos() {
        UUID repetido = UUID.randomUUID();
        List<UUID> valores = List.of(UUID.randomUUID(), repetido, UUID.randomUUID(), repetido);

        assertThat(UtilColeccion.primerDuplicado(valores)).contains(repetido);
    }

    @Test
    void debeRetornarVacio_cuandoColeccionEsNulaOVacia() {
        assertThat(UtilColeccion.primerDuplicado(null)).isEmpty();
        assertThat(UtilColeccion.primerDuplicado(List.of())).isEmpty();
    }
}
