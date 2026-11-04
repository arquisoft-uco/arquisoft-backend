package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositorioArtefactoCriteriaTest {

    @Test
    void debeAceptarFiltro_cuandoFiltraPorCadaClaveFiltrable() {
        for (var campo : RepositorioArtefactoCriteria.Campo.values()) {
            // Arrange
            var raiz = NodoFiltro.predicado(campo.getClave(), FiltroOperador.ES, "x");

            // Act
            var criteria = RepositorioArtefactoCriteria.builder().raiz(raiz).build();

            // Assert
            assertThat(criteria.tieneFiltros()).as(campo.getClave()).isTrue();
        }
    }

    @Test
    void debeLanzarFiltroException_cuandoElCampoNoEsFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInventado", FiltroOperador.ES, "x");
        var builder = RepositorioArtefactoCriteria.builder();

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz).build()).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoElCampoNoEsOrdenable() {
        // Arrange
        var orden = List.of(SortOrder.of("administradorId", SortDirection.ASC));
        var builder = RepositorioArtefactoCriteria.builder();

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden).build()).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeAceptarOrden_cuandoElCampoEsOrdenable() {
        // Arrange
        var orden = List.of(SortOrder.of("administradorNombre", SortDirection.DESC));

        // Act
        var criteria = RepositorioArtefactoCriteria.builder().ordenamiento(orden).build();

        // Assert
        assertThat(criteria.tieneOrden()).isTrue();
    }
}
