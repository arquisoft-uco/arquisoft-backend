package com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model.ConsultarObservacionesItemJuradoQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarObservacionesItemJuradoMapperTest {

    private static ConsultarObservacionesItemJuradoQuery query(
            UUID evaluacionCuantitativaJurado, int pagina, int tamanio, List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarObservacionesItemJuradoQuery.crear(
                evaluacionCuantitativaJurado, ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeCopiarPaginacionFiltroYEvaluacionCuantitativa_alCriteria() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var filtro = NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "rigor");

        // Act
        var criteria = ConsultarObservacionesItemJuradoMapper.toCriteria(
                query(evaluacionCuantitativaJurado, 2, 5, List.of(), filtro));

        // Assert
        assertThat(criteria.getEvaluacionCuantitativaJurado()).isEqualTo(evaluacionCuantitativaJurado);
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getRaiz()).isSameAs(filtro);
    }

    @Test
    void debeAplicarOrdenPorDescripcionAscendente_cuandoElClienteNoPideOrden() {
        // Act
        var criteria = ConsultarObservacionesItemJuradoMapper.toCriteria(
                query(UUID.randomUUID(), 0, 10, List.of(), null));

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple(
                        ObservacionItemJuradoCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC));
        assertThat(criteria.tieneFiltros()).isFalse();
    }

    @Test
    void debeConservarElOrdenDelCliente_cuandoLoPide() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcion", SortDirection.DESC));

        // Act
        var criteria = ConsultarObservacionesItemJuradoMapper.toCriteria(
                query(UUID.randomUUID(), 0, 10, orden, null));

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcion", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenEsSobreUnCampoNoPermitido() {
        // Arrange
        var orden = List.of(SortOrder.of("id", SortDirection.ASC));
        var query = query(UUID.randomUUID(), 0, 10, orden, null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemJuradoMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElFiltroEsSobreUnCampoNoPermitido() {
        // Arrange
        var filtro = NodoFiltro.predicado("evaluacionCuantitativaJurado", FiltroOperador.ES, UUID.randomUUID().toString());
        var query = query(UUID.randomUUID(), 0, 10, List.of(), filtro);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemJuradoMapper.toCriteria(query))
                .isInstanceOf(FiltroException.class);
    }
}
