package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesAsesorQuery;
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

class ConsultarEvaluacionesAsesorMapperTest {

    @Test
    void debeForzarFiltroDelAsesor_yAplicarOrdenPorProyectoAscendente_cuandoNoHayRaizNiOrden() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsesorQuery.crear(
                asesor, ConsultaCriteriaQuery.crear(0, 10, List.of(), null));

        // Act
        var criteria = ConsultarEvaluacionesAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.predicado(
                EvaluacionAsesorCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES, asesor.toString()));
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(EvaluacionAsesorCriteria.Campo.PROYECTO.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeCombinarElFiltroForzadoConLaRaizDelClienteConAnd_yConservarOrdenYPaginacion() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot");
        var orden = List.of(SortOrder.of("estado", SortDirection.DESC));
        var query = ConsultarEvaluacionesAsesorQuery.crear(
                asesor, ConsultaCriteriaQuery.crear(2, 5, orden, raizCliente));
        var forzado = NodoFiltro.predicado(
                EvaluacionAsesorCriteria.Campo.ASESOR_ID.getClave(), FiltroOperador.ES, asesor.toString());

        // Act
        var criteria = ConsultarEvaluacionesAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz())
                .isEqualTo(NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("estado");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }
}
