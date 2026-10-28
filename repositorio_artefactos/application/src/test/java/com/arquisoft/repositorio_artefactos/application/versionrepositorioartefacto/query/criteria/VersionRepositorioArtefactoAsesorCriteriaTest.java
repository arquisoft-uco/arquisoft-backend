package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioArtefactoAsesorCriteriaTest {

    @Test
    void debeAceptarFiltro_cuandoFiltraPorCadaClaveFiltrable() {
        for (var campo : VersionRepositorioArtefactoAsesorCriteria.Campo.values()) {
            // Arrange
            var raiz = NodoFiltro.predicado(campo.getClave(), FiltroOperador.ES, "x");

            // Act
            var criteria = VersionRepositorioArtefactoAsesorCriteria.builder().raiz(raiz).build();

            // Assert
            assertThat(criteria.tieneFiltros()).as(campo.getClave()).isTrue();
        }
    }

    @Test
    void debeLanzarFiltroException_cuandoElCampoNoEsFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("uriArchivo", FiltroOperador.ES, "x");
        var builder = VersionRepositorioArtefactoAsesorCriteria.builder();

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz).build()).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeAceptarOrden_cuandoOrdenaPorCadaClaveOrdenable() {
        for (var clave : List.of("nombreDocumento", "numeroVersion", "fechaCreacion", "nombreRepositorio")) {
            // Arrange
            var orden = List.of(SortOrder.of(clave, SortDirection.DESC));

            // Act
            var criteria = VersionRepositorioArtefactoAsesorCriteria.builder().ordenamiento(orden).build();

            // Assert
            assertThat(criteria.tieneOrden()).as(clave).isTrue();
        }
    }

    @Test
    void debeLanzarFiltroException_cuandoElCampoNoEsOrdenable() {
        // Arrange
        var orden = List.of(SortOrder.of("tipoContenido", SortDirection.ASC));
        var builder = VersionRepositorioArtefactoAsesorCriteria.builder();

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(orden).build()).isInstanceOf(FiltroException.class);
    }
}
