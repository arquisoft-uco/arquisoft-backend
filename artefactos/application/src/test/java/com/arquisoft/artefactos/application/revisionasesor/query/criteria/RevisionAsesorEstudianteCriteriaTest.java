package com.arquisoft.artefactos.application.revisionasesor.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RevisionAsesorEstudianteCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("versionArtefacto")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("versionArtefacto")).isFalse();

        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("estadoRevisionAsesor")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("estadoRevisionAsesor")).isTrue();

        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("estudianteId")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("estudianteId")).isFalse();

        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("campoInexistente", FiltroOperador.ES, "valor");
        var builder = RevisionAsesorEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("versionArtefacto", SortDirection.ASC));
        var builder = RevisionAsesorEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeDeclararFiltrableYNoOrdenable_cuandoCampoEsArtefacto() {
        // Arrange & Act & Assert
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("artefacto")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("artefacto")).isFalse();
    }

    @Test
    void debeDeclararFiltrableYOrdenable_cuandoCampoEsFechaCreacion() {
        // Arrange & Act & Assert
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("fechaCreacion")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("fechaCreacion")).isTrue();
    }

    @Test
    void debeDeclararFiltrableYNoOrdenable_cuandoCampoEsNombreDocumento() {
        // Arrange & Act & Assert
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("nombreDocumento")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("nombreDocumento")).isFalse();
    }

    @Test
    void debeRechazarFiltroPorVersion_cuandoSoloEsOrdenable() {
        // Arrange
        var raiz = NodoFiltro.predicado("version", FiltroOperador.ES, "1");
        var builder = RevisionAsesorEstudianteCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaOrdenar("version")).isTrue();
        assertThat(RevisionAsesorEstudianteCriteria.Campo.esValidoParaFiltrar("version")).isFalse();
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }
}
