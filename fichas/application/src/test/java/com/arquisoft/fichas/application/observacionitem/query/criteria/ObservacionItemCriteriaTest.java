package com.arquisoft.fichas.application.observacionitem.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(ObservacionItemCriteria.Campo.esValidoParaFiltrar("revisionItem")).isTrue();
        assertThat(ObservacionItemCriteria.Campo.esValidoParaOrdenar("revisionItem")).isFalse();

        assertThat(ObservacionItemCriteria.Campo.esValidoParaFiltrar("estadoObservacionRevision")).isTrue();
        assertThat(ObservacionItemCriteria.Campo.esValidoParaOrdenar("estadoObservacionRevision")).isTrue();

        assertThat(ObservacionItemCriteria.Campo.esValidoParaFiltrar("asesorId")).isTrue();
        assertThat(ObservacionItemCriteria.Campo.esValidoParaOrdenar("asesorId")).isFalse();

        assertThat(ObservacionItemCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(ObservacionItemCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }

    @Test
    void debeConstruirCriteria_cuandoFiltraYOrdenaPorCamposPermitidos() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoObservacionRevision", FiltroOperador.ES, "PENDIENTE");
        var ordenamiento = List.of(SortOrder.of("estadoObservacionRevision", SortDirection.DESC));

        // Act
        var criteria = ObservacionItemCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(raiz)
                .ordenamiento(ordenamiento)
                .build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
        assertThat(criteria.tieneFiltros()).isTrue();
        assertThat(criteria.tieneOrden()).isTrue();
    }

    @Test
    void debeLanzarFiltroException_cuandoFiltraPorCampoNoDeclaradoOOrdenaPorCampoNoOrdenable() {
        // Arrange
        var raizInvalida = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");
        var ordenInvalido = List.of(SortOrder.of("revisionItem", SortDirection.ASC));
        var builder = ObservacionItemCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raizInvalida)).isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> builder.ordenamiento(ordenInvalido)).isInstanceOf(FiltroException.class);
    }
}
