package com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.primaryport.model.ConsultarEvaluacionesAsignadasAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesAsignadasAsesorMapperTest {

    @Test
    void debeConstruirCriteriaConElAsesorYElOrdenPorDefecto_cuandoNoLlegaOrden() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var query = ConsultarEvaluacionesAsignadasAsesorQuery.crear(
                asesor, ConsultaCriteriaQuery.crear(0, 10, List.of(), null));

        // Act
        var criteria = ConsultarEvaluacionesAsignadasAsesorMapper.toCriteria(query);

        // Assert
        assertThat(criteria.getAsesor()).isEqualTo(asesor);
        assertThat(criteria.getPagina()).isZero();
        assertThat(criteria.getTamanio()).isEqualTo(10);
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo)
                .containsExactly("estado", "proyecto", "version");
    }

    @Test
    void debeRespetarOrdenYFiltroRecibidos() {
        // Arrange
        var filtro = NodoFiltro.predicado("estado", FiltroOperador.ES, "PENDIENTE");
        var query = ConsultarEvaluacionesAsignadasAsesorQuery.crear(
                UtilUUID.generarNuevoUUID(),
                ConsultaCriteriaQuery.crear(1, 5, List.of(SortOrder.of("proyecto", SortDirection.DESC)), filtro));

        // Act
        var criteria = ConsultarEvaluacionesAsignadasAsesorMapper.toCriteria(query);

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
