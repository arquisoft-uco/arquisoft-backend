package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarVersionesRepositorioArtefactoAsesorMapperTest {

    @Test
    void debeAplicarOrdenPorDefecto_cuandoNoHayOrden() {
        // Arrange
        var raiz = NodoFiltro.predicado("nombreRepositorio", FiltroOperador.CONTIENE, "repo");
        var query = ConsultaCriteriaQuery.crear(2, 25, List.of(), raiz);

        // Act
        var criteria = ConsultarVersionesRepositorioArtefactoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.getOrdenamiento()).hasSize(2);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("nombreDocumento");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
        assertThat(criteria.getOrdenamiento().get(1).getCampo()).isEqualTo("numeroVersion");
        assertThat(criteria.getOrdenamiento().get(1).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeConservarOrden_cuandoElClienteLoPide() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("fechaCreacion", SortDirection.DESC)), null);

        // Act
        var criteria = ConsultarVersionesRepositorioArtefactoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaCreacion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
