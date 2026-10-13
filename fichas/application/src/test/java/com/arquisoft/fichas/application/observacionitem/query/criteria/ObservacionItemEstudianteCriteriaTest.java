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

class ObservacionItemEstudianteCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaFiltrar("revisionItem")).isTrue();
        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaOrdenar("revisionItem")).isFalse();

        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaFiltrar("estadoObservacionRevision")).isTrue();
        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaOrdenar("estadoObservacionRevision")).isTrue();

        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaFiltrar("estudianteId")).isTrue();
        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaOrdenar("estudianteId")).isFalse();

        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(ObservacionItemEstudianteCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }

    @Test
    void debeConstruirCriteria_cuandoRaizFiltraPorCampoPermitido() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoObservacionRevision", FiltroOperador.ES, "PENDIENTE");

        // Act
        var criteria = ObservacionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(raiz)
                .build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeConstruirCriteria_cuandoOrdenaPorEstadoObservacionRevision() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("estadoObservacionRevision", SortDirection.DESC));

        // Act
        var criteria = ObservacionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .ordenamiento(ordenamiento)
                .build();

        // Assert
        assertThat(criteria.getOrdenamiento()).isEqualTo(ordenamiento);
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");
        var builder = ObservacionItemEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var porRevisionItem = List.of(SortOrder.of("revisionItem", SortDirection.ASC));
        var porEstudianteId = List.of(SortOrder.of("estudianteId", SortDirection.ASC));
        var builder = ObservacionItemEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(porRevisionItem)).isInstanceOf(FiltroException.class);
        assertThatThrownBy(() -> builder.ordenamiento(porEstudianteId)).isInstanceOf(FiltroException.class);
    }
}
