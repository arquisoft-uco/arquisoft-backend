package com.arquisoft.fichas.application.observacionitem.query.primaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarObservacionesItemAsesorMapperTest {

    @Test
    void debeForzarFiltroUnicoPorAsesorId_cuandoQueryNoTraeFiltrosDelCliente() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarObservacionesItemAsesorQuery.crear(asesorFicha, criterio);

        // Act
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(ObservacionItemCriteria.Campo.ASESOR_ID.getClave(),
                        FiltroOperador.ES, asesorFicha.toString()));
    }

    @Test
    void debeCombinarConAndElFiltroForzadoConElRaizDelCliente_cuandoQueryTraeFiltrosPropios() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("estadoObservacionRevision", FiltroOperador.ES, "PENDIENTE");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarObservacionesItemAsesorQuery.crear(asesorFicha, criterio);

        var forzado = NodoFiltro.predicado(ObservacionItemCriteria.Campo.ASESOR_ID.getClave(),
                FiltroOperador.ES, asesorFicha.toString());

        // Act
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var asesorFicha = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("estadoObservacionRevision", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarObservacionesItemAsesorQuery.crear(asesorFicha, criterio);

        // Act
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("estadoObservacionRevision");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
