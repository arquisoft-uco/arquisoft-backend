package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEstadosPosiblesAsesorMapperTest {

    @Test
    void debeCopiarPaginacionOrdenYFiltros_cuandoSeConvierteAlCriteria() {
        // Arrange
        var raiz = NodoFiltro.predicado("nombre", FiltroOperador.ES, "Asignado");
        var orden = List.of(SortOrder.of("nombre", SortDirection.DESC));
        var query = ConsultaCriteriaQuery.crear(2, 25, orden, raiz);

        // Act
        var criteria = ConsultarEstadosPosiblesAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().getFirst().getCampo()).isEqualTo("nombre");
        assertThat(criteria.getOrdenamiento().getFirst().getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
