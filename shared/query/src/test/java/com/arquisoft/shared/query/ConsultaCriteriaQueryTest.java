package com.arquisoft.shared.query;

import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultaCriteriaQueryTest {

    @Test
    void debeNormalizarOrdenamientoNulo_aListaVacia() {
        var query = ConsultaCriteriaQuery.crear(1, 20, null, null);

        assertThat(query.ordenamiento()).isEmpty();
    }

    @Test
    void debeConservarPaginaTamanioOrdenamientoYRaiz() {
        var orden = List.of(SortOrder.of("tituloProyecto", SortDirection.ASC));
        var raiz = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web");

        var query = ConsultaCriteriaQuery.crear(3, 50, orden, raiz);

        assertThat(query.pagina()).isEqualTo(3);
        assertThat(query.tamanio()).isEqualTo(50);
        assertThat(query.ordenamiento()).containsExactlyElementsOf(orden);
        assertThat(query.raiz()).isEqualTo(raiz);
    }

    @Test
    void debeExponerUnOrdenamientoInmutable() {
        var query = ConsultaCriteriaQuery.crear(0, 10, new ArrayList<>(List.of(
                SortOrder.of("asesorNombre", SortDirection.DESC))), null);

        assertThatThrownBy(() -> query.ordenamiento().add(SortOrder.of("x", SortDirection.ASC)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
