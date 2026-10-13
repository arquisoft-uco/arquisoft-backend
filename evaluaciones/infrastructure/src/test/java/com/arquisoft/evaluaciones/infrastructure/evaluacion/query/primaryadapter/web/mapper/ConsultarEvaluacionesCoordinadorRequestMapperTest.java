package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarEvaluacionesCoordinadorRequestMapperTest {

    @Test
    void debeAplicarLosValoresPorDefecto_cuandoNoLlegaBody() {
        // Act
        var query = ConsultarEvaluacionesCoordinadorRequestMapper.toQuery(null);

        // Assert
        assertThat(query.pagina()).isZero();
        assertThat(query.tamanio()).isEqualTo(10);
        assertThat(query.ordenamiento()).isEmpty();
        assertThat(query.raiz()).isNull();
    }

    @Test
    void debeArmarLaQueryConPaginacionOrdenYFiltro_cuandoLlegaBody() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(
                2, 5, List.of("estado:desc"), new PredicadoFiltroDTO("proyecto", "CONTIENE", "robot"));

        // Act
        var query = ConsultarEvaluacionesCoordinadorRequestMapper.toQuery(dto);

        // Assert
        assertThat(query.pagina()).isEqualTo(2);
        assertThat(query.tamanio()).isEqualTo(5);
        assertThat(query.ordenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("estado", SortDirection.DESC));
        assertThat(query.raiz())
                .isEqualTo(NodoFiltro.predicado("proyecto", FiltroOperador.CONTIENE, "robot"));
    }
}
