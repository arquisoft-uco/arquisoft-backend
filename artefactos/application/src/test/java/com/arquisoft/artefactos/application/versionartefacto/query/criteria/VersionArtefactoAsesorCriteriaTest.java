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

class VersionArtefactoAsesorCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaFiltrar("artefacto")).isTrue();
        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaOrdenar("artefacto")).isFalse();

        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaFiltrar("version")).isTrue();
        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaOrdenar("version")).isTrue();

        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaFiltrar("fechaCreacion")).isTrue();
        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaOrdenar("fechaCreacion")).isTrue();

        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaFiltrar("asesorId")).isTrue();
        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaOrdenar("asesorId")).isFalse();

        assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaFiltrar("uriArchivo")).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("uriArchivo", FiltroOperador.ES, "valor");
        var builder = VersionArtefactoAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("artefacto", SortDirection.ASC));
        var builder = VersionArtefactoAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeDeclararFiltrableYNoOrdenable_proyectoGradoNombreProyectoNombreDocumento() {
        // Arrange
        var claves = List.of("proyectoGrado", "nombreProyecto", "nombreDocumento");

        // Act & Assert
        assertThat(claves).allSatisfy(clave -> {
            assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaFiltrar(clave)).isTrue();
            assertThat(VersionArtefactoAsesorCriteria.Campo.esValidoParaOrdenar(clave)).isFalse();
        });
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorNombreProyecto() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("nombreProyecto", SortDirection.ASC));
        var builder = VersionArtefactoAsesorCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }
}
