package com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCoordinadorMapperTest {

    @Test
    void debeAplicarOrdenPorProyectoAscendente_cuandoElOrdenamientoVieneVacio() {
        // Arrange
        var query = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var criteria = ConsultarEvaluacionesCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(EvaluacionCriteria.Campo.PROYECTO.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeRespetarElOrdenExplicito_cuandoElOrdenamientoVieneInformado() {
        // Arrange
        var ordenExplicito = List.of(SortOrder.of("estado", SortDirection.DESC));
        var query = ConsultaCriteriaQuery.crear(0, 10, ordenExplicito, null);

        // Act
        var criteria = ConsultarEvaluacionesCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("estado");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
    }

    @Test
    void debeTrasladarPaginaTamanioYFiltro() {
        // Arrange
        var filtro = NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot");
        var query = ConsultaCriteriaQuery.crear(2, 5, List.of(), filtro);

        // Act
        var criteria = ConsultarEvaluacionesCoordinadorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getRaiz()).isEqualTo(filtro);
    }
}
