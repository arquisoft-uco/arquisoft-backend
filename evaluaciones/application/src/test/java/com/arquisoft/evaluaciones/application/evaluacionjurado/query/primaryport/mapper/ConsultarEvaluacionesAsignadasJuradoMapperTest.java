package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesAsignadasJuradoMapperTest {

    @Test
    void debeConstruirCriteriaConElJuradoYElOrdenPorDefecto_cuandoNoLlegaOrden() {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(
                jurado, ConsultaCriteriaQuery.crear(0, 10, List.of(), null));

        // Act
        var criteria = ConsultarEvaluacionesAsignadasJuradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getJurado()).isEqualTo(jurado);
        assertThat(criteria.getPagina()).isZero();
        assertThat(criteria.getTamanio()).isEqualTo(10);
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo)
                .containsExactly("estadoJurado", "estado", "proyecto", "version");
    }

    @Test
    void debeRespetarOrdenYFiltroRecibidos() {
        // Arrange
        var filtro = NodoFiltro.predicado("estadoJurado", FiltroOperador.ES, "EVALUADA");
        var query = ConsultarEvaluacionesAsignadasJuradoQuery.crear(
                UtilUUID.generarNuevoUUID(),
                ConsultaCriteriaQuery.crear(1, 5, List.of(SortOrder.of("proyecto", SortDirection.DESC)), filtro));

        // Act
        var criteria = ConsultarEvaluacionesAsignadasJuradoMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getOrdenamiento())
                .singleElement()
                .satisfies(orden -> {
                    assertThat(orden.getCampo()).isEqualTo("proyecto");
                    assertThat(orden.getDireccion()).isEqualTo(SortDirection.DESC);
                });
        assertThat(criteria.tieneFiltros()).isTrue();
    }
}
