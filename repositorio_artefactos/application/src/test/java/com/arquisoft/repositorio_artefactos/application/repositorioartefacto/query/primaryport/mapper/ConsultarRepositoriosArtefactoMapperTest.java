package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.primaryport.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarRepositoriosArtefactoMapperTest {

    @Test
    void debeCopiarPaginaTamanioOrdenYRaiz_cuandoConvierteLaQuery() {
        // Arrange
        var raiz = NodoFiltro.predicado("nombreRepositorio", FiltroOperador.CONTIENE, "repo");
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("descripcion", SortDirection.DESC)), raiz);

        // Act
        var criteria = ConsultarRepositoriosArtefactoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("descripcion");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }
}
