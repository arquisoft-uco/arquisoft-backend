package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
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

class ConsultarEvaluacionesFichaPerfilAsesorMapperTest {

    @Test
    void debeForzarSoloElPredicadoDeAsesorFicha_cuandoElQueryNoTieneFiltros() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarEvaluacionesFichaPerfilAsesorQuery.crear(asesorFicha, criterio);

        // Act
        var criteria = ConsultarEvaluacionesFichaPerfilAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.predicado(EvaluacionFichaPerfilAsesorCriteria.Campo.ASESOR_FICHA.getClave(),
                        FiltroOperador.ES, asesorFicha.toString()));
    }

    @Test
    void debeCombinarEnAndElPredicadoForzadoConLaRaizDelCliente_cuandoElQueryTraeFiltros() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "web");
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente);
        var query = ConsultarEvaluacionesFichaPerfilAsesorQuery.crear(asesorFicha, criterio);

        var forzado = NodoFiltro.predicado(EvaluacionFichaPerfilAsesorCriteria.Campo.ASESOR_FICHA.getClave(),
                FiltroOperador.ES, asesorFicha.toString());

        // Act
        var criteria = ConsultarEvaluacionesFichaPerfilAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(
                NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debePropagarPaginaTamanioYOrdenamiento_sinModificarlos() {
        // Arrange
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var ordenamiento = List.of(SortOrder.of("fechaCreacion", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(2, 25, ordenamiento, null);
        var query = ConsultarEvaluacionesFichaPerfilAsesorQuery.crear(asesorFicha, criterio);

        // Act
        var criteria = ConsultarEvaluacionesFichaPerfilAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(25);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("fechaCreacion");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
