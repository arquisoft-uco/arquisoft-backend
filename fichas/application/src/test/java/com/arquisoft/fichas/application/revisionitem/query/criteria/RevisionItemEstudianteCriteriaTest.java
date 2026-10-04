package com.arquisoft.fichas.application.revisionitem.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionItemEstudianteCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaFiltrar("item")).isTrue();
        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaOrdenar("item")).isFalse();

        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaFiltrar("estadoRevision")).isTrue();
        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaOrdenar("estadoRevision")).isTrue();

        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaFiltrar("estudianteId")).isTrue();
        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaOrdenar("estudianteId")).isFalse();

        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(RevisionItemEstudianteCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }

    @Test
    void debeConstruirCriteria_cuandoRaizFiltraPorCampoPermitido() {
        // Arrange
        var raiz = NodoFiltro.predicado("estadoRevision", FiltroOperador.ES, "EN_PROGRESO");

        // Act
        var criteria = RevisionItemEstudianteCriteria.builder()
                .pagina(0).tamanio(10)
                .raiz(raiz)
                .build();

        // Assert
        assertThat(criteria.getRaiz()).isEqualTo(raiz);
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");
        var builder = RevisionItemEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("item", SortDirection.ASC));
        var builder = RevisionItemEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }
}
