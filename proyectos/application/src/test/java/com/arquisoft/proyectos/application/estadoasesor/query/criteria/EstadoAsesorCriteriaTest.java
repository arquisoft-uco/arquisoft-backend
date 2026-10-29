package com.arquisoft.proyectos.application.estadoasesor.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoAsesorCriteriaTest {

    @ParameterizedTest
    @ValueSource(strings = {"id", "nombre"})
    void debeAceptarFiltroPorIdYNombre_cuandoCampoFiltrable(String campo) {
        // Arrange
        var raiz = NodoFiltro.predicado(campo, FiltroOperador.ES, "valor");

        // Act
        var criteria = EstadoAsesorCriteria.builder().raiz(raiz).build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeRechazarFiltroPorDescripcion_cuandoCampoNoFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("descripcion", FiltroOperador.ES, "valor");

        // Act & Assert
        assertThatThrownBy(() -> EstadoAsesorCriteria.builder().raiz(raiz))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeAceptarOrdenPorNombre_cuandoCampoOrdenable() {
        // Arrange
        var orden = List.of(SortOrder.of("nombre", SortDirection.DESC));

        // Act
        var criteria = EstadoAsesorCriteria.builder().ordenamiento(orden).build();

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.tieneOrden()).isTrue();
    }

    @Test
    void debeRechazarOrdenPorCampoInexistente_cuandoClaveDesconocida() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcion", SortDirection.ASC));

        // Act & Assert
        assertThatThrownBy(() -> EstadoAsesorCriteria.builder().ordenamiento(orden))
                .isInstanceOf(FiltroException.class);
    }
}
