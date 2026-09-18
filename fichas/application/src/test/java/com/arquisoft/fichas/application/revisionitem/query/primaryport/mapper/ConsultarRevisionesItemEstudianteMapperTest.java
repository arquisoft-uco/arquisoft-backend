package com.arquisoft.fichas.application.revisionitem.query.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemEstudianteQuery;
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

class ConsultarRevisionesItemEstudianteMapperTest {

    @Test
    void debeForzarFiltroUnicoPorEstudianteId_cuandoQueryNoTraeFiltrosDelCliente() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRevisionesItemEstudianteQuery.crear(estudiante, criterio);

        // Act
        RevisionItemEstudianteCriteria criteria = ConsultarRevisionesItemEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(RevisionItemEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(),
                        FiltroOperador.ES, estudiante.toString()));
    }

    @Test
    void debeCombinarConAndElFiltroForzadoConElRaizDelCliente_cuandoQueryTraeFiltrosPropios() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var raizCliente = NodoFiltro.predicado("estadoRevision", FiltroOperador.ES, "EN_PROGRESO");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarRevisionesItemEstudianteQuery.crear(estudiante, criterio);

        var forzado = NodoFiltro.predicado(RevisionItemEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(),
                FiltroOperador.ES, estudiante.toString());

        // Act
        RevisionItemEstudianteCriteria criteria = ConsultarRevisionesItemEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var ordenamiento = List.of(SortOrder.of("estadoRevision", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarRevisionesItemEstudianteQuery.crear(estudiante, criterio);

        // Act
        RevisionItemEstudianteCriteria criteria = ConsultarRevisionesItemEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("estadoRevision");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
