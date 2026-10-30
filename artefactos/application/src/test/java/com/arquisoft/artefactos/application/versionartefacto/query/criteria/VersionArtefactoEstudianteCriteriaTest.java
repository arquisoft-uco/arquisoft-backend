package com.arquisoft.artefactos.application.versionartefacto.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionArtefactoEstudianteCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar("artefacto")).isTrue();
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaOrdenar("artefacto")).isFalse();

        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar("version")).isTrue();
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaOrdenar("version")).isTrue();

        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar("fechaCreacion")).isTrue();
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaOrdenar("fechaCreacion")).isTrue();

        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar("estudianteId")).isTrue();
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaOrdenar("estudianteId")).isFalse();

        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar("uriArchivo")).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("uriArchivo", FiltroOperador.ES, "valor");
        var builder = VersionArtefactoEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("artefacto", SortDirection.ASC));
        var builder = VersionArtefactoEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeDeclararFiltrableYNoOrdenable_proyectoGradoNombreProyectoNombreDocumento() {
        // Arrange
        var claves = List.of("proyectoGrado", "nombreProyecto", "nombreDocumento");

        // Act & Assert
        assertThat(claves).allSatisfy(clave -> {
            assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar(clave)).isTrue();
            assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaOrdenar(clave)).isFalse();
        });
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorNombreProyecto() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("nombreProyecto", SortDirection.ASC));
        var builder = VersionArtefactoEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeDeclararFiltrableYOrdenable_estadoRevision() {
        // Arrange & Act & Assert
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaFiltrar("estadoRevision")).isTrue();
        assertThat(VersionArtefactoEstudianteCriteria.Campo.esValidoParaOrdenar("estadoRevision")).isTrue();
    }
}
