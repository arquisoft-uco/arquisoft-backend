package com.arquisoft.artefactos.application.artefacto.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoSinAvanceCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        for (var clave : List.of("proyectoGrado", "nombreProyecto", "versionRepositorioArtefacto",
                "nombreDocumento", "versionDocumento", "nombreRepositorio", "estadoUltimaRevision")) {
            assertThat(ArtefactoSinAvanceCriteria.Campo.esValidoParaFiltrar(clave)).isTrue();
        }
        for (var clave : List.of("nombreProyecto", "nombreDocumento", "versionDocumento", "nombreRepositorio",
                "estadoUltimaRevision", "fechaUltimaVersion")) {
            assertThat(ArtefactoSinAvanceCriteria.Campo.esValidoParaOrdenar(clave)).isTrue();
        }
        assertThat(ArtefactoSinAvanceCriteria.Campo.esValidoParaFiltrar("fechaUltimaVersion")).isFalse();
        assertThat(ArtefactoSinAvanceCriteria.Campo.esValidoParaOrdenar("proyectoGrado")).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoFiltrable() {
        // Arrange
        var raiz = NodoFiltro.predicado("fechaUltimaVersion", FiltroOperador.ES, "2026-01-01");
        var builder = ArtefactoSinAvanceCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeAceptarOrden_cuandoEsFechaUltimaVersion() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("fechaUltimaVersion", SortDirection.ASC));

        // Act
        var criteria = ArtefactoSinAvanceCriteria.builder().pagina(0).tamanio(10)
                .ordenamiento(ordenamiento).build();

        // Assert
        assertThat(criteria.getOrdenamiento()).hasSize(1);
    }
}
