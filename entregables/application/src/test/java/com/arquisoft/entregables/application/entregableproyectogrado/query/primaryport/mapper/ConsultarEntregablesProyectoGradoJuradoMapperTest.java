package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEntregablesProyectoGradoJuradoMapperTest {

    private static final List<String> ESTADOS_A_EVALUAR = List.of(
            EstadoEntregable.DISPONIBLE_EVALUACION.getId(), EstadoEntregable.EN_EVALUACION.getId());

    @Test
    void debeForzarEstadosAEvaluar_cuandoNoHayRaiz() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarEntregablesProyectoGradoJuradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicadoMultivalor("estadoEntregable", FiltroOperador.IN, ESTADOS_A_EVALUAR));
        assertThat(criteria.tieneFiltros()).isTrue();
    }

    @Test
    void debeCombinarConAnd_cuandoHayRaizDelCliente() {
        // Arrange
        var raizCliente = NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado("asesorNombre", FiltroOperador.CONTIENE, "Ana"),
                NodoFiltro.predicado("estadoEntregable", FiltroOperador.ES, "APROBADO")));
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);

        // Act
        var criteria = ConsultarEntregablesProyectoGradoJuradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isInstanceOfSatisfying(NodoFiltro.Grupo.class, grupo -> {
            assertThat(grupo.conector()).isEqualTo(FiltroConector.AND);
            assertThat(grupo.nodos()).containsExactly(
                    NodoFiltro.predicadoMultivalor("estadoEntregable", FiltroOperador.IN, ESTADOS_A_EVALUAR),
                    raizCliente);
        });
    }

    @Test
    void debeCopiarPaginaTamanioYOrden() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(
                2, 25, List.of(SortOrder.of("proyectoGradoNombre", SortDirection.DESC)), null);

        // Act
        var criteria = ConsultarEntregablesProyectoGradoJuradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("proyectoGradoNombre");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
