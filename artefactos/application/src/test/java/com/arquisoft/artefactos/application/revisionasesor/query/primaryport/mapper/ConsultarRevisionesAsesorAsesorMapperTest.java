package com.arquisoft.artefactos.application.revisionasesor.query.primaryport.mapper;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.artefactos.application.revisionasesor.query.primaryport.model.ConsultarRevisionesAsesorAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarRevisionesAsesorAsesorMapperTest {

    @Test
    void debeForzarSoloElPredicadoDelAsesor_cuandoQueryNoTraeFiltrosDelCliente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRevisionesAsesorAsesorQuery.crear(asesor, criterio);

        // Act
        var criteria = ConsultarRevisionesAsesorAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(RevisionAsesorAsesorCriteria.Campo.ASESOR_ID.getClave(),
                        FiltroOperador.ES, asesor.toString()));
    }

    @Test
    void debeCombinarConAndConElForzadoPrimero_aunqueElClienteFiltrePorOtroAsesorId() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado(RevisionAsesorAsesorCriteria.Campo.ASESOR_ID.getClave(),
                FiltroOperador.ES, otroAsesor.toString());
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarRevisionesAsesorAsesorQuery.crear(asesor, criterio);
        var forzado = NodoFiltro.predicado(RevisionAsesorAsesorCriteria.Campo.ASESOR_ID.getClave(),
                FiltroOperador.ES, asesor.toString());

        // Act
        var criteria = ConsultarRevisionesAsesorAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("estadoRevisionAsesor", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarRevisionesAsesorAsesorQuery.crear(asesor, criterio);

        // Act
        var criteria = ConsultarRevisionesAsesorAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("estadoRevisionAsesor");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeAplicarOrdenPorEstadoAscendente_cuandoElCriterioNoTraeOrden() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarRevisionesAsesorAsesorQuery.crear(asesor, criterio);

        // Act
        var criteria = ConsultarRevisionesAsesorAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(RevisionAsesorAsesorCriteria.Campo.ESTADO_REVISION_ASESOR.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }
}
