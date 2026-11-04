package com.arquisoft.artefactos.application.versionartefacto.query.primaryport.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionesArtefactoAsesorQuery;
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

class ConsultarVersionesArtefactoAsesorMapperTest {

    @Test
    void debeForzarSoloElPredicadoDelAsesor_cuandoQueryNoTraeFiltrosDelCliente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio);

        // Act
        var criteria = ConsultarVersionesArtefactoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(VersionArtefactoAsesorCriteria.Campo.ASESOR_ID.getClave(),
                        FiltroOperador.ES, asesor.toString()));
    }

    @Test
    void debeCombinarConAndConElForzadoPrimero_aunqueElClienteFiltrePorOtroAsesorId() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var otroAsesor = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado(VersionArtefactoAsesorCriteria.Campo.ASESOR_ID.getClave(),
                FiltroOperador.ES, otroAsesor.toString());
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio);
        var forzado = NodoFiltro.predicado(VersionArtefactoAsesorCriteria.Campo.ASESOR_ID.getClave(),
                FiltroOperador.ES, asesor.toString());

        // Act
        var criteria = ConsultarVersionesArtefactoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debeConservarPaginaTamanioYOrden_cuandoElClienteLoEnvia() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("version", SortDirection.ASC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio);

        // Act
        var criteria = ConsultarVersionesArtefactoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("version");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeOrdenarPorFechaCreacionDescendente_cuandoElCriterioNoTraeOrden() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarVersionesArtefactoAsesorQuery.crear(asesor, criterio);

        // Act
        var criteria = ConsultarVersionesArtefactoAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(VersionArtefactoAsesorCriteria.Campo.FECHA_CREACION.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
