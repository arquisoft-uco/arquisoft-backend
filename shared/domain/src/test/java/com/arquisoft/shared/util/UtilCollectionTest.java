package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UtilCollectionTest {

    @Test
    void debeRetornarTrue_cuandoColeccionEsNula() {
        assertThat(UtilCollection.isEmptyOrNull(null)).isTrue();
    }

    @Test
    void debeRetornarTrue_cuandoColeccionEstaVacia() {
        assertThat(UtilCollection.isEmptyOrNull(new ArrayList<>())).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoColeccionTieneElementos() {
        assertThat(UtilCollection.isEmptyOrNull(List.of("a"))).isFalse();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayDuplicados() {
        assertThat(UtilCollection.firstDuplicate(List.of("a", "b", "c"))).isEmpty();
    }

    @Test
    void debeRetornarPrimerDuplicado_cuandoHayRepetidos() {
        UUID repetido = UUID.randomUUID();
        List<UUID> valores = List.of(UUID.randomUUID(), repetido, UUID.randomUUID(), repetido);

        assertThat(UtilCollection.firstDuplicate(valores)).contains(repetido);
    }

    @Test
    void debeRetornarVacio_cuandoColeccionEsNulaOVacia() {
        assertThat(UtilCollection.firstDuplicate(null)).isEmpty();
        assertThat(UtilCollection.firstDuplicate(List.of())).isEmpty();
    }
}
