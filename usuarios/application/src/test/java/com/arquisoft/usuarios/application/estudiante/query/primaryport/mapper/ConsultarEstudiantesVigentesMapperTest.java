package com.arquisoft.usuarios.application.estudiante.query.primaryport.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEstudiantesVigentesMapperTest {

    @Test
    void debePropagarPaginaTamanioOrdenamientoYRaiz_cuandoElCriterioEsValido() {
        // Arrange
        var raiz = NodoFiltro.predicado("email", FiltroOperador.CONTIENE, "uco.edu.co");
        var query = ConsultaCriteriaQuery.crear(
                1, 20, List.of(SortOrder.of("nombre", SortDirection.ASC)), raiz);

        // Act
        var criteria = ConsultarEstudiantesVigentesMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(1);
        assertThat(criteria.getTamanio()).isEqualTo(20);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("nombre");
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraPorVigente() {
        // Arrange — vigente no existe en la whitelist de este endpoint
        var raiz = NodoFiltro.predicado("vigente", FiltroOperador.ES, "false");
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesVigentesMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraPorEstado() {
        // Arrange — estado no existe en la whitelist de este endpoint
        var raiz = NodoFiltro.predicado("estado", FiltroOperador.ES, "ACTIVO");
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raiz);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesVigentesMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorEstado() {
        // Arrange — estado no existe en la whitelist de este endpoint
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("estado", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesVigentesMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorVigente() {
        // Arrange — vigente no existe en la whitelist de este endpoint
        var query = ConsultaCriteriaQuery.crear(
                0, 10, List.of(SortOrder.of("vigente", SortDirection.ASC)), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEstudiantesVigentesMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeConstruirCriteriaSinFiltros_cuandoElCriterioNoTraeRaiz() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarEstudiantesVigentesMapper.toCriteria(query);

        // Assert
        assertThat(criteria.tieneFiltros()).isFalse();
        assertThat(criteria.tieneOrden()).isFalse();
    }
}
