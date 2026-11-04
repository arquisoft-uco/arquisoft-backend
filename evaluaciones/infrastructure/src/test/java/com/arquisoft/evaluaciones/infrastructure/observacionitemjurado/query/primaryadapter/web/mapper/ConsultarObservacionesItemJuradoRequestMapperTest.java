package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.mapper;

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

class ConsultarObservacionesItemJuradoRequestMapperTest {

    @Test
    void debeAplicarLosValoresPorDefecto_cuandoNoLlegaBody() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();

        // Act
        var query = ConsultarObservacionesItemJuradoRequestMapper.toQuery(null, evaluacionCuantitativaJurado);

        // Assert
        assertThat(query.evaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }

    @Test
    void debeArmarElCriterioConPaginacionOrdenYFiltro_cuandoLlegaBody() {
        // Arrange
        var dto = new QueryCriteriaRequestDTO(
                2, 5, List.of("descripcion:desc"), new PredicadoFiltroDTO("descripcion", "CONTIENE", "rigor"));

        // Act
        var query = ConsultarObservacionesItemJuradoRequestMapper.toQuery(dto, UUID.randomUUID());

        // Assert
        assertThat(query.criterio().pagina()).isEqualTo(2);
        assertThat(query.criterio().tamanio()).isEqualTo(5);
        assertThat(query.criterio().ordenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcion", SortDirection.DESC));
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "rigor"));
    }
}
