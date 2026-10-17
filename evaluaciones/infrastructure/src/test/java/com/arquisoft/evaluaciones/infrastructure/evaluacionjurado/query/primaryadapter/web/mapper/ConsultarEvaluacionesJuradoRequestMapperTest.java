package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesJuradoRequestMapperTest {

    @Test
    void debeAplicarLosValoresPorDefecto_cuandoNoLlegaBody() {
        // Arrange
        var evaluacionId = UUID.randomUUID();

        // Act
        var query = ConsultarEvaluacionesJuradoRequestMapper.toQuery(null, evaluacionId);

        // Assert
        assertThat(query.evaluacion()).isEqualTo(evaluacionId);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarElCriterioConPaginacionOrdenYFiltro_cuandoLlegaBody() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(
                2, 5, List.of("jurado:desc"), new PredicadoFiltroDTO("jurado", "CONTIENE", "Ana"));
        var evaluacionId = UUID.randomUUID();

        // Act
        var query = ConsultarEvaluacionesJuradoRequestMapper.toQuery(dto, evaluacionId);

        // Assert
        assertThat(query.evaluacion()).isEqualTo(evaluacionId);
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(5);
        assertThat(query.criterio().ordenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("jurado", SortDirection.DESC));
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("jurado", FiltroOperador.CONTIENE, "Ana"));
    }
}
