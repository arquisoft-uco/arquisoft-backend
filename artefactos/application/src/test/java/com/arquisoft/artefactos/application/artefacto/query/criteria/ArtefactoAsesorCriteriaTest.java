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

class ArtefactoAsesorCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        for (var clave : List.of("proyectoGrado", "nombreProyecto", "versionRepositorioArtefacto",
                "nombreDocumento", "versionDocumento", "nombreRepositorio", "asesorId")) {
            assertThat(ArtefactoAsesorCriteria.Campo.esValidoParaFiltrar(clave)).isTrue();
        }
        for (var clave : List.of("nombreProyecto", "nombreDocumento", "versionDocumento", "nombreRepositorio")) {
            assertThat(ArtefactoAsesorCriteria.Campo.esValidoParaOrdenar(clave)).isTrue();
        }
        for (var clave : List.of("proyectoGrado", "versionRepositorioArtefacto", "asesorId")) {
            assertThat(ArtefactoAsesorCriteria.Campo.esValidoParaOrdenar(clave)).isFalse();
        }
        assertThat(ArtefactoAsesorCriteria.Campo.esValidoParaFiltrar("uriArchivo")).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("uriArchivo", FiltroOperador.ES, "valor");
        var builder = ArtefactoAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("proyectoGrado", SortDirection.ASC));
        var builder = ArtefactoAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }
}
