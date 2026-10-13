package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesJuradoEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesJuradoEstudianteMapperTest {

    @Test
    void debeAplicarOrdenPorJuradoAscendente_cuandoElOrdenamientoVieneVacio() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);
        var query = ConsultarEvaluacionesJuradoEstudianteQuery.crear(evaluacion, criterio);

        // Act
        var criteria = ConsultarEvaluacionesJuradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo())
                .isEqualTo(EvaluacionJuradoCriteria.Campo.JURADO.getClave());
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void debeRespetarElOrdenExplicito_cuandoElOrdenamientoVieneInformado() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var ordenExplicito = List.of(SortOrder.of("jurado", SortDirection.DESC));
        var criterio = ConsultaCriteriaQuery.crear(1, 5, ordenExplicito, null);
        var query = ConsultarEvaluacionesJuradoEstudianteQuery.crear(evaluacion, criterio);

        // Act
        var criteria = ConsultarEvaluacionesJuradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
        assertThat(criteria.getOrdenamiento().get(0).getCampo()).isEqualTo("jurado");
        assertThat(criteria.getOrdenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
        assertThat(criteria.getPagina()).isEqualTo(1);
        assertThat(criteria.getTamanio()).isEqualTo(5);
    }
}
