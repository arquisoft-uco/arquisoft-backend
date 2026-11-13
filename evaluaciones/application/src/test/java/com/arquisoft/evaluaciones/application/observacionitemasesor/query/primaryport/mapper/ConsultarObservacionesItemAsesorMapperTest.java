package com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ConsultarObservacionesItemAsesorMapperTest {

    private static ConsultarObservacionesItemAsesorQuery query(
            UUID evaluacion, UUID asesor, int pagina, int tamanio, List<SortOrder> orden, NodoFiltro raiz) {
        return ConsultarObservacionesItemAsesorQuery.crear(
                evaluacion, asesor, ConsultaCriteriaQuery.crear(pagina, tamanio, orden, raiz));
    }

    @Test
    void debeCopiarEvaluacionAsesorPaginacionYFiltro_alCriteria() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var filtro = NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "rigor");

        // Act
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(
                query(evaluacion, asesor, 2, 5, List.of(), filtro));

        // Assert
        assertThat(criteria.getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(criteria.getAsesor()).isEqualTo(asesor);
        assertThat(criteria.getPagina()).isEqualTo(2);
        assertThat(criteria.getTamanio()).isEqualTo(5);
        assertThat(criteria.getRaiz()).isSameAs(filtro);
    }

    @Test
    void debeAplicarOrdenPorDescripcionAscendente_cuandoElClienteNoPideOrden() {
        // Act
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(
                query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10, List.of(), null));

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple(
                        ObservacionItemAsesorCriteria.Campo.DESCRIPCION.getClave(), SortDirection.ASC));
        assertThat(criteria.tieneFiltros()).isFalse();
    }

    @Test
    void debeConservarElOrdenDelCliente_cuandoLoPide() {
        // Arrange
        var orden = List.of(SortOrder.of("descripcion", SortDirection.DESC));

        // Act
        var criteria = ConsultarObservacionesItemAsesorMapper.toCriteria(
                query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10, orden, null));

        // Assert
        assertThat(criteria.getOrdenamiento())
                .extracting(SortOrder::getCampo, SortOrder::getDireccion)
                .containsExactly(tuple("descripcion", SortDirection.DESC));
    }

    @Test
    void debeLanzarFiltroException_cuandoElOrdenOElFiltroSonSobreUnCampoNoPermitido() {
        // Arrange
        var ordenInvalido = query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10,
                List.of(SortOrder.of("id", SortDirection.ASC)), null);
        var filtroInvalido = query(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), 0, 10, List.of(),
                NodoFiltro.predicado("asesor", FiltroOperador.ES, UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> ConsultarObservacionesItemAsesorMapper.toCriteria(ordenInvalido))
                .isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> ConsultarObservacionesItemAsesorMapper.toCriteria(filtroInvalido))
                .isInstanceOf(FiltroException.class);
    }
}
