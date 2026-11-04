package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesEstudianteQuery;
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

class ConsultarEvaluacionesEstudianteMapperTest {

    @Test
    void debeForzarFiltroDelEstudiante_yAplicarOrdenPorProyectoAscendente_cuandoNoHayRaizNiOrden() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesEstudianteQuery.crear(
                estudiante, ConsultaCriteriaQuery.crear(0, 10, List.of(), null));

        // Act
        var criteria = ConsultarEvaluacionesEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(NodoFiltro.predicado(
                EvaluacionEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES, estudiante.toString()));
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(EvaluacionEstudianteCriteria.Campo.PROYECTO.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeMantenerElForzadoFueraDelOrDelCliente_cuandoLaRaizEsUnGrupoOr() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.grupo(FiltroConector.OR, List.of(
                NodoFiltro.predicado(EvaluacionEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES,
                        ajeno.toString()),
                NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot")));
        var query = ConsultarEvaluacionesEstudianteQuery.crear(
                estudiante, ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente));
        var forzado = NodoFiltro.predicado(
                EvaluacionEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES, estudiante.toString());

        // Act
        var criteria = ConsultarEvaluacionesEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getRaiz())
                .isEqualTo(NodoFiltro.grupo(FiltroConector.AND, List.of(forzado, raizCliente)));
    }

    @Test
    void debeCombinarElFiltroForzadoConLaRaizDelClienteConAnd_yConservarOrdenYPaginacion() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var raizCliente = NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot");
        var orden = List.of(SortOrder.of("estado", SortDirection.DESC));
        var query = ConsultarEvaluacionesEstudianteQuery.crear(
                estudiante, ConsultaCriteriaQuery.crear(2, 5, orden, raizCliente));
        var forzado = NodoFiltro.predicado(
                EvaluacionEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave(), FiltroOperador.ES, estudiante.toString());

        // Act
        var criteria = ConsultarEvaluacionesEstudianteMapper.toCriteria(query);

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
